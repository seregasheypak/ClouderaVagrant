package cloudera.services.installer

import com.cloudera.api.ClouderaManagerClientBuilder
import com.cloudera.api.model.ApiCluster
import com.cloudera.api.model.ApiClusterList
import com.cloudera.api.v5.RootResourceV5

/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 13:46
 */
class Main {

    public static void main(String... args){
        new Executor().createCluster()
    }

}
