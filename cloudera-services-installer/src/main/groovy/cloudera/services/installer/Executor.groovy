package cloudera.services.installer

import cloudera.services.installer.model.HBase
import cloudera.services.installer.model.Hive
import cloudera.services.installer.model.Hue
import cloudera.services.installer.model.Impala
import cloudera.services.installer.model.MapReduce
import cloudera.services.installer.model.Oozie
import cloudera.services.installer.model.Sqoop
import cloudera.services.installer.model.Zookeeper
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
import com.cloudera.api.model.ApiRoleNameList
import com.cloudera.api.v4.ServicesResourceV4
import com.cloudera.api.v5.RootResourceV5

import cloudera.services.installer.model.Cluster
import cloudera.services.installer.model.ScmConf
import cloudera.services.installer.model.Hosts
import cloudera.services.installer.model.HDFS
import cloudera.services.installer.utility.ParcelActivator
import cloudera.services.installer.model.ClusterHosts
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
    private final Map yamlConfig

    public Executor(Map yamlConfig) {
        this.yamlConfig = yamlConfig
        root = new ClouderaManagerClientBuilder()
                .withHost(yamlConfig['scm']['host'])
                .withUsernamePassword(yamlConfig['scm']['username'], yamlConfig['scm']['password'])
                .build()
                .getRootV5()
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
        new ParcelActivator(products: ScmConf.PRODUCTS, root: root, clusterName: Cluster.name).activate()
        this
    }

    def stopCluster() {
        def delCommand = root.clustersResource.stopCommand(Cluster.name)

        println delCommand

        println 'status: ' + root.commandsResource.readCommand(delCommand.id)
        this
    }

    def deleteCluster() {
        root.clustersResource.deleteCluster(Cluster.name)
        LOG.info "Cluster: $Cluster.name has been deleted"
        this
    }

    def createCluster() {
        String clusterName = yamlConfig['scm']['cluster_name']
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

        String clusterName = yamlConfig['scm']['cluster_name']
        LOG.info 'root.clustersResource.listHosts(Cluster.name)::: ' + root.clustersResource.listHosts(clusterName)


        def hosts = root.clustersResource.addHosts(clusterName, clusterHosts)
        LOG.info hosts
        LOG.info 'Hosts have been added to cluster'
        this
    }

    def createHDFS() {
        ServicesResourceV4 resource = root.clustersResource.getServicesResource(new Cluster().name)
        resource.createServices(new HDFS().build())
        LOG.info 'HDFS service has been created'
        sleep(10000)

        ApiRoleNameList apiRoleNameList = new ApiRoleNameList();
        apiRoleNameList.setRoleNames([HDFS.NAMENODE + "-${Hosts.asRoleNameSuffix(Hosts.getInstance().HOST_03)}"])
        waitCommandExecuted(resource.getRoleCommandsResource(HDFS.SERVICE_NAME).formatCommand(apiRoleNameList))
        waitCommandExecuted(resource.startCommand(HDFS.SERVICE_NAME))
        this
    }

    def createMapReduce() {
        ServicesResourceV4 resource = root.clustersResource.getServicesResource(new Cluster().name)
        resource.createServices(new MapReduce().build())
        LOG.info 'MapReduce service has been created'
        sleep(1000)
        waitCommandExecuted(resource.startCommand(MapReduce.SERVICE_NAME))
        this
    }


    def createOozie() {
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new Oozie().build())
        LOG.info 'Oozie service has been created'
        this
    }

    def createHive() {
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new Hive().build())
        LOG.info 'Hive service has been created'
        this
    }

    def createHBase() {
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new HBase().build())
        LOG.info 'HBase service has been created'
        this
    }

    def createZookeeper() {
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new Zookeeper().build())
        LOG.info 'Zookeeper service has been created'
        this
    }

    def createSqoop() {
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new Sqoop().build())
        LOG.info 'Sqoop service has been created'
        this
    }

    def createImpala() {
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new Impala().build())
        LOG.info 'Impala service has been created'
        this
    }

    def createHue() {
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new Hue().build())
        LOG.info 'Hue service has been created'
        this
    }


    def deployClusterWideClientsConfig() {
        LOG.info "Deploy cluster wide configuration "
        waitCommandExecuted(root.clustersResource.deployClientConfig(Cluster.name))
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
