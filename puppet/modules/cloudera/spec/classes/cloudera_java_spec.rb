#!/usr/bin/env rspec

require 'spec_helper'

describe 'cloudera::java', :type => 'class' do

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
      :osfamily        => 'RedHat',
      :operatingsystem => 'CentOS'
    }
    end
    it { should contain_package('jdk').with_ensure('present') }
    it { should contain_file('java-profile.d').with(
      :ensure => 'present',
      :path   => '/etc/profile.d/java.sh',
      :mode   => '0755',
      :owner  => 'root',
      :group  => 'root'
    )}
    it { should contain_exec('java-alternatives').with(
      :command => 'alternatives --install /usr/bin/java java /usr/java/default/jre/bin/java 1600 --slave /usr/bin/keytool keytool /usr/java/default/bin/keytool --slave /usr/bin/rmiregistry rmiregistry /usr/java/default/bin/rmiregistry --slave /usr/lib/jvm/jre jre /usr/java/default/jre --slave /usr/lib/jvm-exports/jre jre_exports /usr/java/default/jre/lib',
      :unless  => 'alternatives --display java | grep -q /usr/java/default/jre/bin/java'
    )}
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
      it { should contain_package('jdk').with_ensure('absent') }
      it { should contain_file('java-profile.d').with_ensure('absent') }
    end

    describe 'ensure => badvalue' do
      let :params do {
        :ensure => 'badvalue'
      }
      end
      it 'should fail' do
        expect {
          should raise_error(Puppet::Error, /ensure parameter must be present or absent/)
        }
      end
    end

    describe 'autoupgrade => true' do
      let :params do {
        :autoupgrade   => true
      }
      end
      it { should contain_package('jdk').with_ensure('latest') }
      it { should contain_file('java-profile.d').with_ensure('present') }
    end

    describe 'autoupgrade => badvalue' do
      let :params do {
        :autoupgrade => 'badvalue'
      }
      end
      it 'should fail' do
        expect {
          should raise_error(Puppet::Error, /"badvalue" is not a boolean./)
        }
      end
    end

  end
end
