class timezone::updater {

  $tzupdater = '/usr/sbin/tzupdater.jar'
  notify{'timezone_updater':
    message => 'updating timezone',
  }
  ->
  file { $tzupdater:
    ensure  => file,
    source  => 'puppet:///modules/timezone/tzupdater.jar',
  }
  ->
  exec { "tzupdate":
    unless => "sudo /usr/bin/java -jar $tzupdater -t",
    command => "sudo /usr/bin/java -jar $tzupdater -u",
    user => root,
  }
}
