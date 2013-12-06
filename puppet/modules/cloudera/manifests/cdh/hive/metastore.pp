# == Class: cloudera::cdh::hive::metastore
#
# This class handles installing the Hive Metastore service.
#
# === Parameters:
#
# === Actions:
#
# === Requires:
#
# === Sample Usage:
#
#   class { 'cloudera::cdh::hive::metastore': }
#
# === Authors:
#
# Mike Arnold <mike@razorsedge.org>
#
# === Copyright:
#
# Copyright (C) 2013 Mike Arnold, unless otherwise noted.
#
class cloudera::cdh::hive::metastore {
  package { 'hive-metastore':
    ensure => present,
  }

  service { 'hive-metastore':
    ensure     => running,
    enable     => true,
    hasstatus  => true,
    hasrestart => true,
    require    => Package['hive-metastore'],
  }

  include mysql::java

  file { '/usr/lib/hive/lib/mysql-connector-java.jar':
    ensure => link,
    target => '/usr/share/java/mysql-connector-java.jar',
  }
}
