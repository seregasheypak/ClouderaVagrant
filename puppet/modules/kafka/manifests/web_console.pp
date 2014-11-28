class kafka::web_console {
  class { 'play':
    version => "2.2.1",
    user    => "devops",
  }

  play::application { "kafka-web-console":
    ensure => running,
    path   => "/home/devops/git-repos/kafka-web-console",
  }

}