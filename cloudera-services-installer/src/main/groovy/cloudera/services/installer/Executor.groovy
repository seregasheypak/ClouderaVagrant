package cloudera.services.installer

import cloudera.services.installer.model.MapReduce
import com.cloudera.api.ClouderaManagerClientBuilder
import com.cloudera.api.DataView
import com.cloudera.api.model.ApiHostList
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


    def configureScm(){
        root.clouderaManagerResource.updateConfig(new ScmConf().build())
        LOG.info 'scm config has been updated'
        this
    }

    def activateParcels(){
        new ParcelActivator(products: ScmConf.PRODUCTS, root: root, clusterName: Cluster.name).activate()
        this
    }

    def stopCluster(){
        def delCommand = root.clustersResource.stopCommand(Cluster.name)

        println delCommand

        println 'status: ' + root.commandsResource.readCommand(delCommand.id)
        this
    }

    def deleteCluster(){
        root.clustersResource.deleteCluster(Cluster.name)
        LOG.info "Cluster: $Cluster.name has been deleted"
        this
    }

    def createCluster(){
        def cluster = new Cluster()
        if(root.clustersResource
                       .readClusters(DataView.EXPORT)
                       .find{existingCluster -> existingCluster.name == cluster.name} == null){

            root.clustersResource.createClusters(cluster.build())
            LOG.info "Cluster with name: $cluster.name has been created"
        }else{
            LOG.info "Cluster with name: $cluster.name already exisits"
        }
        this
    }

    def addHosts(){
        ApiHostList existingHosts = root.getHostsResource().readHosts(DataView.EXPORT)
        def newHosts = new Hosts().build()
        newHosts.hosts.removeAll{ newHost ->
            existingHosts.find{ existingHost -> existingHost.hostId == newHost.hostId }
        }


        if(!newHosts.hosts.isEmpty()){
            LOG.info "Creating hosts: " + newHosts.hosts.collect{it.hostId}.join(' ')
            root.hostsResource.createHosts(newHosts)
        }else{
            LOG.info 'All hosts are registered'
        }

        println 'root.clustersResource.listHosts(Cluster.name)::: ' + root.clustersResource.listHosts(Cluster.name)
        def hosts = root.clustersResource.addHosts(Cluster.name, new ClusterHosts().build())
        println hosts
        LOG.info 'Hosts have been added to cluster'
        this
    }

    def createHDFS(){
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new HDFS().build())
        LOG.info 'HDFS service has been created'
        this
    }

    def createMapReduce(){
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new MapReduce().build())
        LOG.info 'MapReduce service has been created'
        this
    }




    def createRoot(){
        new ClouderaManagerClientBuilder()
                    .withHost(System.getProperty('scm.host', '10.211.55.101'))
                    .withUsernamePassword(System.getProperty('scm.username', 'admin'),
                                          System.getProperty('scm.password', 'admin'))
                    .build()
                    .getRootV5()
    }
}
