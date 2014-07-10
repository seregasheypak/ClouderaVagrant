package cloudera.services.installer.model

import com.cloudera.api.model.ApiConfig
import com.cloudera.api.model.ApiConfigList
import com.cloudera.api.model.ApiHostRef
import com.cloudera.api.model.ApiRole
import com.cloudera.api.model.ApiRoleConfigGroup
import com.cloudera.api.model.ApiRoleConfigGroupRef
import com.cloudera.api.model.ApiService
import com.cloudera.api.model.ApiServiceConfig
import com.cloudera.api.model.ApiServiceList
import com.cloudera.api.model.ApiServiceRef

class Hue implements BuiltModel{

    public static final String SERVICE_TYPE_NAME = 'HUE'
    public static final String SERVICE_NAME = 'HUE01'

    public static final String HUE_SERVER = 'HUE_SERVER'
    public static final String BEESWAX_SERVER = 'BEESWAX_SERVER'

    @Override
    def build() {

        def hueService = new ApiService()
        hueService.displayName = SERVICE_NAME
        hueService.name = SERVICE_NAME
        hueService.type = SERVICE_TYPE_NAME
        hueService.config = createServiceWideConfig()


        def roleList = []


        //add HUE_SERVER
        roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: HueServerConfigGroup.NAME),
                hostRef: new ApiHostRef(hostId: Hosts.HOST_01),
                name: "$HUE_SERVER-${Hosts.asRoleNameSuffix(Hosts.HOST_01)}",
                type: HUE_SERVER
        )

        //add BEESWAX_SERVER
        roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: BeeswaxServerConfigGroup.NAME),
                hostRef: new ApiHostRef(hostId: Hosts.HOST_01),
                name: "$BEESWAX_SERVER-${Hosts.asRoleNameSuffix(Hosts.HOST_01)}",
                type: BEESWAX_SERVER
        )

        hueService.roleConfigGroups = [
                new HueServerConfigGroup().build(),
                new BeeswaxServerConfigGroup().build(),
        ]
        hueService.roles = roleList
        new ApiServiceList(services: [hueService])
    }

    ApiServiceConfig createServiceWideConfig() {
        def apiConfig = new ApiServiceConfig()
        apiConfig.add new ApiConfig(name: 'hbase_service', value: HBase.SERVICE_NAME)
        apiConfig.add new ApiConfig(name: 'hive_service', value: Hive.SERVICE_NAME)
        apiConfig.add new ApiConfig(name: 'hue_webhdfs', value: HDFS.NAMENODE)
        apiConfig.add new ApiConfig(name: 'impala_service', value: Impala.SERVICE_NAME)
        apiConfig.add new ApiConfig(name: 'oozie_service', value: Oozie.SERVICE_NAME)
        apiConfig.add new ApiConfig(name: 'sqoop_service', value: Sqoop.SERVICE_NAME)

        return apiConfig
    }

    static class HueServerConfigGroup implements BuiltModel{

        public static final String NAME = "$SERVICE_NAME-$BEESWAX_SERVER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = BEESWAX_SERVER
            configGroup.name = NAME
            configGroup.displayName = "$BEESWAX_SERVER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([
                    new ApiConfig(name: 'beeswax_server_heapsize', value: 236713196),
            ])
            return configGroup
        }
    }

    static class BeeswaxServerConfigGroup implements BuiltModel{
        public static final String NAME = "$SERVICE_NAME-$HUE_SERVER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = HUE_SERVER
            configGroup.name = NAME
            configGroup.displayName = "$HUE_SERVER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([])
            return configGroup
        }
    }
}
