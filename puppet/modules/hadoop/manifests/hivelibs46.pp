class hadoop::hivelibs46 {
  
  hadoop::hdfsdir{"/usr/lib/hive/lib":        
  }
  
  hadoop::http_hdfs_file {'http://ci.kyc.megafon.ru:8080/nexus/content/groups/all-repos/com/google/guava/guava/11.0.2/guava-11.0.2.jar':
    hdfs_dir => '/usr/lib/hive/lib',
    hdfs_name => 'guava-11.0.2.jar',
    require => Hadoop::Hdfsdir['/usr/lib/hive/lib'],    
  }
  
  hadoop::http_hdfs_file {'http://ci.kyc.megafon.ru:8080/nexus/content/groups/all-repos/org/apache/hbase/hbase/0.94.15-cdh4.6.0/hbase-0.94.15-cdh4.6.0.jar':
    hdfs_dir => '/usr/lib/hive/lib',
    hdfs_name => 'hbase-0.94.15-cdh4.6.0.jar',
    require => Hadoop::Hdfsdir['/usr/lib/hive/lib'],    
  }
   
  hadoop::http_hdfs_file {'http://ci.kyc.megafon.ru:8080/nexus/content/groups/all-repos/org/apache/hive/hive-hbase-handler/0.10.0-cdh4.6.0/hive-hbase-handler-0.10.0-cdh4.6.0.jar':
    hdfs_dir => '/usr/lib/hive/lib',
    hdfs_name => 'hive-hbase-handler-0.10.0-cdh4.6.0.jar',
    require => Hadoop::Hdfsdir['/usr/lib/hive/lib'],
  }
  
  hadoop::http_hdfs_file {'http://ci.kyc.megafon.ru:8080/nexus/content/groups/all-repos/org/apache/zookeeper/zookeeper/3.4.5-cdh4.6.0/zookeeper-3.4.5-cdh4.6.0.jar':
    hdfs_dir => '/usr/lib/hive/lib',
    hdfs_name => 'zookeeper-3.4.5-cdh4.6.0.jar',
    require => Hadoop::Hdfsdir['/usr/lib/hive/lib'],
  }

}