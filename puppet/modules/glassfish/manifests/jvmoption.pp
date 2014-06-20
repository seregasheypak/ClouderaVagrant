define glassfish::jvmoption(
    $option = $name
    ) {

    jvmoption { $option:
        ensure  => present,
    }

}