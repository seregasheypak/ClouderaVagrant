class kafka::offset_monitor(
  $kafka_om_port = '8099',
  $kafka_om_refresh = '10.seconds',
  $kafka_om_retain = '2.days',
){
  
  #if hiera(kafka_install, 'no') == 'yes' {
    $zookeeper_hosts = hiera_array(kafka_zookeepers)
    $zookeeper_hosts_str = join($zookeeper_hosts, ',')
    $zookeeper_chroot = hiera(kafka_zookeeper_root)
  
    $kafka_om_zookeepers = "$zookeeper_hosts_str$zookeeper_chroot"
  
    package{'kafka_om':
      ensure => 'installed',
    }
    ->
    service{'kafka_om':
      ensure => running,
    }
  
    file{'/etc/sysconfig/kafka_om/kafka_om_env.sh':
      ensure => 'file',
      content => template('kafka/kafka_om_env.sh.erb'),
      notify => Service[kafka_om],
    }    
  #}
  
  
}