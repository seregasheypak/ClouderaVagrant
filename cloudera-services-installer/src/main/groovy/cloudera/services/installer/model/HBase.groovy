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

class HBase implements BuiltModel {

    public static final String SERVICE_TYPE_NAME = 'HBASE'
    public static final String SERVICE_NAME = 'HBASE01'

    public static final String GATEWAY = 'GATEWAY'
    public static final String HBASETHRIFTSERVER = 'HBASETHRIFTSERVER'
    public static final String MASTER = 'MASTER'
    public static final String HBASERESTSERVER = 'HBASERESTSERVER'
    public static final String REGIONSERVER = 'REGIONSERVER'


    @Override
    def build() {
        def hbaseService = new ApiService()
        hbaseService.displayName = SERVICE_NAME
        hbaseService.name        = SERVICE_NAME
        hbaseService.type        = SERVICE_TYPE_NAME
        hbaseService.config = createServiceWideConfig()

        def roleList = []

        Hosts.HOSTS.each { host ->
            //add Gateway for each host
            roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName:GatewayConfigGroup.NAME),
                    hostRef:            new ApiHostRef(hostId: host.hostname),
                    name:               "$SERVICE_TYPE_NAME$GATEWAY-${Hosts.asRoleNameSuffix(host.hostname)}",
                    type:               GATEWAY
            )

        }

        //add all others roles
        roleList.add new ApiRole( roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: MasterConfigGroup.NAME),
                hostRef:            new ApiHostRef(hostId: Hosts.HOST_01),
                name:               "$MASTER-${Hosts.asRoleNameSuffix(Hosts.HOST_01)}",
                type:               MASTER
        )
        roleList.add new ApiRole( roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: RegionConfigGroup.NAME),
                hostRef:            new ApiHostRef(hostId: Hosts.HOST_01),
                name:               "$REGIONSERVER-${Hosts.asRoleNameSuffix(Hosts.HOST_01)}",
                type:               REGIONSERVER
        )
        roleList.add new ApiRole( roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: ThriftServerConfigGroup.NAME),
                hostRef:            new ApiHostRef(hostId: Hosts.HOST_01),
                name:               "$HBASETHRIFTSERVER-${Hosts.asRoleNameSuffix(Hosts.HOST_01)}",
                type:               HBASETHRIFTSERVER
        )
        roleList.add new ApiRole( roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: RestServerConfigGroup.NAME),
                hostRef:            new ApiHostRef(hostId: Hosts.HOST_01),
                name:               "$HBASERESTSERVER-${Hosts.asRoleNameSuffix(Hosts.HOST_01)}",
                type:               HBASERESTSERVER
        )

        hbaseService.roleConfigGroups = [new GatewayConfigGroup().build(),
                new MasterConfigGroup().build(),
                new RegionConfigGroup().build(),
                new ThriftServerConfigGroup().build(),
                new RestServerConfigGroup().build(),
        ]
        hbaseService.roles = roleList
        ApiServiceList services = new ApiServiceList(services: [hbaseService])
        services.iterator().each {serv ->
            println serv.toString();
        }
        return services
    }

    ApiServiceConfig createServiceWideConfig() {
        def apiConfig = new ApiServiceConfig()
        apiConfig.add new ApiConfig(name: 'hdfs_service', value: HDFS.SERVICE_NAME)
        apiConfig.add new ApiConfig(name: 'zookeeper_service', value: Zookeeper.SERVICE_NAME)
        return apiConfig
    }

    static class MasterConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$MASTER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = MASTER
            configGroup.name = NAME
            configGroup.displayName = "$MASTER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([
//                    new ApiConfig(name: 'hbase_master_java_heapsize', value: 1030531544),
            ])
            return configGroup
        }
    }

    static class RegionConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$REGIONSERVER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = REGIONSERVER
            configGroup.name = NAME
            configGroup.displayName = "$REGIONSERVER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([
//                    new ApiConfig(name: 'hbase_regionserver_java_heapsize', value: 1030531544),
            ])
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

    static class ThriftServerConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$HBASETHRIFTSERVER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = HBASETHRIFTSERVER
            configGroup.name = NAME
            configGroup.displayName = "$HBASETHRIFTSERVER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([])
            return configGroup
        }
    }

    static class RestServerConfigGroup implements BuiltModel {

        public static final String NAME = "$SERVICE_NAME-$HBASERESTSERVER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = HBASERESTSERVER
            configGroup.name = NAME
            configGroup.displayName = "$HBASERESTSERVER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([])
            return configGroup
        }
    }
}
