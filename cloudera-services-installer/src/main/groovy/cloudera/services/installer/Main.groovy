package cloudera.services.installer
/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 13:46
 */
class Main {

    public static void main(String... args){
        new Executor()
                    .configureScm()
                     .stopCluster()
                      .deleteCluster()
                      .createCluster()
                      .addHosts()
                      .activateParcels()
                      .createHDFS()
                      .createMapReduce()
                    .createZookeeper()
    }

}
