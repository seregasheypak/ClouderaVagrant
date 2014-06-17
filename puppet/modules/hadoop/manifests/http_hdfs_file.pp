define hadoop::http_hdfs_file(
  $http_file_location = $name,
  $hdfs_dir,
  $hdfs_name,
  $owner = 'hdfs'
  ){
  
  hadoop::download{$hdfs_name:
    uri => $http_file_location,
    before => Exec["hadoop_put $hdfs_name"],
  }    
    
    
  exec {"hadoop_put $hdfs_name":
    command => "/usr/bin/sudo -u hdfs /usr/bin/hadoop fs -put /tmp/$hdfs_name $hdfs_dir/$hdfs_name",
    before => Exec["hadoop_chown $hdfs_name"],
    unless => "/usr/bin/sudo -u hdfs /usr/bin/hadoop fs -test -e $hdfs_dir/$hdfs_name"                         
  }
  
  exec {"hadoop_chown $hdfs_name":
    command => "/usr/bin/sudo -u hdfs /usr/bin/hadoop fs -chown ${owner} $hdfs_dir/$hdfs_name",
    before => Exec["hadoop_chmod $hdfs_name"], 
    unless => "/usr/bin/sudo -u hdfs /usr/bin/hadoop fs -ls $hdfs_dir/$hdfs_name | tail -n 1 | grep -P \".{10}\s+\d+\s+${owner}\"",   
  }
  
  exec {"hadoop_chmod $hdfs_name":
    command => "/usr/bin/sudo -u hdfs /usr/bin/hadoop fs -chmod 777 $hdfs_dir/$hdfs_name",
    unless => "/usr/bin/sudo -u hdfs /usr/bin/hadoop fs -ls $hdfs_dir/$hdfs_name | tail -n 1 | grep rw-rw-rw-",    
  }
  
  
  
}