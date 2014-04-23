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

class Hive implements BuiltModel {

    public static final String SERVICE_TYPE_NAME = 'HIVE'
    public static final String SERVICE_NAME = 'HIVE01'

    public static final String GATEWAY = 'GATEWAY'
    public static final String HIVESERVER2 = 'HIVESERVER2'
    public static final String HIVEMETASTORE = 'HIVEMETASTORE'
    public static final String WEBHCAT = 'WEBHCAT'


    @Override
    def build() {
        def hiveService = new ApiService()
        hiveService.displayName = SERVICE_NAME
        hiveService.name = SERVICE_NAME
        hiveService.type = SERVICE_TYPE_NAME
        hiveService.config = createServiceWideConfig()

        def roleList = []

        Hosts.HOSTS.each { host ->
            //add Gateway for each host
            roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: GatewayConfigGroup.NAME),
                    hostRef: new ApiHostRef(hostId: host.hostname),
                    name: "$SERVICE_TYPE_NAME$GATEWAY-${Hosts.asRoleNameSuffix(host.hostname)}",
                    type: GATEWAY
            )
        }

        //add Webchat
        roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: WebChatConfigGroup.NAME),
                hostRef: new ApiHostRef(hostId: Hosts.HOST_03),
                name: "$WEBHCAT-${Hosts.asRoleNameSuffix(Hosts.HOST_03)}",
                type: WEBHCAT
        )

        //add Metastore
        roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: MetastoreConfigGroup.NAME),
                hostRef: new ApiHostRef(hostId: Hosts.HOST_03),
                name: "$HIVEMETASTORE-${Hosts.asRoleNameSuffix(Hosts.HOST_03)}",
                type: HIVEMETASTORE
        )

        //add Hiveserver2
        roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: Hiveserver2ConfigGroup.NAME),
                hostRef: new ApiHostRef(hostId: Hosts.HOST_03),
                name: "$HIVESERVER2-${Hosts.asRoleNameSuffix(Hosts.HOST_03)}",
                type: HIVESERVER2
        )

        hiveService.roleConfigGroups = [new WebChatConfigGroup().build(),
                new GatewayConfigGroup().build(),
                new MetastoreConfigGroup().build(),
                new Hiveserver2ConfigGroup().build(),
        ]
        hiveService.roles = roleList
        new ApiServiceList(services: [hiveService])
    }

    ApiServiceConfig createServiceWideConfig() {
        def apiConfig = new ApiServiceConfig()
        apiConfig.add new ApiConfig(name: 'mapreduce_yarn_service', value: MapReduce.SERVICE_NAME)
        apiConfig.add new ApiConfig(name: 'zookeeper_service', value: Zookeeper.SERVICE_NAME)
        return apiConfig
    }


    static class WebChatConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$WEBHCAT-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = WEBHCAT
            configGroup.name = NAME
            configGroup.displayName = "$WEBHCAT (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([])
            return configGroup
        }
    }

    static class GatewayConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$GATEWAY-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = GATEWAY
            configGroup.name = NAME
            configGroup.displayName = "$GATEWAY (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([])
            return configGroup
        }
    }

    static class MetastoreConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$HIVEMETASTORE-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = HIVEMETASTORE
            configGroup.name = NAME
            configGroup.displayName = "$HIVEMETASTORE (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([])
            return configGroup
        }
    }

    static class Hiveserver2ConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$HIVESERVER2-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = HIVESERVER2
            configGroup.name = NAME
            configGroup.displayName = "$HIVESERVER2 (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([])
            return configGroup
        }
    }
}
