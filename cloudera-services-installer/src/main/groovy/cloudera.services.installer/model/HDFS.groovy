package cloudera.services.installer.model

import com.cloudera.api.model.ApiConfig
import com.cloudera.api.model.ApiConfigList
import com.cloudera.api.model.ApiHostRef
import com.cloudera.api.model.ApiRole
import com.cloudera.api.model.ApiRoleConfigGroup
import com.cloudera.api.model.ApiRoleConfigGroupList
import com.cloudera.api.model.ApiRoleConfigGroupRef
import com.cloudera.api.model.ApiRoleList
import com.cloudera.api.model.ApiService
import com.cloudera.api.model.ApiServiceConfig
import com.cloudera.api.model.ApiServiceList
import com.cloudera.api.model.ApiServiceRef

/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 16:30
 */
class HDFS implements BuiltModel{

    public static final String SERVICE_TYPE_NAME = 'HDFS'
    public static final String SERVICE_NAME = 'HDFS01'

    public static final String NAMENODE = 'NAMENODE'
    public static final String SECONDARYNAMENODE = 'SECONDARYNAMENODE'
    public static final String DATANODE = 'DATANODE'

    public static final int HEAP_SIZE_128_MB = 134217728
    public static final int HEAP_SIZE_256_MB = HEAP_SIZE_128_MB * 2


    def build(){
        def hdfsService = new ApiService()

        hdfsService.displayName = SERVICE_NAME
        hdfsService.name        = SERVICE_NAME
        hdfsService.type        = SERVICE_TYPE_NAME




        def roleList = []
        Hosts.HOSTS.each { host ->
            roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName:DataNodeConfigGroup.NAME),
                                     hostRef:            new ApiHostRef(hostId: host.hostname),
                                     name:               "$DATANODE-$host.hostname",
                                     type:               DATANODE
            )
        }
        roleList.add new ApiRole( roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: NameNodeConfigGroup.NAME),
                                  hostRef:            new ApiHostRef(hostId: Hosts.HOST_01),
                                  name:               "$NAMENODE-$Hosts.HOST_01",
                                  type:               NAMENODE
                )

        roleList.add new ApiRole( roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: SecondaryNameNodeConfigGroup.NAME),
                                  hostRef:            new ApiHostRef(hostId: Hosts.HOST_02),
                                  name:               "$SECONDARYNAMENODE-$Hosts.HOST_02",
                                  type:               SECONDARYNAMENODE
        )
        hdfsService.roleConfigGroups = [new DataNodeConfigGroup().build(), new NameNodeConfigGroup().build(), new SecondaryNameNodeConfigGroup().build()]
        hdfsService.roles = roleList
        new ApiServiceList(services: [hdfsService])
    }


    static class HDFSConfigGroups implements BuiltModel{

        def build(){
            new ApiRoleConfigGroupList([new DataNodeConfigGroup().build(), new NameNodeConfigGroup().build(), new SecondaryNameNodeConfigGroup().build()])
        }
    }

    static class DataNodeConfigGroup implements BuiltModel{

        public static final String NAME = "$SERVICE_NAME-$DATANODE-BASE"
        public static final String DATANODE_JAVA_HEAPSIZE = 'datanode_java_heapsize'
        public static final String DFS_DATA_DIR_LIST = 'dfs_data_dir_list'

        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = DATANODE
            configGroup.name = NAME
            configGroup.displayName = "$DATANODE (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([new ApiConfig(name: DATANODE_JAVA_HEAPSIZE, value: HEAP_SIZE_128_MB),
                                                    new ApiConfig(name: DFS_DATA_DIR_LIST, value: '/dfs/dn')
                                                   ])
            configGroup
        }
    }

    static class SecondaryNameNodeConfigGroup implements BuiltModel{

        public static final String NAME = "$SERVICE_NAME-$SECONDARYNAMENODE-BASE"

        public static final String FS_CHECKPOINT_DIR_LIST = 'fs_checkpoint_dir_list'
        public static final String SECONDARY_NAMENODE_JAVA_HEAPSIZE = 'secondary_namenode_java_heapsize'

        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = SECONDARYNAMENODE
            configGroup.name = NAME
            configGroup.displayName = "$SECONDARYNAMENODE (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([new ApiConfig(name: FS_CHECKPOINT_DIR_LIST,           value: '/dfs/snn'),
                                                    new ApiConfig(name: SECONDARY_NAMENODE_JAVA_HEAPSIZE, value: HEAP_SIZE_256_MB)
            ])
            configGroup

        }
    }

    static class NameNodeConfigGroup implements BuiltModel{

        public static final String NAME = "$SERVICE_NAME-$NAMENODE-BASE"

        public static final String DFS_NAME_DIR_LIST = 'dfs_name_dir_list'
        public static final String NAMENODE_JAVA_HEAPSIZE = 'namenode_java_heapsize'
        public static final String DFS_NAMENODE_SERVICERPC_ADDRESS = 'dfs_namenode_servicerpc_address'

        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = NAMENODE
            configGroup.name = NAME
            configGroup.displayName = "$NAMENODE (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([new ApiConfig(name: DFS_NAME_DIR_LIST,               value: '/dfs/nn'),
                                                    new ApiConfig(name: DFS_NAMENODE_SERVICERPC_ADDRESS, value: '8022'),
                                                    new ApiConfig(name: NAMENODE_JAVA_HEAPSIZE,          value: HEAP_SIZE_256_MB)
            ])
            configGroup

        }
    }
}

