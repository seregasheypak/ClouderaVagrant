


node default {
  include cloudera::cloudera
  
  class { 'cloudera':
        cm_server_host => 'vm-cluster-node1.localdomain',
        use_parcels    => true,
    } ->
   class { 'cloudera::java': } ->
   class { 'cloudera::cm': }
}

node 'vm-cluster-node1.localdomain' inherits default {
  class { 'cloudera::repo':
  #  cdh_version => '4.4',
    cm_version  => '4.8',
  } ->
  
  
  class { 'cloudera::cm::server': }
}

