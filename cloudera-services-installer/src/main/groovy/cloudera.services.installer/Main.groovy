package cloudera.services.installer
/**
 * User: sergey.sheypak
 * Date: 07.12.13
 * Time: 2:41
 */
class Main {

    public static void main(String... args){
        def client = new ClouderaClient(host: sysProp('cloudera.host'),
                                        port: sysProp('cloudera.port') as Integer,
                                        user: sysProp('cloudera.user'),
                                        password: sysProp('cloudera.password'))
    }

    static def sysProp(String name){
        System.getProperty(name)
    }

}


