package cloudera.services.installer

import com.cloudera.api.ClouderaManagerClientBuilder

/**
 * User: sergey.sheypak
 * Date: 07.12.13
 * Time: 2:49
 */
class ClouderaClient {

    String host
    Integer port

    String user
    String password

    def connect(){
        new ClouderaManagerClientBuilder()
                .withHost(host)
                .withPort(port)
        .withUsernamePassword(user,password)
        .build()
    }

}
