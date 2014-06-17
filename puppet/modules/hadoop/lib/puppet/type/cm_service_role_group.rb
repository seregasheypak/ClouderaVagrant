Puppet::Type.newtype(:cm_service_role_group) do
	@doc = "Enforces some CM config"
    ensurable

  	newparam(:name, :namevar=>true) do
    	desc 'The name of the config set.'
  	end

    newparam(:scm_host_port) do
      desc 'SCM login:password@host:port'
    end

    newparam(:scm_cluster) do
      desc 'SCM cluster name, like "Cluster 1 - CDH4"'
    end

    newparam(:scm_service) do
      desc 'SCM service name, like "oozie1"'
    end

    newparam(:scm_role_group) do
      desc 'SCM Role Group Display Name, like "Oozie Server (Default)"'
    end

  	newparam(:config) do
    	desc 'Config hash'
  	end

 end
