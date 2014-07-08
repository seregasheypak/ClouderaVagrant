class hadoop::users_and_groups {  
  group{['supergroup', 'hadoop']:
    ensure => present,
  }
  ->
  user {['mapred', 'hbase', 'hdfs']:
    ensure => present,
    groups => ['supergroup', 'hadoop'],
  }

}