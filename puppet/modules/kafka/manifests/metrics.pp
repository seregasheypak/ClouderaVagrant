class kafka::metrics(
  $zookeeper_hosts,
  $zookeeper_chroot,
  $update_interval,
  $expire_interval
) {

  monit::shell::service { 'kafka-report-brokers-count':
    command        => "kafka-report-brokers-count.sh ${zookeeper_hosts[0]}$zookeeper_chroot $update_interval $expire_interval",
    restart_count  => 5,
    restart_cycles => 5
  }
  
}
