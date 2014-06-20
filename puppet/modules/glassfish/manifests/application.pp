define glassfish::application(
  $asadminuser = 'devops',      
  $passwordfile = '/home/glassfish/.aspass',
  $user         = 'glassfish',
  $contextroot = $name,
  $war_name = "${name}.war",
){
  
  file{"/tmp/$war_name":
    ensure => file,
    source => "puppet:///modules/glassfish/$war_name",
  }
  ->
  application{$name:
    ensure  => present,
    source => "/tmp/$war_name",
    contextroot => $contextroot,   
    asadminuser => 'devops',    
    passwordfile => '/tmp/.aspass',
    user         => 'glassfish',    
    domaindir => '/opt/glassfish-web/glassfish/domains/',
    require => Class['glassfish'],
  }
}