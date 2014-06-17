define hadoop::cloudera::impala (
  $max_log_size = nil,
  $impalad_base_memory_limit               = nil,
  $impalad_collocated_memory_limit         = nil,
  $impalad_collocated_rm_memory_hard_limit = nil,
  $impalad_moderate_memory_limit = nil,) {
  include 'hadoop::cm_rest_client'

  cm_service_role_group {
    "impala_catalog_server_config":
      ensure         => present,
      scm_host_port  => hiera(cm_host_port),
      scm_cluster    => hiera(cm_cluster_name),
      scm_service    => hiera(cm_impala_service),
      scm_role_group => "Impala Catalog Server Daemon (Default)",
      config         => {
        'max_log_size' => "$max_log_size",
      };

    "impala_daemon_base_config":
      ensure         => present,
      scm_host_port  => hiera(cm_host_port),
      scm_cluster    => hiera(cm_cluster_name),
      scm_service    => hiera(cm_impala_service),
      scm_role_group => "Impala Daemon (Default)",
      config         => {
        'impalad_memory_limit' => "$impalad_base_memory_limit",
      };

  }

  if $impalad_collocated_memory_limit != nil or $impalad_collocated_rm_memory_hard_limit != nil {
    cm_service_role_group { "impala_daemon_collocated_config":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => hiera(cm_cluster_name),
    scm_service    => hiera(cm_impala_service),
    scm_role_group => "collocated_jt_nn",
    config         => {
      'impalad_memory_limit' => "$impalad_collocated_memory_limit",
      'rm_memory_hard_limit' => "$impalad_collocated_rm_memory_hard_limit",
    }
   }    
  }
  
  if $impalad_moderate_memory_limit != nil {
     cm_service_role_group { "impalad_moderate_config":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => hiera(cm_cluster_name),
    scm_service    => hiera(cm_impala_service),
    scm_role_group => "impalad_moderate",
    config         => {
      'impalad_memory_limit' => $impalad_moderate_memory_limit,      
    }
   } 
  }
  

}