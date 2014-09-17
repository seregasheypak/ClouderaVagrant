package cloudera.services.installer.model

import com.cloudera.api.model.ApiConfig
import com.cloudera.api.model.ApiConfigList
import com.cloudera.api.model.ApiHostRef
import com.cloudera.api.model.ApiRole
import com.cloudera.api.model.ApiRoleConfigGroup
import com.cloudera.api.model.ApiRoleConfigGroupRef
import com.cloudera.api.model.ApiService
import com.cloudera.api.model.ApiServiceConfig
import com.cloudera.api.model.ApiServiceList
import com.cloudera.api.model.ApiServiceRef

class ServiceYamlBuilder {
    private Map yaml

    public ServiceYamlBuilder(Map source) {
        this.yaml = source
    }

    public ApiServiceList buildService(String serviceType) {
        Map serviceAsMap
        yaml.services.each {
            if (it.type.equals(serviceType)) {
                serviceAsMap = it
            }
        }
        if (serviceAsMap == null) {
            throw new IllegalArgumentException("Service with type $serviceType does not exist in yaml")
        }

        ApiService ourService = new ApiService()

        ourService.displayName = serviceAsMap['display_name']
        ourService.name = serviceAsMap['display_name']
        ourService.type = serviceAsMap['type']

        //build service-wide config
        Map serviceWideProperties = serviceAsMap['service_wide_config']
        if (serviceWideProperties != null) {
            ApiServiceConfig apiConfig = new ApiServiceConfig()
            for (String key : serviceWideProperties.keySet()) {
                apiConfig.add(new ApiConfig(name: key, value: serviceWideProperties.get(key)))
            }
            ourService.config = apiConfig
        }


        List<ApiRoleConfigGroup> roleConfigGroups = []
        List<ApiRole> apiRoles = []
        //create roles
        serviceAsMap.roles.each {
            roleAsMap ->
                ApiRoleConfigGroup roleConfigGroup = new ApiRoleConfigGroup()
                roleConfigGroup.base = true
                roleConfigGroup.roleType = roleAsMap['type']
                roleConfigGroup.name = serviceAsMap['display_name'] + '-' + roleAsMap['type'] + '-BASE'
                roleConfigGroup.displayName = roleAsMap.type + ' (Default)'
                roleConfigGroup.serviceRef = new ApiServiceRef(clusterName: yaml['scm']['cluster_name'], serviceName: serviceAsMap['display_name'])
                List<ApiConfig> apiConfigs = []
                for (String propName : roleAsMap.keySet()) {
                    if (propName.equals('type') || propName.equals('hosts')) {
                        continue
                    }
                    apiConfigs.add(new ApiConfig(name: propName, value: serviceAsMap[propName]))
                }
                roleConfigGroup.config = new ApiConfigList(apiConfigs)

                roleConfigGroups.add(roleConfigGroup)

                //add roles to hosts
                roleAsMap.hosts.each {
                    host ->
                        ApiRole apiRole = new ApiRole(roleConfigGroupRef: new ApiRoleConfigGroupRef(roleConfigGroupName: roleConfigGroup.name),
                                hostRef: new ApiHostRef(hostId: host.name),
                                name: roleConfigGroup.roleType + "-" + asRoleNameSuffix(host.name),
                                type: roleConfigGroup.roleType)
                        apiRoles.add(apiRole)
                }
        }

        ourService.roleConfigGroups = roleConfigGroups
        ourService.roles = apiRoles

        new ApiServiceList(services: [ourService])
    }


    static asRoleNameSuffix(String hostName) {
        hostName.replace('.', '_').replace('-', '')
    }
}
