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

class Oozie implements BuiltModel {

    public static final String SERVICE_TYPE_NAME = 'OOZIE'
    public static final String SERVICE_NAME = 'OOZIE01'

    public static final String OOZIE_SERVER = 'OOZIE_SERVER'


    @Override
    def build() {
        def oozieService = new ApiService()
        oozieService.displayName = SERVICE_NAME
        oozieService.name = SERVICE_NAME
        oozieService.type = SERVICE_TYPE_NAME
        oozieService.config = createServiceWideConfig()

        def roleList = []

        roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: OozieServerConfigGroup.NAME),
                hostRef: new ApiHostRef(hostId: Hosts.HOST_02),
                name: "$OOZIE_SERVER-${Hosts.asRoleNameSuffix(Hosts.HOST_02)}",
                type: OOZIE_SERVER
        )


        oozieService.roleConfigGroups = [new OozieServerConfigGroup().build()]
        oozieService.roles = roleList
        new ApiServiceList(services: [oozieService])

    }

    ApiServiceConfig createServiceWideConfig() {
        def apiConfig = new ApiServiceConfig()
        apiConfig.add new ApiConfig(name: 'mapreduce_yarn_service', value: MapReduce.SERVICE_NAME)
        return apiConfig
    }


    static class OozieServerConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$OOZIE_SERVER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = OOZIE_SERVER
            configGroup.name = NAME
            configGroup.displayName = "$OOZIE_SERVER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([
//                    new ApiConfig(name: 'oozie_service_callablequeueservice_callable_concurrency', value: 96),
            ])
            return configGroup
        }
    }
}
