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

class Impala implements BuiltModel {

    public static final String SERVICE_TYPE_NAME = 'IMPALA'
    public static final String SERVICE_NAME = 'IMPALA01'

    public static final String IMPALA_DAEMON = 'IMPALADAEMON'
    public static final String IMPALA_CATALOG_SERVER = 'IMPALACATALOGSERVERDAEMON'
    public static final String IMPALA_STATE_STORE_DAEMON = 'IMPALASTATESTOREDAEMON'


    @Override
    def build() {
        def impalaService = new ApiService()
        impalaService.displayName = SERVICE_NAME
        impalaService.name = SERVICE_NAME
        impalaService.type = SERVICE_TYPE_NAME
        impalaService.config = createServiceWideConfig()


        def roleList = []

        //add IMPALA_DAEMON
        roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: ImpalaDaemonConfigGroup.NAME),
                hostRef: new ApiHostRef(hostId: Hosts.HOST_02),
                name: "$IMPALA_DAEMON-${Hosts.asRoleNameSuffix(Hosts.HOST_02)}",
                type: IMPALA_DAEMON
        )

        //add IMPALA_CATALOG_SERVER
        roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: ImpalaCatalogServerConfigGroup.NAME),
                hostRef: new ApiHostRef(hostId: Hosts.HOST_02),
                name: "$IMPALA_CATALOG_SERVER-${Hosts.asRoleNameSuffix(Hosts.HOST_02)}",
                type: IMPALA_CATALOG_SERVER
        )

        //add IMPALA_STATE_STORE_DAEMON
        roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: ImpalaStateStoreConfigGroup.NAME),
                hostRef: new ApiHostRef(hostId: Hosts.HOST_03),
                name: "$IMPALA_STATE_STORE_DAEMON-${Hosts.asRoleNameSuffix(Hosts.HOST_03)}",
                type: IMPALA_STATE_STORE_DAEMON
        )


        impalaService.roleConfigGroups = [new ImpalaDaemonConfigGroup().build(),
                new ImpalaCatalogServerConfigGroup().build(),
                new ImpalaStateStoreConfigGroup().build(),
        ]
        impalaService.roles = roleList
        new ApiServiceList(services: [impalaService])

    }

    ApiServiceConfig createServiceWideConfig() {
        def apiConfig = new ApiServiceConfig()
        apiConfig.add new ApiConfig(name: 'hbase_service', value: HBase.SERVICE_NAME)
        apiConfig.add new ApiConfig(name: 'hdfs_service', value: HDFS.SERVICE_NAME)
        apiConfig.add new ApiConfig(name: 'hive_service', value: Hive.SERVICE_NAME)
        return apiConfig
    }

    static class ImpalaDaemonConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$IMPALA_DAEMON-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = IMPALA_DAEMON
            configGroup.name = NAME
            configGroup.displayName = "$IMPALA_DAEMON (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([])
            return configGroup
        }
    }

    static class ImpalaCatalogServerConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$IMPALA_CATALOG_SERVER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = IMPALA_CATALOG_SERVER
            configGroup.name = NAME
            configGroup.displayName = "$IMPALA_CATALOG_SERVER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([])
            return configGroup
        }
    }

    static class ImpalaStateStoreConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$IMPALA_STATE_STORE_DAEMON-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = IMPALA_STATE_STORE_DAEMON
            configGroup.name = NAME
            configGroup.displayName = "$IMPALA_STATE_STORE_DAEMON (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([])
            return configGroup
        }
    }

}
