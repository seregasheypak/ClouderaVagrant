package cloudera.services.installer

import com.cloudera.api.ClouderaManagerClientBuilder
import com.cloudera.api.DataView
import com.cloudera.api.v5.RootResourceV5

import cloudera.services.installer.model.Cluster
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

    def createCluster(){
        def cluster = new Cluster()
        if(root.clustersResource
                       .readClusters(DataView.EXPORT)
                       .find{existingCluster -> existingCluster.name == cluster.name} == null){

            root.clustersResource.createClusters(new Cluster().build())
            LOG.info "Cluster with name: $cluster.name has been created"
        }else{
            LOG.info "Cluster with name: $cluster.name already exisits"
        }
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
