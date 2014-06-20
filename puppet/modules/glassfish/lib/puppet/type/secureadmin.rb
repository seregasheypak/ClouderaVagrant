Puppet::Type.newtype(:secureadmin) do
  @doc = "Manage Glassfish secure admin"

  ensurable

  newparam(:name) do
    desc "Secure admin resource name"
    isnamevar
  end

  newparam(:asadminuser) do
    desc "The internal Glassfish user asadmin uses. Default: admin"
    defaultto "admin"
  end
  
  newparam(:adminport) do
    desc "The Glassfish domain admin port."
    defaultto "4848"
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

  newparam(:portbase) do
    desc "The Glassfish domain port base. Default: 4800"
    defaultto "4800"
  end
end
