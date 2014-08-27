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

class Sqoop implements BuiltModel {

    public static final String SERVICE_TYPE_NAME = 'SQOOP'
    public static final String SERVICE_NAME = 'SQOOP01'

    public static final String SQOOP_SERVER = 'SQOOP_SERVER'


    @Override
    def build() {

        def sqoopService = new ApiService()
        sqoopService.displayName = SERVICE_NAME
        sqoopService.name = SERVICE_NAME
        sqoopService.type = SERVICE_TYPE_NAME
        sqoopService.config = createServiceWideConfig()

        def roleList = []

        //add SQOOP_SERVER
        roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: SqoopServerConfigGroup.NAME),
                hostRef: new ApiHostRef(hostId: Hosts.getInstance().HOST_01),
                name: "$SQOOP_SERVER-${Hosts.asRoleNameSuffix(Hosts.getInstance().HOST_01)}",
                type: SQOOP_SERVER
        )

        sqoopService.roleConfigGroups = [
                new SqoopServerConfigGroup().build(),
        ]
        sqoopService.roles = roleList
        new ApiServiceList(services: [sqoopService])
    }


    ApiServiceConfig createServiceWideConfig() {
        def apiConfig = new ApiServiceConfig()
        apiConfig.add new ApiConfig(name: 'mapreduce_yarn_service', value: MapReduce.SERVICE_NAME)
        return apiConfig
    }

    static class SqoopServerConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$SQOOP_SERVER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = SQOOP_SERVER
            configGroup.name = NAME
            configGroup.displayName = "$SQOOP_SERVER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([
                    new ApiConfig(name: 'sqoop_java_heapsize', value: 923754052),
            ])
            return configGroup
        }
    }
}
