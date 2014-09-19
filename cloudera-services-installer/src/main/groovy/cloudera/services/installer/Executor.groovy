package cloudera.services.installer

import com.cloudera.api.ClouderaManagerClientBuilder
import com.cloudera.api.DataView
import com.cloudera.api.model.ApiCluster
import com.cloudera.api.model.ApiClusterList
import com.cloudera.api.model.ApiCommand
import com.cloudera.api.model.ApiCommandList
import com.cloudera.api.model.ApiConfig
import com.cloudera.api.model.ApiConfigList
import com.cloudera.api.model.ApiHost
import com.cloudera.api.model.ApiHostList
import com.cloudera.api.model.ApiHostRef
import com.cloudera.api.model.ApiHostRefList
import com.cloudera.api.model.ApiRole

import com.cloudera.api.model.ApiRoleNameList
import com.cloudera.api.model.ApiService
import com.cloudera.api.model.ApiServiceList
import com.cloudera.api.v4.ServicesResourceV4
import com.cloudera.api.v5.RootResourceV5

import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 13:57
 */
class Executor {

    private static final Logger LOG = LoggerFactory.getLogger(Executor.class)

    private final RootResourceV5 root
    private final ServicesResourceV4 servicesResourceV4
    private final Map yamlConfig
    private final String clusterName
    private final ServiceYamlBuilder serviceYamlBuilder

    public Executor(Map yamlConfig) {
        this.yamlConfig = yamlConfig
        this.root = new ClouderaManagerClientBuilder()
                .withHost(yamlConfig['scm']['host']['ip'])
                .withUsernamePassword(yamlConfig['scm']['username'], yamlConfig['scm']['password'])
                .build()
                .getRootV5()

        this.clusterName = yamlConfig['scm']['cluster_name']
        servicesResourceV4 = root.clustersResource.getServicesResource(clusterName)
        this.serviceYamlBuilder = new ServiceYamlBuilder(yamlConfig)
    }

    def configureScm() {
        ApiConfigList apiConfigList = new ApiConfigList()
        apiConfigList.add(new ApiConfig(name: 'remote_parcel_repo_urls', value: yamlConfig['parcel_repos'].join(',')))
        apiConfigList.add(new ApiConfig(name: 'distribute_parcels_automatically', value: 'false'))
        apiConfigList.add(new ApiConfig(name: 'download_parcels_automatically', value: 'false'))
        root.clouderaManagerResource.updateConfig(apiConfigList)
        LOG.info 'scm config has been updated'
        this
    }

    def activateParcels() {
        new ParcelActivator(clusterName, root, yamlConfig['parcels']).activate()
        this
    }

    def stopCluster() {
        def delCommand = root.clustersResource.stopCommand(clusterName)

        println delCommand

        println 'status: ' + root.commandsResource.readCommand(delCommand.id)
        this
    }

    def deleteCluster() {
        root.clustersResource.deleteCluster(clusterName)
        LOG.info "Cluster: $clusterName has been deleted"
        this
    }

    def createCluster() {
        String clusterVersion = yamlConfig['scm']['cluster_version']

        ApiClusterList apiClusterList = new ApiClusterList()
        apiClusterList.add(new ApiCluster(name: clusterName, version: clusterVersion))
        if (root.clustersResource
                .readClusters(DataView.EXPORT)
                .find { existingCluster -> existingCluster.name == clusterName } == null) {

            root.clustersResource.createClusters(apiClusterList)
            LOG.info "Cluster with name: $clusterName has been created"
        } else {
            LOG.info "Cluster with name: $clusterName already exisits"
        }
        this
    }

    def addHosts() {
        ApiHostList existingHosts = root.getHostsResource().readHosts(DataView.EXPORT)
        ApiHostList newHosts = new ApiHostList()
        ApiHostRefList clusterHosts = new ApiHostRefList()
        yamlConfig['hosts'].each {
            newHosts.add(new ApiHost(hostId: it.name, hostname: it.name, ipAddress: it.ip))
            clusterHosts.add(new ApiHostRef(hostId: it.name))
        }



        for (ApiHost existingHost : existingHosts.getHosts()) {
            Iterator<ApiHost> newHostsIt = newHosts.getHosts().iterator()
            while (newHostsIt.hasNext()) {
                ApiHost current = newHostsIt.next()
                if (current.getHostId().equals(existingHost.getHostId())) {
                    newHostsIt.remove()
                    LOG.info("Host " + existingHost.getHostId() + " already exists, removing")
                }
            }
        }


        if (!newHosts.hosts.isEmpty()) {
            LOG.info "Creating hosts: " + newHosts.hosts.collect { it.hostId }.join(' ')
            root.hostsResource.createHosts(newHosts)
        } else {
            LOG.info 'All hosts are registered'
        }

        LOG.info 'root.clustersResource.listHosts(Cluster.name)::: ' + root.clustersResource.listHosts(clusterName)


        def hosts = root.clustersResource.addHosts(clusterName, clusterHosts)
        LOG.info hosts.toString()
        LOG.info 'Hosts have been added to cluster'
        this
    }

    def createHDFS() {
        LOG.info 'creating hdfs'
        ApiServiceList apiServiceList = serviceYamlBuilder.buildService('HDFS')

        servicesResourceV4.createServices(apiServiceList)
        LOG.info 'HDFS service has been created'
        sleep(10000)

        ApiService hdfsService = apiServiceList.services.get(0)
        ApiRole nameNode = hdfsService.roles.find {
            apiRole ->
                apiRole.type.equals('NAMENODE')
        }
        ApiRoleNameList apiRoleNameList = new ApiRoleNameList();
        apiRoleNameList.setRoleNames([nameNode.name])
        waitCommandExecuted(servicesResourceV4.getRoleCommandsResource(hdfsService.displayName).formatCommand(apiRoleNameList))
        waitCommandExecuted(servicesResourceV4.startCommand(hdfsService.displayName))
        this
    }

    def createMapReduce() {
        ApiServiceList apiServiceList = serviceYamlBuilder.buildService('MAPREDUCE')
        servicesResourceV4.createServices(apiServiceList)
        LOG.info 'MapReduce service has been created'
        sleep(1000)
        waitCommandExecuted(servicesResourceV4.startCommand(apiServiceList.get(0).displayName))
        this
    }


    def createOozie() {
        ApiServiceList apiServiceList = serviceYamlBuilder.buildService('OOZIE')
        servicesResourceV4.createServices(apiServiceList)
        sleep(1000)
        waitCommandExecuted(servicesResourceV4.startCommand(apiServiceList.get(0).displayName))
        LOG.info 'Oozie service has been created'
        this
    }

    def createHive() {
        ApiServiceList apiServiceList = serviceYamlBuilder.buildService('HIVE')
        servicesResourceV4.createServices(apiServiceList)
        LOG.info 'Hive service has been created'
        sleep(1000)
        waitCommandExecuted(servicesResourceV4.hiveCreateMetastoreDatabaseCommand(apiServiceList.get(0).displayName))
        waitCommandExecuted(servicesResourceV4.createHiveUserDirCommand(apiServiceList.get(0).displayName))
        waitCommandExecuted(servicesResourceV4.startCommand(apiServiceList.get(0).displayName))
        this
    }

    def createHBase() {
        ApiServiceList apiServiceList = serviceYamlBuilder.buildService('HBASE')
        servicesResourceV4.createServices(apiServiceList)
        LOG.info 'HBase service has been created'
        sleep(1000)
        waitCommandExecuted(servicesResourceV4.createHBaseRootCommand(apiServiceList.get(0).displayName))
        waitCommandExecuted(servicesResourceV4.startCommand(apiServiceList.get(0).displayName))
        this
    }

    def createZookeeper() {
        ApiServiceList apiServiceList = serviceYamlBuilder.buildService('ZOOKEEPER')
        servicesResourceV4.createServices(apiServiceList)
        LOG.info 'Zookeeper service has been created'
        sleep(1000)
        waitCommandExecuted(servicesResourceV4.startCommand(apiServiceList.get(0).displayName))
        this
    }

    def createSqoop() {
        ApiServiceList apiServiceList = serviceYamlBuilder.buildService('SQOOP')
        servicesResourceV4.createServices(apiServiceList)
        LOG.info 'Sqoop service has been created'
        sleep(1000)
        waitCommandExecuted(servicesResourceV4.startCommand(apiServiceList.get(0).displayName))
        this
    }

    def createImpala() {
        ApiServiceList apiServiceList = serviceYamlBuilder.buildService('IMPALA')
        servicesResourceV4.createServices(apiServiceList)
        LOG.info 'Impala service has been created'
        sleep(1000)
        waitCommandExecuted(servicesResourceV4.startCommand(apiServiceList.get(0).displayName))
        this
    }


    def deployClusterWideClientsConfig() {
        LOG.info "Deploy cluster wide configuration "
        waitCommandExecuted(root.clustersResource.deployClientConfig(clusterName))
        this
    }


    void waitCommandExecuted(ApiCommandList list, long timeout = 5 * 60 * 1000) {
        List<ApiCommand> commands = list.getCommands();
        for (ApiCommand command : commands) {
            waitCommandExecuted(command, timeout);
        }
    }

    //default timeout = 5 minutes
    void waitCommandExecuted(ApiCommand command, long timeout = 5 * 60 * 1000) {
        long tenSeconds = 10000;
        long waitingTime = 0;
        while (true) {
            if (root.commandsResource.readCommand(command.getId()).endTime == null) {
                if (waitingTime > timeout) {
                    throw new RuntimeException("Timeout while executing command " + command.properties);
                }
                waitingTime += tenSeconds
                sleep(tenSeconds)
            } else {
                ApiCommand commandReturn = root.commandsResource.readCommand(command.getId())
                LOG.info "Command success: " + commandReturn.getSuccess() + " " + commandReturn
                break;
            }
        }

    }
}
