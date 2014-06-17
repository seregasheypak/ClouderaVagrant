define hadoop::cloudera::hdfs(
  #Service wide
  $dfs_block_local_path_access_user = nil,
  $dfs_ha_fencing_methods = nil,
  $dfs_namenode_quorum_journal_name = nil,
  $hdfs_service_config_safety_valve = nil,
  #DataNode default
  $default_datanode_java_heapsize = nil,
  $default_datanode_dfs_data_dir_list = nil,
  $default_dfs_datanode_data_dir_perm = nil, 
  $default_dfs_datanode_du_reserved = nil,
  $default_dfs_datanode_failed_volumes_tolerated = nil,
  $default_dfs_datanode_handler_count = nil,
  $default_dfs_datanode_max_xcievers = nil,
  $default_datanode_max_log_backup_index = nil,
  $default_datanode_max_log_size = nil,
  #Gateway
  $gate_dfs_client_use_trash = nil,
  $gate_hdfs_client_config_safety_valve = nil,
  #NameNode
  $namenode_dfs_name_dir_list = nil,
  $dfs_namenode_servicerpc_address = nil,
  $dfs_namenode_handler_count = nil,
  $dfs_namenode_service_handler_count = nil,
  $namenode_java_heapsize = nil,
  $namenode_autofailover_enabled = nil,
  #Secondary NameNode
  $secondary_fs_checkpoint_dir_list = nil,
  $secondary_namenode_java_heapsize = nil,
  #Balancer
  $balancer_config_safety_valve = nil,
  #JournalNode
  $dfs_journalnode_edits_dir = nil,
  $journalNode_java_heapsize = nil,
  #Http FS
  $httpfs_java_heapsize = nil,
  #
  $scm_cluster_name = hiera(cm_cluster_name),
  $scm_hdfs_service_name = hiera(cm_hdfs_service),
){
  include 'hadoop::cm_rest_client'  
  
  cm_service_wide{"hdfs-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_hdfs_service_name,
    config => {
      'dfs_block_local_path_access_user' => $dfs_block_local_path_access_user,
      'dfs_ha_fencing_methods' => $dfs_ha_fencing_methods,
      'dfs_namenode_quorum_journal_name' => $dfs_namenode_quorum_journal_name,
      'hdfs_service_config_safety_valve' => $hdfs_service_config_safety_valve,
    };
  }
  
  cm_service_role_group { 
  "hdfs-datanode-default-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_hdfs_service_name,
    scm_role_group => "DataNode (Default)",
    config         => {
      'datanode_java_heapsize'        => $default_datanode_java_heapsize,
      'dfs_data_dir_list' => $default_datanode_dfs_data_dir_list,
      'dfs_datanode_data_dir_perm' => $default_dfs_datanode_data_dir_perm,
      'dfs_datanode_du_reserved' => $default_dfs_datanode_du_reserved,
      'dfs_datanode_failed_volumes_tolerated' => $default_dfs_datanode_failed_volumes_tolerated,
      'dfs_datanode_handler_count' => $default_dfs_datanode_handler_count,
      'dfs_datanode_max_xcievers' => $default_dfs_datanode_max_xcievers,
      'max_log_backup_index' => $default_datanode_max_log_backup_index,
      'max_log_size' => $default_datanode_max_log_size,
    };
   "hdfs-default-gateway-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_hdfs_service_name,
    scm_role_group => "Gateway (Default)",
    config         => {
      'dfs_client_use_trash'        => $gate_dfs_client_use_trash,
      'hdfs_client_config_safety_valve' => $gate_hdfs_client_config_safety_valve,      
    };  
   "hdfs-namenode-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_hdfs_service_name,
    scm_role_group => "NameNode (Default)",
    config         => {
      'dfs_name_dir_list'        => $namenode_dfs_name_dir_list,
      'dfs_namenode_servicerpc_address' => $dfs_namenode_servicerpc_address,
      'dfs_namenode_handler_count' => $dfs_namenode_handler_count,
      'namenode_java_heapsize' => $namenode_java_heapsize, 
      'autofailover_enabled' => $namenode_autofailover_enabled,
      'dfs_namenode_service_handler_count' => $dfs_namenode_service_handler_count,     
    };    
   "hdfs-secondary-namenode-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_hdfs_service_name,
    scm_role_group => "SecondaryNameNode (Default)",
    config         => {
      'fs_checkpoint_dir_list'        => $secondary_fs_checkpoint_dir_list,
      'secondary_namenode_java_heapsize' => $secondary_namenode_java_heapsize,      
    };  
   "hdfs-balancer-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_hdfs_service_name,
    scm_role_group => "Balancer (Default)",
    config         => {
      'balancer_config_safety_valve'        => $balancer_config_safety_valve,
    };    
    "hdfs-journalnode-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_hdfs_service_name,
    scm_role_group => "JournalNode (Default)",
    config         => {
      'dfs_journalnode_edits_dir'        => $dfs_journalnode_edits_dir,
      'journalNode_java_heapsize' => $journalNode_java_heapsize,
    };  
    "hdfs-httpfs-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => $scm_cluster_name,
    scm_service    => $scm_hdfs_service_name,
    scm_role_group => "HttpFS (Default)",
    config         => {
      'httpfs_java_heapsize'        => $httpfs_java_heapsize,      
    }; 
  }
    
  
}