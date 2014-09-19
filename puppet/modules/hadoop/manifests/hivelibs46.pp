class hadoop::hivelibs46 {
  
  hadoop::hdfsdir{"/usr/lib/hive/lib":        
  }
  
  hadoop::hdfs_file {'guava-11.0.2.jar':
    hdfs_dir => '/usr/lib/hive/lib',
    hdfs_name => 'guava-11.0.2.jar',
    require => Hadoop::Hdfsdir['/usr/lib/hive/lib'],    
  }
  
  hadoop::hdfs_file {'hbase-0.94.15-cdh4.6.0.jar':
    hdfs_dir => '/usr/lib/hive/lib',
    hdfs_name => 'hbase-0.94.15-cdh4.6.0.jar',
    require => Hadoop::Hdfsdir['/usr/lib/hive/lib'],    
  }
   
  hadoop::hdfs_file {'hive-hbase-handler-0.10.0-cdh4.6.0.jar':
    hdfs_dir => '/usr/lib/hive/lib',
    hdfs_name => 'hive-hbase-handler-0.10.0-cdh4.6.0.jar',
    require => Hadoop::Hdfsdir['/usr/lib/hive/lib'],
  }
  
  hadoop::hdfs_file {'zookeeper-3.4.5-cdh4.6.0.jar':
    hdfs_dir => '/usr/lib/hive/lib',
    hdfs_name => 'zookeeper-3.4.5-cdh4.6.0.jar',
    require => Hadoop::Hdfsdir['/usr/lib/hive/lib'],
  }

}