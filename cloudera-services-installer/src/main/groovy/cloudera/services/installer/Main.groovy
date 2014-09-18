package cloudera.services.installer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml

import javax.ws.rs.BadRequestException

/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 13:46
 */
class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class)

    public static void main(String... args){
        String yamlLocation = System.getProperty('yaml.location', 'configuration.yaml')
        Yaml yaml = new Yaml();
        Map yamlConfig = (Map) yaml.load(new File(yamlLocation).text)
        LOG.info("read config")
        LOG.info(yamlConfig.toString())


        try{
            new Executor(yamlConfig)
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
                    //.deployClusterWideClientsConfig()
        }catch(BadRequestException ex){

            ex.printStackTrace()
        }

        Thread.sleep(10000);

    }

}
