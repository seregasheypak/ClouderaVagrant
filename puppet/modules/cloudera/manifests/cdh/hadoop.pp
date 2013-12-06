#
#  Copyright (c) 2011, Cloudera, Inc. All Rights Reserved.
#
#  Cloudera, Inc. licenses this file to you under the Apache License,
#  Version 2.0 (the "License"). You may not use this file except in
#  compliance with the License. You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
#  CONDITIONS OF ANY KIND, either express or implied. See the License for
#  the specific language governing permissions and limitations under the
#  License.
#

class cloudera::cdh::hadoop {
  anchor { 'cloudera::cdh::hadoop::begin': }
  anchor { 'cloudera::cdh::hadoop::end': }

  class { 'cloudera::cdh::hadoop::client':
#    ensure      => $ensure,
#    autoupgrade => $autoupgrade,
    require     => Anchor['cloudera::cdh::hadoop::begin'],
    before      => Anchor['cloudera::cdh::hadoop::end'],
  }

  package { 'hadoop':
    ensure => 'present',
  }

  package { 'hadoop-hdfs':
    ensure => 'present',
  }

  package { 'hadoop-httpfs':
    ensure => 'present',
  }

  package { 'hadoop-mapreduce':
    ensure => 'present',
  }

  package { 'hadoop-yarn':
    ensure => 'present',
  }

  package { 'hadoop-0.20-mapreduce':
    ensure => 'present',
  }

  service { 'hadoop-httpfs':
#    ensure    => 'stopped',
    enable    => false,
    hasstatus => true,
    require   => Package['hadoop-httpfs'],
  }
}
