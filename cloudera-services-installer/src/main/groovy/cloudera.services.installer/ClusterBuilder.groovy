package cloudera.services.installer

import com.cloudera.api.ApiRootResource
import com.cloudera.api.model.ApiCluster
import com.cloudera.api.model.ApiClusterList
import com.cloudera.api.model.ApiParcel
import com.cloudera.api.model.ApiParcelList
import com.cloudera.api.model.ApiService

/**
 * User: sergey.sheypak
 * Date: 07.12.13
 * Time: 14:42
 */
class ClusterBuilder {
    ApiRootResource apiRoot
    def json

    def createCluster(){
        apiRoot.getRootV4().clustersResource.createClusters(new ApiClusterList([new ApiCluster(name: json.cluster.name)]))
    }

    def setupParcels(){
        def apiParcelList = new ApiClusterList()
    }
}
