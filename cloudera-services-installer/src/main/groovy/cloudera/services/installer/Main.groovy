package cloudera.services.installer

import javax.ws.rs.BadRequestException

/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 13:46
 */
class Main {

    public static void main(String... args){

        try{
            new Executor()
                    .configureScm()
                    //.stopCluster()
                    //.deleteCluster()
                    .createCluster()
                    .addHosts()
                    .activateParcels()
                    .createHDFS()
                    .createMapReduce()
                    .createOozie()
                    .createZookeeper()
                    .createHBase()
                    .createHive()
                    .createImpala()
                    .createSqoop()
                    //.createHue()
                    //.deployClusterWideClientsConfig()
        }catch(BadRequestException ex){

            ex.printStackTrace()
        }

        Thread.sleep(10000);

    }

}
