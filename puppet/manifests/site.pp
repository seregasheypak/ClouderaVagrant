node default {
  #include 'cloudera'
  
  class { 'cloudera':
        cm_server_host => 'vm-cluster-node1.localdomain',
        use_parcels    => true,
    } ->
   class { 'cloudera::java': } ->
   class { 'cloudera::cm': }
}

node 'vm-cluster-node1.localdomain' { #inherits default {
  class { 'cloudera':
        cm_version => '4.8.0',
        cm_server_host => 'vm-cluster-node1.localdomain',
        use_parcels    => true,
    } ->   
  class { 'cloudera::cm::server': }
}

