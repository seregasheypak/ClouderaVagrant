class kafka::hiera(
  $kafka_version,
  $metrics_enabled = false
) {
	if hiera(kafka_install, 'no') == 'yes' {
		$kafka_brokers = hiera_hash(kafka_brokers, 'unknown')

		if member(keys($kafka_brokers), $fqdn) {
			class {'kafka':
			  version => $kafka_version
			}

      $broker_log_dirs = $kafka_brokers[$fqdn]['data_dirs']
      $zookeeper_hosts = hiera_array(kafka_zookeepers)
      $zookeeper_chroot = hiera(kafka_zookeeper_root)

      $kafka_dir = '/opt/kafka'

      $zookeeper_hosts_str = join($zookeeper_hosts, ',')
      exec {'create-zk-root':
        command => "$kafka_dir/bin/kafka-run-class.sh org.apache.zookeeper.ZooKeeperMain -server $zookeeper_hosts_str create $zookeeper_chroot null",
        unless => "$kafka_dir/bin/kafka-run-class.sh org.apache.zookeeper.ZooKeeperMain -server $zookeeper_hosts_str ls / | tail -1 | grep kafka",
        require => Class['kafka'],
      }

			class {'kafka::server':
			    brokers => $kafka_brokers,
			    log_dirs => $broker_log_dirs,
			    zookeeper_hosts => $zookeeper_hosts,
			    zookeeper_chroot => $zookeeper_chroot,
			    auto_create_topics_enable => hiera(kafka_autocreate_topics, false),
			    num_io_threads => size($broker_log_dirs),
          log_retention_minutes       => hiera(kafka_log_retention_minutes),
          controlled_shutdown_enable  => hiera(controlled_shutdown_enable),
          replica_lag_max_messages    => hiera(kafka_replica_lag_max_messages, -1),
          replica_fetch_max_bytes     => hiera(kafka_replica_fetch_max_bytes, -1),
          num_replica_fetchers        => hiera(kafka_num_replica_fetchers, -1),
			    require => Class['kafka'],
      }
      
      class{'monit::kafka':
        require => Class['kafka'],
      }

      if ($metrics_enabled) {
        class { 'kafka::metrics':
          zookeeper_hosts  => $zookeeper_hosts,
          zookeeper_chroot => $zookeeper_chroot,
          update_interval  => 15,
          expire_interval  => 60,
          require          => Class['kafka'],
        }
      }

      tune_fs::change_mountoption {"kafka_mount_flags":
        mountpoint_count      => 10,
        fs_mount              => "/data/disk",
        fs_options            => ["defaults", "noatime", "nodiratime", "nobh", "delalloc"],
        state                 => "set"
      }
    }
	} else {
	    notify {'No need to install Kafka in this environment':}
	}
}