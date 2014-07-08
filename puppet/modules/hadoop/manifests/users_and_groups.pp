class hadoop::users_and_groups {
  group{'supergroup':
    ensure => present,
  }
  ->
  user {['mapred', 'hbase', 'hdfs']:
    ensure => present,
    groups => ['supergroup'],
  }
}