# == Class: cloudera::cdh::impala
#
# This class installs the Impala packages.
#
# === Parameters:
#
# === Actions:
#
# === Requires:
#
# === Sample Usage:
#
#   class { 'cloudera::cdh::impala': }
#
# === Authors:
#
# Mike Arnold <mike@razorsedge.org>
#
# === Copyright:
#
# Copyright (C) 2013 Mike Arnold, unless otherwise noted.
#
class cloudera::cdh::impala {
  package { 'impala':
    ensure => 'present',
  }

  package { 'impala-shell':
    ensure => 'present',
  }
}
