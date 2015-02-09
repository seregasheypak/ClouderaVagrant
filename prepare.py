from string import Template

def config():    
    config = {
        'master_name': 'vm-cluster-node1',
        'slave1_name': 'vm-cluster-node2',
        'slave2_name': 'vm-cluster-node3',
        'slave3_name': 'vm-cluster-node4',
        'slave4_name': 'vm-cluster-node5',
        'master_ip': '10.211.55.101',
        'slave1_ip': '10.211.55.102',
        'slave2_ip': '10.211.55.103',
        'slave3_ip': '10.211.55.104',
        'slave4_ip': '10.211.55.104',
    }
    return config

if __name__ == "__main__":    
    conf = config()
    with open ("vagrant/Vagrantfile.tmpl", "r") as vagrantTmlFile:
        vagrantTmpl = vagrantTmlFile.read()
        content = Template(vagrantTmpl).safe_substitute(conf)
        vagrantFile = open("vagrant/Vagrantfile", "w")
        vagrantFile.write(content)

    with open ("puppet/manifests/site.pp.tmpl", "r") as siteTmlFile:
        siteTmpl = siteTmlFile.read()
        content = Template(siteTmpl).safe_substitute(conf)
        siteFile = open("puppet/manifests/site.pp", "w")
        siteFile.write(content)


