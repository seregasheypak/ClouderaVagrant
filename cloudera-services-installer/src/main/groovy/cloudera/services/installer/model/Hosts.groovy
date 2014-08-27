package cloudera.services.installer.model

import com.cloudera.api.model.ApiHost
import com.cloudera.api.model.ApiHostList

/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 14:32
 */
class Hosts implements BuiltModel{

    public final HOSTS

    public final String HOST_01
    public final String HOST_02
    public final String HOST_03

    private static Hosts instance

    public static Hosts getInstance(){
        if(instance == null){
            instance = new Hosts()
        }

        return instance
    }

    private Hosts(){
        Properties prop = new Properties()
        prop.load(new FileReader('cloudera-installer.properties'))
        HOST_01 = prop.getProperty('host1.name')
        HOST_02 = prop.getProperty('host2.name')
        HOST_03 = prop.getProperty('host3.name')

        HOSTS = [
                [hostname: HOST_01, ipAddress: prop.getProperty('host1.ip')],
                [hostname: HOST_02, ipAddress: prop.getProperty('host2.ip')],
                [hostname: HOST_03, ipAddress: prop.getProperty('host3.ip')]
        ]
    }





    def build(){
        new ApiHostList(HOSTS.collect{ new ApiHost(hostId: it.hostname, hostname: it.hostname, ipAddress: it.ipAddress)})
    }

    static asRoleNameSuffix(String hostName){
        hostName.replace('.', '_').replace('-','')
    }
}
