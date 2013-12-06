# == Class: cloudera::cdh::flume
#
# This class installs the Flume NG packages.
#
# === Parameters:
#
# === Actions:
#
# === Requires:
#
# === Sample Usage:
#
#   class { 'cloudera::cdh::flume': }
#
# === Authors:
#
# Mike Arnold <mike@razorsedge.org>
#
# === Copyright:
#
# Copyright (C) 2013 Mike Arnold, unless otherwise noted.
#
class cloudera::cdh::flume {
  package { 'flume-ng':
    ensure => 'present',
  }

  service { 'flume-ng':
#    ensure     => 'running',
    enable     => false,
    hasstatus  => false,
    hasrestart => true,
    require    => Package['flume-ng'],
  }
}
