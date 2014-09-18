package cloudera.services.installer.model

import cloudera.services.installer.ServiceYamlBuilder
import com.cloudera.api.model.ApiService
import com.cloudera.api.model.ApiServiceList
import org.testng.Assert
import org.testng.annotations.Test
import org.yaml.snakeyaml.Yaml

class ServiceYamlBuilderTest {
    private static final TEST_DATA = 'test-data/'
    private Yaml yaml = new Yaml()

    @Test
    public void buildService(){
        Map yamlConfig = (Map) yaml.load(new File(TEST_DATA, 'configuration.yaml').text)
        ServiceYamlBuilder builder = new ServiceYamlBuilder(yamlConfig)
        ApiServiceList service = builder.buildService('HDFS')
        ApiService hdfs = service.get(0)
        Assert.assertEquals(hdfs.name, 'HDFS01')
        Assert.assertEquals(hdfs.roleConfigGroups.size(), 4)
    }
}
