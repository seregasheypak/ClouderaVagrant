package cloudera.services.installer

import groovyx.net.http.RESTClient

/**
 * User: sergey.sheypak
 * Date: 07.12.13
 * Time: 2:41
 */
class Main {

    public static void main(String... args){
        restClient.delete(path: 'clusters/cloudera-cluster')
        def resp = restClient.post(
                path:'clusters',
                body: Main.class.classLoader.getResourceAsStream('01_CreateCluster.json').readLines().join('\n'),
                requestContentType: 'application/json'
        )
        println(resp.data)

        resp = restClient.post(
                path:'clusters/cloudera-cluster/hosts',
                body: Main.class.classLoader.getResourceAsStream('02_AddHosts.json').readLines().join('\n'),
                requestContentType: 'application/json'
        )
        println(resp.data)

        restClient.post(
                path:'clusters/cloudera-cluster/services',
                body: Main.class.classLoader.getResourceAsStream('03_AddServices.json').readLines().join('\n'),
                requestContentType: 'application/json'
        )
        println(resp.data)
    }

    static def cmDeployment(){
        restClient.get(path: 'cm/deployment')
        //[timestamp:2013-12-07T12:12:45.316Z, clusters:[[name:cloudera-cluster, version:CDH4, services:[], parcels:[]]], hosts:[[hostId:vm-cluster-node1.localdomain, ipAddress:10.211.55.101, hostname:vm-cluster-node1.localdomain, rackId:/default, config:[items:[]]], [hostId:vm-cluster-node2.localdomain, ipAddress:10.211.55.102, hostname:vm-cluster-node2.localdomain, rackId:/default, config:[items:[]]], [hostId:vm-cluster-node3.localdomain, ipAddress:10.211.55.103, hostname:vm-cluster-node3.localdomain, rackId:/default, config:[items:[]]]], users:[[name:admin, roles:[ROLE_ADMIN], pwHash:f570c8c9467f4eab8974a1e4927466e9d6f7b5b98122be14de6170d1610f1383, pwSalt:-1120292342220520548, pwLogin:true]], versionInfo:[version:4.8.0, buildUser:jenkins, buildTimestamp:20131125-1015, gitHash:48c25adb872f94de22b61868e82700217853b60e, snapshot:false], managerSettings:[items:[[name:CLUSTER_STATS_START, value:10/25/2012 9:30], [name:ENABLE_API_DEBUG, value:true], [name:PUBLIC_CLOUD_STATUS, value:NOT_ON_PUBLIC_CLOUD]]], allHostsConfig:[items:[]], peers:[]]
    }

    static def cmConfig(){
        restClient.get(path: 'cm/config')
        //[items:[[name:CLUSTER_STATS_START, value:10/25/2012 9:30], [name:ENABLE_API_DEBUG, value:true]]
    }

    static def createCluster(){

        def resp = restClient.post(
                path:'clusters',
                body: Main.class.classLoader.getResourceAsStream('01_CreateCluster.json').readLines().join('\n'),
                requestContentType: 'application/json'
        )
        println resp
    }

    static def getRestClient(){
        def restClient = new RESTClient( 'http://10.211.55.101:7180/api/v4/' )
        restClient.auth.basic 'admin', 'admin'
        restClient
    }

}


