# Class: glassfish
#
# This module manages glassfish
#
# Parameters:
#
# Actions:
#
# Requires:
#
# Sample Usage:
#
class glassfish (
  $package_name = 'glassfish-web',
  $version,
  $domain,
  $admin_user,
  $admin_password,
  $admin_port = '4848',
  $instance_port = '8080',  
  $jvmoptions = [],
  $allow_encoded_slash = 'true',
  $disable_console_updates = 'true',
  ) {

    $glassfish_user = 'glassfish'
    $glassfish_group = 'glassfish'
    $password_file = '/home/glassfish/.aspass'

    if $disable_console_updates {
        exec {'disable_download': 
            command => '/bin/mv /opt/glassfish-web/glassfish/modules/console-updatecenter-plugin.jar /opt/glassfish-web/glassfish/modules/console-updatecenter-plugin.jar.disabled',
            creates => '/opt/glassfish-web/glassfish/modules/console-updatecenter-plugin.jar.disabled',
            require => Package[$package_name],
            before => Service[$package_name],            
        }
    }else {
        exec {'enable_download': 
            command => '/bin/mv /opt/glassfish-web/glassfish/modules/console-updatecenter-plugin.jar.disabled /opt/glassfish-web/glassfish/modules/console-updatecenter-plugin.jar',
            creates => '/opt/glassfish-web/glassfish/modules/console-updatecenter-plugin.jar',
            require => Package[$package_name],
            before => Service[$package_name],
        }
    }

    package { $package_name:
        ensure  => $version,
    }
    file { 'root.nproc.conf':
        ensure  => present,
        mode    => '0644',
        path    =>  '/etc/security/limits.d/root.nproc.conf',
        owner => root,
        group => root,
        source => "puppet:///modules/glassfish/root.nproc.conf",
        require => Package[$package_name],
    }
    file { 'root.nofile.conf':
        ensure  => present,
        mode    => '0644',
        path    =>  '/etc/security/limits.d/root.nofile.conf',
        owner => root,
        group => root,
        source => "puppet:///modules/glassfish/root.nofile.conf",
        require => Package[$package_name],
    }
    service { $package_name:
        ensure    => running,
        enable    => true,
        require   => Domain[$domain]
    }
    file { $password_file:
        ensure  => present,
        content => "AS_ADMIN_PASSWORD=${admin_password}",
        owner   => $glassfish_user,
        group   => $glassfish_group,
        mode    => 640
    }
    Domain {
        user          => $glassfish_user,
        asadminuser   => $admin_user,
        adminport     => $admin_port,
        passwordfile  => $password_file,
    }
    domain { $domain:
        ensure        => present,
        instanceport  => $instance_port,
        require       => [File[$password_file], Package[$package_name]],
    }
    Secureadmin {
        user          => $glassfish_user,
        asadminuser   => $admin_user,
        adminport     => $admin_port,
        passwordfile  => $password_file,
    }
    secureadmin { $admin_user:
        ensure  => present,
        require => Service[$package_name],
    }
    Systemproperty {
        user          => $glassfish_user,
        asadminuser   => $admin_user,
        adminport     => $admin_port,
        passwordfile  => $password_file,
    }
    systemproperty { "com.sun.grizzly.util.buf.UDecoder.ALLOW_ENCODED_SLASH":
        ensure  => present,
        value   => $allow_encoded_slash,
        require => Service[$package_name],
    }    
    Jvmoption {
        ensure        => present,
        user          => $glassfish_user,
        asadminuser   => $admin_user,
        adminport     => $admin_port,
        passwordfile  => $password_file,
    }
    jvmoption { $jvmoptions:
        require => Service[$package_name],
    }

}
