package cloudera.services.installer.model

import com.cloudera.api.model.ApiConfig
import com.cloudera.api.model.ApiConfigList
import com.cloudera.api.model.ApiRole
import com.cloudera.api.model.ApiRoleConfigGroup
import com.cloudera.api.model.ApiRoleConfigGroupList
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

    def build(){
        def hdfsService = new ApiService()

        hdfsService.displayName = SERVICE_NAME
        hdfsService.name        = SERVICE_NAME
        hdfsService.type        = SERVICE_TYPE_NAME
        hdfsService.setRoles([nameNodeRole, secondaryNameNodeRole, dataNodeRoles].flatten())

        new ApiServiceList(services: [hdfsService])
    }

    ApiRole getNameNodeRole(){
        new ApiRole(type: NAMENODE,
                    hostRef: new Hosts().hostRef(Hosts.HOST_01),
                    config: nameNodeApiConfigList)
    }

    ApiRole getSecondaryNameNodeRole(){
        new ApiRole(type: SECONDARYNAMENODE,
                    hostRef: new Hosts().hostRef(Hosts.HOST_02),
                    config: secondaryNameNodeApiConfigList)
    }

    List<ApiRole> getDataNodeRoles(){
        def i=1;
        new Hosts().build().hosts.collect{ host->
            new ApiRole(hostRef: new Hosts().hostRef(host.hostId),
                        type: DATANODE,
                        name: "${DATANODE}_${i++}",
                        config: dataNodeApiConfigList)
        }
    }

    ApiConfigList getDataNodeApiConfigList(){
        new ApiConfigList(values: [new ApiConfig(name: 'dfs_data_dir_list', value: '/dfs/datanode/data')])
    }

    ApiConfigList getNameNodeApiConfigList(){
        new ApiConfigList(values: [new ApiConfig(name: 'dfs_name_dir_list', value: '/dfs/namenode/nn')])
    }

    ApiConfigList getSecondaryNameNodeApiConfigList(){
        new ApiConfigList(values: [new ApiConfig(name: 'fs_checkpoint_dir_list', value: '/dfs/secondarynamenode/checkpoint')])
    }

    ApiRoleConfigGroupList getDataNodeRoleConfigGroup(){
        ApiRoleConfigGroup confGroup = new ApiRoleConfigGroup()
        confGroup.base = true
        confGroup.displayName = DATANODE+'_BASE_ROLE_GROUP'
        confGroup.roleType = DATANODE
        null
    }

}

