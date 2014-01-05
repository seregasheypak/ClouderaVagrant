package cloudera.services.installer
/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 13:46
 */
class Main {

    public static void main(String... args){
        new Executor().configureScm()
                      .deleteCluster()
                      .createCluster()
                      .addHosts()
                      .waitForParcelActivation()
                      //.createHDFS()

//            println new ClouderaManagerClientBuilder()
//                    .withHost(System.getProperty('scm.host', 'napoleon1.desert.ru'))
//                    .withUsernamePassword(System.getProperty('scm.username', 'ssa'),
//                    System.getProperty('scm.password', 'shu3EoXo'))
//                    .build()
//                    .getRootV5().clustersResource.getServicesResource('Cluster 1 - CDH4').readServices(DataView.EXPORT)
    }

}
