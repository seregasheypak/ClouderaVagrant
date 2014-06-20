require 'puppet/provider/asadmin'
Puppet::Type.type(:secureadmin).provide(:asadmin,
                                   :parent => Puppet::Provider::Asadmin) do
  desc "Glassfish secure admin support."
  commands :asadmin => "asadmin"

  def create
    asadmin_exec(["enable-secure-admin"])
#    asadmin_exec(["restart-domain"])
  end

  def destroy
    asadmin_exec(["disable-secure-admin"])
#    asadmin_exec(["restart-domain"])
  end

  def exists?
    asadmin_exec(["get secure-admin.enabled"]).each do |line|
      return true if line.include? "secure-admin.enabled=true"
    end
    return false
  end
end
