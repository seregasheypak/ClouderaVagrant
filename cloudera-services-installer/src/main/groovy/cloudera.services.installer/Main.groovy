package cloudera.services.installer

import cloudera.services.installer.model.Cluster
import cloudera.services.installer.model.HDFS
/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 13:46
 */
class Main {

    public static void main(String... args){
        new Executor().configureScm()
                      .stopCluster()
                      .deleteCluster()
                      .createCluster()
                      .addHosts()
                      .activateParcels()
                      .createHDFS()
//        def root = new Executor().createRoot()
//        def hdfs =  root.clustersResource.getServicesResource(Cluster.name).readService('hdfs1')
//        println hdfs
//
//        def roles = root.clustersResource.getServicesResource(Cluster.name).getRolesResource('hdfs1').readRoles()
//        roles.each {
//            println 'role ::: ' + it
//        }



    }

}
