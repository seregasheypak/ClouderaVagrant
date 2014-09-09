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
import com.cloudera.api.model.ApiCommand
import com.cloudera.api.model.ApiHost
import com.cloudera.api.model.ApiHostList
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

    private final RootResourceV5 root = createRoot()


    def configureScm() {
        root.clouderaManagerResource.updateConfig(new ScmConf().build())
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
        def cluster = new Cluster()
        if (root.clustersResource
                .readClusters(DataView.EXPORT)
                .find { existingCluster -> existingCluster.name == cluster.name } == null) {

            root.clustersResource.createClusters(cluster.build())
            LOG.info "Cluster with name: $cluster.name has been created"
        } else {
            LOG.info "Cluster with name: $cluster.name already exisits"
        }
        this
    }

    def addHosts() {
        ApiHostList existingHosts = root.getHostsResource().readHosts(DataView.EXPORT)
        def newHosts = Hosts.getInstance().build()

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

        println 'root.clustersResource.listHosts(Cluster.name)::: ' + root.clustersResource.listHosts(Cluster.name)
        def hosts = root.clustersResource.addHosts(Cluster.name, new ClusterHosts().build())
        println hosts
        LOG.info 'Hosts have been added to cluster'
        this
    }

    def createHDFS() {
        ServicesResourceV4 resource = root.clustersResource.getServicesResource(new Cluster().name)
        resource.createServices(new HDFS().build())
        LOG.info 'HDFS service has been created'
        LOG.info 'Deployin client configuration'
        sleep(5000)
        waitCommandExecuted(resource.deployClientConfigCommand(HDFS.SERVICE_NAME, new ApiRoleNameList()))
        LOG.info 'Deployin client configuration finished'
        this
    }

    def createMapReduce() {
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new MapReduce().build())
        LOG.info 'MapReduce service has been created'
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



    def createRoot() {
        new ClouderaManagerClientBuilder()
                .withHost(System.getProperty('scm.host', Hosts.getInstance().HOST_01))
                .withUsernamePassword(System.getProperty('scm.username', 'admin'),
                System.getProperty('scm.password', 'admin'))
                .build()
                .getRootV5()
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
