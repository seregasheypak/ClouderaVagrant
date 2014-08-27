package cloudera.services.installer.model

import com.cloudera.api.model.ApiHostRef
import com.cloudera.api.model.ApiHostRefList

/**
 * User: sergey.sheypak
 * Date: 08.01.14
 * Time: 12:53
 */
class ClusterHosts {

    def build(){
        new ApiHostRefList(Hosts.getInstance().HOSTS.collect{ new ApiHostRef(hostId: it.hostname)})
    }
}
