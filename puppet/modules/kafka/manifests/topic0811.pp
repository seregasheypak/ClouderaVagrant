define kafka::topic0811(
  $topic_name = $name,
  $partition,
  $replica
  
){
  $kafka_dir = '/opt/kafka'
  
  $zookeeper_hosts_str = join(hiera_array(kafka_zookeepers), ',')
  $zookeeper_chroot = hiera(kafka_zookeeper_root)
  
  exec{"$kafka_dir/bin/kafka-topics.sh --create --partition $partition --replication-factor $replica --topic $topic_name --zookeeper $zookeeper_hosts_str$zookeeper_chroot":
    unless => "$kafka_dir/bin/kafka-topics.sh --list --zookeeper $zookeeper_hosts_str$zookeeper_chroot | grep $topic_name",
    require => Class['kafka']
  }
}