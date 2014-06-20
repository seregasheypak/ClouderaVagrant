Puppet::Type.newtype(:systemproperty) do
  @doc = "Manage system-properties of Glassfish domains"

  #ensurable

  newparam(:name) do
    desc "The system property key."
    isnamevar
  end

  newparam(:value) do
    desc "The system property value."
    defaultto ""
  end

  newproperty(:ensure) do
    newvalue(:present) do
      provider.create_or_update
    end
    newvalue(:absent) do
      provider.destroy
    end
    def retrieve
      current_value = provider.get
      if current_value == nil
        return :absent
      elsif current_value != @resource['value']
        return :out_of_sync
      else
        return :present
      end
    end
  end

  newparam(:adminport) do
    desc "The Glassfish domain admin port."
    defaultto "4848"
  end

  newparam(:asadminuser) do
    desc "The internal Glassfish user asadmin uses. Default: admin"
    defaultto "admin"
  end

  newparam(:passwordfile) do
    desc "The file containing the password for the user."
  end

  newparam(:user) do
    desc "The user to run the command as."

    validate do |user|
      unless Puppet.features.root?
        self.fail "Only root can execute commands as other users"
      end
    end
  end
end
