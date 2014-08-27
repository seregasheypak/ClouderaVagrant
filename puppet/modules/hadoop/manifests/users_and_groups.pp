class hadoop::users_and_groups {  
  group{['supergroup', 'hadoop']:
    ensure => present,
  }
  ->
  user {['mapred', 'hbase', 'hdfs', 'hive', 'vagrant']:
    ensure => present,
    groups => ['supergroup', 'hadoop'],
  }

}