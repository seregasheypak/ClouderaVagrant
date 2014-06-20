require 'puppet/provider/asadmin'
Puppet::Type.type(:httplistener).provide(:asadmin, :parent =>
                                           Puppet::Provider::Asadmin) do
  desc "Glassfish httplistener support."
  commands :asadmin => "asadmin"

  def create
    args = []
    args << "create-http-listener "
    args << "--listeneraddress '" + escape(@resource[:listener_address]) + "' "
    args << "--listenerport " + escape(@resource[:listener_port]) + " "
    args << "--default-virtual-server '" + escape(@resource[:virtual_server]) + "' "
    args << "--enabled true --securityenabled true "
    args << escape(@resource[:name])
    asadmin_exec(args)
#    asadmin_exec(["restart-domain"])
  end

  def destroy
    args = []
    args << "delete-http-listener " << "'" + escape(@resource[:name]) + "'"
    asadmin_exec(args)
#    asadmin_exec(["restart-domain"])
  end

  def exists?
    asadmin_exec(["list-http-listeners"]).each do |line|      
      line = line.chomp
      line.strip!
      self.debug "'" + line + "'"
      if @resource[:name] == line
        self.debug @resource[:name] + " EXISTS"
        return true
      end
    end
    self.debug "'" + @resource[:name] + "'" + " DOES NOT EXIST"
    return false
  end
end
