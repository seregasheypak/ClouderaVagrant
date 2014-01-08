package cloudera.services.installer.model

import com.cloudera.api.model.ApiHostRef
import com.cloudera.api.model.ApiHostRefList

/**
 * User: sergey.sheypak
 * Date: 08.01.14
 * Time: 12:53
 */
class ClusterHosts extends Hosts{

    def build(){
        new ApiHostRefList(hosts.collect{ new ApiHostRef(hostId: it.hostname)})
    }
}
