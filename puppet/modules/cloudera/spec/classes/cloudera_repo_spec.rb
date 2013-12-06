#!/usr/bin/env rspec

require 'spec_helper'

describe 'cloudera::repo', :type => 'class' do

  context 'on a non-supported operatingsystem' do
    let :facts do {
      :osfamily        => 'foo',
      :operatingsystem => 'bar'
    }
    end
    it 'should fail' do
      expect {
        should raise_error(Puppet::Error, /Module cloudera is not supported on bar/)
      }
    end
  end

  context 'on a supported operatingsystem, default parameters' do
    let :facts do {
      :osfamily               => 'RedHat',
      :operatingsystem        => 'CentOS',
      :operatingsystemrelease => '6.3',
      :os_maj_version         => '6',
      :architecture           => 'x86_64'
    }
    end
    it { should contain_yumrepo('cloudera-cdh4').with(
      :descr          => 'Cloudera\'s Distribution for Hadoop, Version 4',
      :enabled        => '1',
      :gpgcheck       => '1',
      :gpgkey         => 'http://archive.cloudera.com/cdh4/redhat/6/x86_64/cdh/RPM-GPG-KEY-cloudera',
      :baseurl        => 'http://archive.cloudera.com/cdh4/redhat/6/x86_64/cdh/4/',
      :priority       => '50',
      :protect        => '0',
      :proxy          => 'absent',
      :proxy_username => 'absent',
      :proxy_password => 'absent'
    )}
    it { should contain_yumrepo('cloudera-impala').with(
      :descr          => 'Impala',
      :enabled        => '1',
      :gpgcheck       => '1',
      :gpgkey         => 'http://archive.cloudera.com/impala/redhat/6/x86_64/impala/RPM-GPG-KEY-cloudera',
      :baseurl        => 'http://archive.cloudera.com/impala/redhat/6/x86_64/impala/1/',
      :priority       => '50',
      :protect        => '0',
      :proxy          => 'absent',
      :proxy_username => 'absent',
      :proxy_password => 'absent'
    )}
    it { should_not contain_yumrepo('cloudera-manager') }
  end

  context 'on a supported operatingsystem, custom parameters' do
    let :facts do {
      :osfamily        => 'RedHat',
      :operatingsystem => 'OracleLinux'
    }
    end

    describe 'ensure => absent' do
      let :params do {
        :ensure => 'absent'
      }
      end
      it { should contain_yumrepo('cloudera-cdh4').with_enabled('0') }
      it { should contain_yumrepo('cloudera-impala').with_enabled('0') }
    end

    describe 'all other parameters' do
      let :params do {
        :cdh_yumserver  => 'http://localhost',
        :cdh_yumpath    => '/somepath/',
        :cdh_version    => '999',
        :ci_yumserver   => 'http://localhost',
        :ci_yumpath     => '/somepath/2/',
        :ci_version     => '777',
        :proxy          => 'http://proxy:3128/',
        :proxy_username => 'myUser',
        :proxy_password => 'myPass'
      }
      end
      it { should contain_yumrepo('cloudera-cdh4').with(
        :gpgkey         => 'http://localhost/somepath/RPM-GPG-KEY-cloudera',
        :baseurl        => 'http://localhost/somepath/999/',
        :proxy          => 'http://proxy:3128/',
        :proxy_username => 'myUser',
        :proxy_password => 'myPass'
      )}
      it { should contain_yumrepo('cloudera-impala').with(
        :gpgkey         => 'http://localhost/somepath/2/RPM-GPG-KEY-cloudera',
        :baseurl        => 'http://localhost/somepath/2/777/',
        :proxy          => 'http://proxy:3128/',
        :proxy_username => 'myUser',
        :proxy_password => 'myPass'
      )}
    end
  end
end
