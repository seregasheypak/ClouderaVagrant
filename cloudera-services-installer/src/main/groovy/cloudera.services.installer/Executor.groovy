package cloudera.services.installer

import com.cloudera.api.ClouderaManagerClientBuilder
import com.cloudera.api.DataView
import com.cloudera.api.model.ApiHostList
import com.cloudera.api.v5.RootResourceV5

import cloudera.services.installer.model.Cluster
import cloudera.services.installer.model.ScmConf
import cloudera.services.installer.model.Hosts
import cloudera.services.installer.model.HDFS
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 13:57
 */
class Executor {

    private static final Logger LOG = LoggerFactory.getLogger(Executor.class)
    public static final long SECOND = 1000L

    private final RootResourceV5 root = createRoot()


    def configureScm(){
        root.clouderaManagerResource.updateConfig(new ScmConf().build())
        LOG.info 'scm config has been updated'
        this
    }

    def waitForParcelActivation(){
        ScmConf.PRODUCTS.each {product ->
            println "Starting download a product: $product"
            def apiCommand = root.getClustersResource().getParcelsResource(Cluster.name).getParcelResource(product.name, product.version).startDownloadCommand()
            println apiCommand
        }

//        root.getClustersResource().getParcelsResource(Cluster.name).getParcelResource(ScmConf.CDH.name, ScmConf.CDH.version).startDownloadCommand()
//        root.getClustersResource().getParcelsResource(Cluster.name).getParcelResource(ScmConf.IMPALA.name, ScmConf.IMPALA.version).startDownloadCommand()

//        def waitLimit = System.getProperty('parcel.activation.wait.limit.sec','600') as Integer
//        (0..waitLimit).each{ counter ->
//            sleep(SECOND)
//            if(counter % 10 == 0){
//                new ScmConf().products.each {product ->
//                    println "Inspecting product status: $product and culster: ${new Cluster().name}"
//
//                    println root.clustersResource.getParcelsResource(new Cluster().name).parcelUsage
//                    //NOT FOUND! Exception
//                    //println root.clustersResource.getParcelsResource(new Cluster().name).getParcelResource(product.name,product.version).readParcel().state
//
//                    //NOTHING
//                    //println root.clouderaManagerResource.listActiveCommands(DataView.EXPORT)
//
//                    //NOTHING
//                    //println root.clustersResource.getParcelsResource(new Cluster().name).parcelUsage.parcels
//
//                    //???
//                }
//            }
//        }
//        this
    }


    def deleteCluster(){
        def clusterName = new Cluster().name
        root.clustersResource.deleteCluster(clusterName)
        LOG.info "Cluster: $clusterName has been deleted"
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
            LOG.info 'All hosts are in cluster'
        }
        this
    }

    def createHDFS(){
        root.clustersResource.getServicesResource(new Cluster().name).createServices(new HDFS().build())
        LOG.info 'HDFS service has been created'
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
