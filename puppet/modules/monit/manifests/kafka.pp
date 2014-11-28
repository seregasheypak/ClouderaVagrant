class monit::kafka inherits monit {
  
  file{'/etc/monit.d/kafka':
    ensure  => present,
    mode    => '0600',
    path    =>  '/etc/monit.d/kafka',
    owner => root,
    group => root,
    source => "puppet:///modules/monit/kafka",
    notify => Service['kafka'],
    require => Package['kafka'],
  }
}