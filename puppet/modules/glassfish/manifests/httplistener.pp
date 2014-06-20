define glassfish::httplistener(
  $asadminuser = 'devops',      
  $passwordfile = '/home/glassfish/.aspass',
  $user         = 'glassfish',
  $listener_address,
  $listener_port,
){
  
  
  httplistener{$name:
    ensure  => present,   
    asadminuser => 'devops',    
    passwordfile => '/tmp/.aspass',
    user         => 'glassfish',
    listener_address => 'test2-glassfish.kyc.megafon.ru',
    listener_port => 9898,
  }
}