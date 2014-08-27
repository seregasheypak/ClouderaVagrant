package cloudera.services.installer.model

import com.cloudera.api.model.ApiConfig
import com.cloudera.api.model.ApiConfigList
import com.cloudera.api.model.ApiHostRef
import com.cloudera.api.model.ApiRole
import com.cloudera.api.model.ApiRoleConfigGroup
import com.cloudera.api.model.ApiRoleConfigGroupRef
import com.cloudera.api.model.ApiService
import com.cloudera.api.model.ApiServiceList
import com.cloudera.api.model.ApiServiceRef

/**
 * User: sergey.sheypak
 * Date: 09.04.14
 * Time: 0:06
 */
class Zookeeper implements BuiltModel{

    static final SERVICE_TYPE_NAME = 'ZOOKEEPER'
    static final SERVICE_NAME = 'ZOOKEEPER01'

    static final SERVER = 'SERVER'

    @Override
    def build(){
        def zkService = new ApiService()

        zkService.displayName = SERVICE_NAME
        zkService.name        = SERVICE_NAME
        zkService.type        = SERVICE_TYPE_NAME

        def roleList = []

        Hosts.getInstance().HOSTS.eachWithIndex { host, idx ->
            roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: ZookeeperServerConfigGroup.NAME),
                                     hostRef:            new ApiHostRef(hostId: host.hostname),
                                     name:               "$SERVICE_TYPE_NAME-${Hosts.asRoleNameSuffix(host.hostname)}",
                                     type:               SERVER//,
                                     //config: new ApiConfigList(configs: [new ApiConfig(name:'serverId', value: idx+1)]
                                     //)
            )
        }


        zkService.roleConfigGroups = [new ZookeeperServerConfigGroup().build()]
        zkService.roles = roleList
        new ApiServiceList(services: [zkService])
    }

    static class ZookeeperServerConfigGroup implements BuiltModel{

        public static final String NAME = "$SERVICE_NAME-$SERVER-BASE"

        @Override
        def build() { def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = SERVER
            configGroup.name = NAME
            configGroup.displayName = "$SERVICE_TYPE_NAME-$SERVER-BASE (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([new ApiConfig(name: 'zookeeper_server_java_heapsize', value: 160774301)])
            configGroup
        }
    }
}
