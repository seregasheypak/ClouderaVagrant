package cloudera.services.installer.model

import com.cloudera.api.model.ApiCluster
import com.cloudera.api.model.ApiClusterList

/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 13:53
 */
class Cluster implements BuiltModel{

    String name = System.getProperty('cluster.name', 'cloudera-cluster')
    String version = System.getProperty('cluster.version', 'CDH4')

    def build(){
        def apiClusterList = new ApiClusterList()
        apiClusterList.add(new ApiCluster(name: name, version: version))
        apiClusterList
    }
}
