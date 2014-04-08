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

/**
 * User: sergey.sheypak
 * Date: 07.04.14
 * Time: 23:22
 */
class MapReduce implements BuiltModel{

    public static final String SERVICE_TYPE_NAME = 'MAPREDUCE'
    public static final String SERVICE_NAME = 'MAPREDUCE01'

    public static final String JOBTRACKER = 'JOBTRACKER'
    public static final String TASKTRACKER = 'TASKTRACKER'
    public static final String GATEWAY = 'GATEWAY'


    @Override
    def build() {
        def mrService = new ApiService()
        mrService.displayName = SERVICE_NAME
        mrService.name        = SERVICE_NAME
        mrService.type        = SERVICE_TYPE_NAME
        mrService.config = createMapReduceApiServiceConfig()

        def roleList = []

        Hosts.HOSTS.each { host ->
            //add TaskTrackers for each host
            roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName:TaskTrackerConfigGroup.NAME),
                    hostRef:            new ApiHostRef(hostId: host.hostname),
                    name:               "$TASKTRACKER-${Hosts.asRoleNameSuffix(host.hostname)}",
                    type:               TASKTRACKER
            )
            //add MapReduce Gateway for each host
            roleList.add new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName:MapReduceGatewayConfigGroup.NAME),
                    hostRef:            new ApiHostRef(hostId: host.hostname),
                    name:               "$GATEWAY-${Hosts.asRoleNameSuffix(host.hostname)}",
                    type:               GATEWAY
            )
        }

        //add JT
        roleList.add new ApiRole( roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: JobTrackerConfigGroup.NAME),
                hostRef:            new ApiHostRef(hostId: Hosts.HOST_02),
                name:               "$JOBTRACKER-${Hosts.asRoleNameSuffix(Hosts.HOST_02)}",
                type:               JOBTRACKER
        )

        mrService.roleConfigGroups = [new TaskTrackerConfigGroup().build(),
                new MapReduceGatewayConfigGroup().build(),
                new JobTrackerConfigGroup().build()]
        mrService.roles = roleList
        new ApiServiceList(services: [mrService])
    }

    static ApiServiceConfig createMapReduceApiServiceConfig(){
        def apiConfig = new ApiServiceConfig()
        apiConfig.add new ApiConfig(name: 'hdfs_service', value: HDFS.SERVICE_NAME)
        apiConfig
    }

    static class TaskTrackerConfigGroup implements BuiltModel{

        public static final String NAME = "$SERVICE_NAME-$TASKTRACKER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = TASKTRACKER
            configGroup.name = NAME
            configGroup.displayName = "$TASKTRACKER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([
                    new ApiConfig(name: 'mapred_tasktracker_map_tasks_maximum',     value: 6),
                    new ApiConfig(name: 'mapred_tasktracker_reduce_tasks_maximum',  value: 3),
                    new ApiConfig(name: 'task_tracker_java_heapsize',               value: 200967876),
                    new ApiConfig(name: 'tasktracker_mapred_local_dir_list',        value: '/mapred/local')
            ])
            configGroup
        }
    }

    static class JobTrackerConfigGroup implements BuiltModel{

        public static final String NAME = "$SERVICE_NAME-$JOBTRACKER-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = JOBTRACKER
            configGroup.name = NAME
            configGroup.displayName = "$JOBTRACKER (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([
                    new ApiConfig(name: 'jobtracker_java_heapsize',         value: 133978584),
                    new ApiConfig(name: 'jobtracker_mapred_local_dir_list', value: '/mapred/jt'),
                    new ApiConfig(name: 'mapred_job_tracker_handler_count', value: 22)
            ])
            configGroup
        }
    }

    static class MapReduceGatewayConfigGroup implements BuiltModel{

        public static final String NAME = "$SERVICE_NAME-$GATEWAY-BASE"

        @Override
        def build() {
            def configGroup = new ApiRoleConfigGroup()
            configGroup.base = true
            configGroup.roleType = GATEWAY
            configGroup.name = NAME
            configGroup.displayName = "$GATEWAY (Default)"
            configGroup.serviceRef = new ApiServiceRef(clusterName: Cluster.name, serviceName: SERVICE_NAME)
            configGroup.config = new ApiConfigList([
                    new ApiConfig(name: 'io_sort_mb',                       value: 50),
                    new ApiConfig(name: 'mapred_child_java_opts_max_heap',  value: 133978584),
                    new ApiConfig(name: 'mapred_submit_replication',        value: 1)
            ])
            configGroup
        }
    }
}
