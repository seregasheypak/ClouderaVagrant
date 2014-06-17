define hadoop::cloudera::mapreduce(
  #Task tracker
  $mapred_tasktracker_map_tasks_maximum = nil,
  $mapred_tasktracker_reduce_tasks_maximum = nil,
  $mapred_tasktracker_instrumentation = nil,  
  $tasktracker_config_safety_valve = nil,
  $tasktracker_max_log_backup_index = nil,
  $tasktracker_max_log_size = nil,
  $task_tracker_java_heapsize = nil,
  $tasktracker_mapred_local_dir_list = nil,
  $tasktracker_log_threshold = nil,
  $tasktracker_override_mapred_userlog_retain_hours = nil,
  
  #Job tracker
  $jobtracker_java_heapsize = nil,
  $jobtracker_mapred_local_dir_list = nil,
  $mapred_job_tracker_history_completed_dir = nil,
  $jobtracker_max_log_backup_index = nil,
  $jobtracker_max_log_size = nil,
  $mapred_job_tracker_handler_count = nil,
  $jobtracker_log_threshold = nil,
  $jobtracker_mapred_fairscheduler_allocation = nil,
  $jobtracker_mapred_fairscheduler_allow_undeclared_pools = nil,
  $jobtracker_mapred_fairscheduler_poolnameproperty = nil,
  $jobtracker_mapred_queue_names_list = nil,
  $jobtracker_mapreduce_jobhistory_cleaner_interval = nil,
  
  #Gateway
  $gate_io_sort_mb = nil,
  $gate_mapred_child_java_opts_max_heap = nil,
  $gate_mapred_reduce_tasks = nil,
  $gate_mapred_submit_replication = nil,
  $gate_mapred_userlog_retain_hours = nil,
  #
  $scm_cluster_name = hiera(cm_cluster_name),
  $scm_mapreduce_service_name = hiera(cm_mapreduce_service),
){
 include 'hadoop::cm_rest_client'  
  
  cm_service_role_group { 
   "tasktracker_config-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_mapreduce_service_name,
    scm_role_group => "TaskTracker (Default)",
    config         => {
      'mapred_tasktracker_map_tasks_maximum'    => "$mapred_tasktracker_map_tasks_maximum",
      'mapred_tasktracker_reduce_tasks_maximum' => "$mapred_tasktracker_reduce_tasks_maximum",
      'tasktracker_config_safety_valve'         => "$tasktracker_config_safety_valve",
      'mapred_tasktracker_instrumentation'      => "$mapred_tasktracker_instrumentation",
      'max_log_backup_index' => "$tasktracker_max_log_backup_index",
      'max_log_size' => "$tasktracker_max_log_size",
      'task_tracker_java_heapsize' => "$task_tracker_java_heapsize",
      'tasktracker_mapred_local_dir_list' => "$tasktracker_mapred_local_dir_list",
      'log_threshold' => "$tasktracker_log_threshold",
      'override_mapred_userlog_retain_hours' => "$tasktracker_override_mapred_userlog_retain_hours",
    };
   "jobtracker_config-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_mapreduce_service_name,
    scm_role_group => "JobTracker (Default)",
    config         => {
      'jobtracker_java_heapsize'    => "$jobtracker_java_heapsize",
      'jobtracker_mapred_local_dir_list' => "$jobtracker_mapred_local_dir_list",
      'mapred_job_tracker_history_completed_dir'         => "$mapred_job_tracker_history_completed_dir",
      'mapred_tasktracker_instrumentation'      => "$mapred_tasktracker_instrumentation",
      'max_log_backup_index' => "$jobtracker_max_log_backup_index",
      'max_log_size' => "$jobtracker_max_log_size",
      'mapred_job_tracker_handler_count' => "$mapred_job_tracker_handler_count",
      'log_threshold' => "$jobtracker_log_threshold",
      'mapred_fairscheduler_allocation' => "$jobtracker_mapred_fairscheduler_allocation",
      'mapred_fairscheduler_allow_undeclared_pools' => "$jobtracker_mapred_fairscheduler_allow_undeclared_pools",
      'mapred_fairscheduler_poolnameproperty' => "$jobtracker_mapred_fairscheduler_poolnameproperty",
      'mapred_queue_names_list' => "$jobtracker_mapred_queue_names_list",
      'mapreduce_jobhistory_cleaner_interval' => "$jobtracker_mapreduce_jobhistory_cleaner_interval",
    };
   "gateway_config-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_mapreduce_service_name,
    scm_role_group => "Gateway (Default)",
    config         => {
      'io_sort_mb'    => "$gate_io_sort_mb",
      'mapred_child_java_opts_max_heap' => "$gate_mapred_child_java_opts_max_heap",
      'mapred_reduce_tasks'         => "$gate_mapred_reduce_tasks",
      'mapred_submit_replication'      => "$gate_mapred_submit_replication",
      'mapred_userlog_retain_hours' => "$gate_mapred_userlog_retain_hours",
    };
   
  }
   
}