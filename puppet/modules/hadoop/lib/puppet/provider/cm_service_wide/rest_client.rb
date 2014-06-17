require 'rest_client' if Puppet.features.rest_client?
require 'json'


Puppet::Type.type(:cm_service_wide).provide(:rest_client) do

	confine :feature => :rest_client

	def make_url
		scm_host_port = @resource[:scm_host_port]
		scm_cluster = URI.escape(@resource[:scm_cluster])
		scm_service = URI.escape(@resource[:scm_service])
		return "http://#{scm_host_port}/api/v4/clusters/#{scm_cluster}/services/#{scm_service}/config"		
	end

	def get_config(uri)
		response = RestClient.get uri, {:params => {"view" => "full"}}
		parsed = JSON.parse(response)
		return parsed["items"]
	end	

	def get_config_delta(config_list, config_hash)
		result_config = []
		config_hash.each do |key, value|
			config_list.each do |config_element|  #iterate through all config settings, {:params => {"view" => "full"}}                
                if value == 'nil'
                  value = nil
                end
				if config_element["name"] == key 
					current_value = config_element["value"] #is nil if it is default setting on cloudera
					default_value = config_element["default"]
					if value.nil? 						
						unless current_value.nil?
							result_config.push({"name" => key})
						end
					elsif value != current_value
						if value == default_value
							result_config.push({"name" => key})
						else
							result_config.push({"name" => key, "value" => value})
						end
					end
				end
			end
		end
		return result_config
	end

	def set_config(uri, config_list)
		unless config_list.empty?
			response = RestClient.put uri, {"items" => config_list}.to_json, :content_type => :json, :accept => :json
			parsed = JSON.parse(response)
			return parsed["items"]
		else
			return []
		end
	end


	def create
		current_config = get_config(make_url)
		config_delta = get_config_delta(current_config, @resource[:config])
		set_config(make_url, config_delta)
	end

    def destroy
    end

	def exists?
		current_config = get_config(make_url)
		config_delta = get_config_delta(current_config, @resource[:config])
        self.debug "config_delta: #{config_delta}"        
		return config_delta.empty?
	end

end
