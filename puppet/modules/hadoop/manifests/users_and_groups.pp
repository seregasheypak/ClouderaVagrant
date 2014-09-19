class hadoop::users_and_groups {  
  group{['supergroup', 'hadoop', 'oozie']:
    ensure => present,
  }
  ->
  user {['mapred', 'hbase', 'hdfs', 'hive', 'vagrant']:
    ensure => present,
    groups => ['supergroup', 'hadoop'],
  }
  ->
  user{'oozie':
    ensure => present,
    groups => ['oozie'],
  }
}