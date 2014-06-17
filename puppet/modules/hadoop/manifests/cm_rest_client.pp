class hadoop::cm_rest_client {

    package { 'mime-types':
        ensure   => '1.25',
        provider => 'pe_gem',
    }

    package { 'rest-client':
        ensure   => 'installed',
        provider => 'pe_gem',
        require  => Package['mime-types'],
    }

}