package cloudera.services.installer.model

import com.cloudera.api.model.ApiHost
import com.cloudera.api.model.ApiHostList
import com.cloudera.api.model.ApiHostRef


/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 14:32
 */
class Hosts implements BuiltModel{

    public static final String HOST_01 = 'vm-cluster-node1.localdomain'
    public static final String HOST_02 = 'vm-cluster-node2.localdomain'
    public static final String HOST_03 = 'vm-cluster-node3.localdomain'

    public static final HOSTS = [
                    [hostname: HOST_01, ipAddress:'10.211.55.101'],
                    [hostname: HOST_02, ipAddress:'10.211.55.102'],
                    [hostname: HOST_03, ipAddress:'10.211.55.103']
                ]

    def build(){
        new ApiHostList(HOSTS.collect{ new ApiHost(hostId: it.hostname, hostname: it.hostname, ipAddress: it.ipAddress)})
    }
}
