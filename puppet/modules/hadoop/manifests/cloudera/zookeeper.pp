define hadoop::cloudera::zookeeper (
 $maxSessionTimeout = nil,
  $scm_cluster_name = hiera(cm_cluster_name),
  $scm_zookeeper_service_name = hiera(cm_zookeeper_service),
) {
  include 'hadoop::cm_rest_client'

  cm_service_role_group {
    "zookeeper-config-$name":
      ensure         => present,
      require        => Class['hadoop::cm_rest_client'],
      scm_host_port  => hiera(cm_host_port),
      scm_cluster    => $scm_cluster_name,
      scm_service    => $scm_zookeeper_service_name,
      scm_role_group => 'Server (Default)',
      config         => {
        'maxSessionTimeout' => $maxSessionTimeout,
      };    
  }
}