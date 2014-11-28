define kafka::topic(
  $topic_name = $name,
  $partition,
  $replica
  
){
  $kafka_dir = '/opt/kafka'
  
  $zookeeper_hosts_str = join(hiera_array(kafka_zookeepers), ',')
  $zookeeper_chroot = hiera(kafka_zookeeper_root)
  
  exec{"$kafka_dir/bin/kafka-create-topic.sh --partition $partition --replica $replica --topic $topic_name --zookeeper $zookeeper_hosts_str$zookeeper_chroot":
    unless => "$kafka_dir/bin/kafka-list-topic.sh --zookeeper $zookeeper_hosts_str$zookeeper_chroot | grep $topic_name",
    require => Class['kafka']
  }
}