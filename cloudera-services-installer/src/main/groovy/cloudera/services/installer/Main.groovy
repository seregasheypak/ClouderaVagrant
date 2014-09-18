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

    public static void main(String... args) {
        String yamlLocation = System.getProperty('yaml.location', 'configuration.yaml')
        Yaml yaml = new Yaml();
        Map yamlConfig = (Map) yaml.load(new File(yamlLocation).text)
        LOG.info("read config")
        LOG.info(yamlConfig.toString())


        try {
            Executor executor = new Executor(yamlConfig)
            String installType = yamlConfig['install']

            if (installType.equalsIgnoreCase('all')) {
                LOG.info 'initiating cluster'
                executor
                        .configureScm()
                        .createCluster()
                        .addHosts()
                        .activateParcels()
            }

            if(installType.equalsIgnoreCase('all') || installType.equalsIgnoreCase('services')){
                LOG.info('activating services')
                executor
                        .createHDFS()
                        .createMapReduce()
                        .createOozie()
                        .createZookeeper()
                        .createHBase()
                        .createHive()
                        .createImpala()
                        .createSqoop()
                //.deployClusterWideClientsConfig()
            }

        } catch (Exception ex) {

            ex.printStackTrace()
        }

        Thread.sleep(10000);

    }

}
