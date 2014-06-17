define hadoop::cloudera::hive(
  #Service wide
  $hive_metastore_database_host = nil,
  $hive_metastore_database_name = 'hive',
  $hive_metastore_database_user = nil,
  $hive_metastore_database_password = nil,
  $hive_metastore_database_port = nil,
  $hive_metastore_database_type = 'postgresql',
  #Hive others
  $hive_hs2_config_safety_valve = nil,
  $hive_metastore_java_heapsize = nil,
  $hiveserver2_java_heapsize = nil,
  $hiveserver2_java_opts = nil,
  
){
  
  include 'hadoop::cm_rest_client'
  
   cm_service_wide{"hive-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => hiera(cm_cluster_name),
    scm_service    => hiera(cm_hive_service),
    config => {
      'hive_metastore_database_host' => $hive_metastore_database_host,
      'hive_metastore_database_name' => $hive_metastore_database_name,
      'hive_metastore_database_password' => $hive_metastore_database_password,
      'hive_metastore_database_port' => $hive_metastore_database_port,
      'hive_metastore_database_type' => $hive_metastore_database_type,
      'hive_metastore_database_user' => $hive_metastore_database_user,
    };
  }  
  
  cm_service_role_group { 
  "hive_server2":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => hiera(cm_cluster_name),
    scm_service    => hiera(cm_hive_service),
    scm_role_group => "HiveServer2 (Default)",
    config         => {
      'hive_hs2_config_safety_valve'        => $hive_hs2_config_safety_valve,
      'hiveserver2_java_heapsize' => $hiveserver2_java_heapsize,
      'hiveserver2_java_opts' => $hiveserver2_java_opts
    };
   "hive_metastore":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => hiera(cm_cluster_name),
    scm_service    => hiera(cm_hive_service),
    scm_role_group => "Hive Metastore Server (Default)",
    config         => {
      'hive_metastore_java_heapsize'        => "$hive_metastore_java_heapsize",      
    };    

  }
}