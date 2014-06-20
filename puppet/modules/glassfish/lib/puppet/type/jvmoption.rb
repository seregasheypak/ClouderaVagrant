Puppet::Type.newtype(:jvmoption) do
  @doc = "Manage jvm-options of Glassfish domains"

  ensurable

  newparam(:name) do
    desc "The jvm-option."
    isnamevar
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
