define hadoop::hdfsdir(
    $path = $name,
    $owner = 'hdfs'
    ) {

    $hdfs_user = 'hdfs'
    $mkdir_cmd = "/usr/bin/hadoop fs -mkdir ${path}"
    $check_existence_cmd = "/usr/bin/hadoop fs -test -e ${path}"
    $chown_cmd = "/usr/bin/hadoop fs -chown ${owner} ${path}"
    $check_owner_cmd = "/usr/bin/test `/usr/bin/hadoop fs -stat %u ${path}` = ${owner}"

    exec { $mkdir_cmd:
        user    => $hdfs_user,
        unless  => $check_existence_cmd,
    }

    exec { $chown_cmd:
        user        => $hdfs_user,
        unless      => $check_owner_cmd,
        subscribe   => Exec[$mkdir_cmd],
    }

/*    if ($is_virtual)
    {
        Class['hadoop::pseudodistributed'] -> Hadoop::Hdfsdir[$name]
    }*/
}