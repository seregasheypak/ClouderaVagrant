class hadoop::users_and_groups {
  group{'supergroup':
    ensure => present,
  }
  ->
  user {['mapred', 'hbase']:
    ensure => present,
    groups => ['supergroup'],
  }
}