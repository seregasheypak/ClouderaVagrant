package cloudera.services.installer.model

import com.cloudera.api.model.ApiConfig
import com.cloudera.api.model.ApiConfigList

/**
 * User: sergey.sheypak
 * Date: 29.12.13
 * Time: 14:18
 */
class ScmConf implements BuiltModel{

    static CDH = [name:'CDH', version:'4.5.0-1.cdh4.5.0.p0.30']
    static IMPALA = [name: 'IMPALA', version: '1.2.3-1.p0.97']
    static PRODUCTS = [CDH, IMPALA]

    def build(){
        ApiConfigList apiConfigList = new ApiConfigList()
        apiConfigList.add(remoteParcelsRepo)
        //apiConfigList.add(automaticalyDownloadedProducts)
        apiConfigList.add(automaticallyDistributeParcels)
        apiConfigList.add(automaticallyDownloadParcels)
        apiConfigList
    }

    def getRemoteParcelsRepo(){
        new ApiConfig(name: 'remote_parcel_repo_urls',
                      value: 'http://archive.cloudera.com/cdh4/parcels/4.5.0.30/,http://archive.cloudera.com/impala/parcels/1.2.3.97/')
    }

    def getAutomaticalyDownloadedProducts(){
        new ApiConfig(name: 'parcel_autodownload_products',
                      value: "${CDH.name},${IMPALA.name}")
    }

    def getAutomaticallyDistributeParcels(){
        new ApiConfig(name: 'distribute_parcels_automatically',
                      value: 'false')
    }

    def getAutomaticallyDownloadParcels(){
        new ApiConfig(name: 'download_parcels_automatically',
                      value: 'false')
    }
}
