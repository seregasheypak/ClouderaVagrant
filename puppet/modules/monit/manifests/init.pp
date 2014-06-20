class monit {
	package { 'monit':
		ensure => installed,
	}

	file { 'monit.conf':
		ensure  => present,
		mode    => '0600',
		path    =>  '/etc/monit.conf',
		owner => root,
		group => root,
		source => "puppet:///modules/monit/monit.conf",
		require => Package['monit'],
	}
    ~>
	service { 'monit':
		ensure		=> running,
		hasrestart	=> true,
		hasstatus	=> true,
		subscribe	=> File['monit.conf'],
	}
}