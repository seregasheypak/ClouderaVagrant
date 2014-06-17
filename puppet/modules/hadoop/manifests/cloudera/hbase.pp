define hadoop::cloudera::hbase (
  #master
  $java_heap_master = nil,  
  #region
  $hbase_regionserver_handler_count = nil,
  $hbase_regionserver_java_heapsize = nil,  
  $scm_cluster_name = hiera(cm_cluster_name),
  $scm_hbase_service_name = hiera(cm_hbase_service), 
) {
  include 'hadoop::cm_rest_client'

  cm_service_role_group {
    "hbase_master-$name":
      ensure         => present,
      require        => Class['hadoop::cm_rest_client'],
      scm_host_port  => hiera(cm_host_port),
      scm_cluster    => $scm_cluster_name,
      scm_service    => $scm_hbase_service_name,
      scm_role_group => 'Master (Default)',
      config         => {
        'hbase_master_java_heapsize' => $java_heap_master,       
      };

    "hbase_region_server-$name":
      ensure         => present,
      require        => Class['hadoop::cm_rest_client'],
      scm_host_port  => hiera(cm_host_port),
      scm_cluster    => hiera(cm_cluster_name),
      scm_service    => hiera(cm_hbase_service),
      scm_role_group => 'RegionServer (Default)',
      config         => {
        'hbase_regionserver_java_heapsize' => $hbase_regionserver_java_heapsize,        
        'hbase_regionserver_handler_count' => $hbase_regionserver_handler_count,
      };
  }
}