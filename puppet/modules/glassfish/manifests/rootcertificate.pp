define glassfish::rootcertificate(      
  $file_source,
  $crt_alias,
  $keystorepass         = 'changeit',  
){
  #keytool -genkey -keyalg RSA -alias scoring-service-root -keystore scoring-service.jks -storepass changeit -keypass changeit -validity 360 -keysize 2048 -dname "CN=MegaFon, OU=MegaFon, O=MegaFon, L=Moscow, ST=Moscow, C=RU"
  #keytool -export -alias scoring-service-root -file scoring-service-root.crt -keystore scoring-service.jks
  
  file{'/var/lib/glassfish':
    ensure => directory,
  }->
  file{$name:
    source => $file_source,
    path => "/var/lib/glassfish/$name", 
  }
  ->
  exec{"keytool -import -trustcacerts -noprompt -alias $crt_alias -file /var/lib/glassfish/$name -storepass $keystorepass -keystore /opt/glassfish-web/glassfish/domains/kyc-domain/config/cacerts.jks":   
    onlyif => "/usr/bin/test -z `keytool -list -storepass $keystorepass -keystore /opt/glassfish-web/glassfish/domains/kyc-domain/config/cacerts.jks | grep $crt_alias`",    
    path => ['/usr/bin', '/usr/java/default/bin'],
  }
    
}