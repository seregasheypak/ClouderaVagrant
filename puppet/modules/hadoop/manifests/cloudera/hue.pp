define hadoop::cloudera::hue (
  #Service Wide
  $cherrypy_server_threads = nil,
  $hue_service_safety_valve = nil,
  $time_zone = nil,
  #
  $hue_server_hue_safety_valve = nil, 
  $beeswax_meta_server_port = nil,
  $beeswax_server_heapsize = nil,
  $process_auto_restart = nil,
  $beeswax_hive_conf_safety_valve = nil,
) {
  include 'hadoop::cm_rest_client'
  
   cm_service_wide{"hue-$name":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => hiera(cm_cluster_name),
    scm_service    => hiera(cm_hue_service),
    config => {
      'cherrypy_server_threads' => $cherrypy_server_threads,
      'hue_service_safety_valve' => $hue_service_safety_valve,
      'time_zone' => $time_zone,      
    };
  }  


  cm_service_role_group {   
  "hue_server_config":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => hiera(cm_cluster_name),
    scm_service    => hiera(cm_hue_service),
    scm_role_group => "Hue Server (Default)",
    config         => {
      'hue_server_hue_safety_valve' => "$hue_server_hue_safety_valve",
    };
  "beeswax_server_config":
    ensure         => present,
    scm_host_port  => hiera(cm_host_port),
    scm_cluster    => hiera(cm_cluster_name),
    scm_service    => hiera(cm_hue_service),
    scm_role_group => "Beeswax Server (Default)",
    config         => {
      'beeswax_meta_server_port' => "$beeswax_meta_server_port",
      'beeswax_server_heapsize' => "$beeswax_server_heapsize",
      'process_auto_restart' => "$process_auto_restart",
      'beeswax_hive_conf_safety_valve' => "$beeswax_hive_conf_safety_valve",
    };
  }
}