package cloudera.services.installer.utility

import com.cloudera.api.DataView
import com.cloudera.api.v5.RootResourceV5

/**
 * User: sergey.sheypak
 * Date: 05.01.14
 * Time: 23:12
 *
 * The aim of this class is to force parcel distribution over cluster hosts
 */
class ParcelActivator {
    static final AVAILABLE_REMOTELY = 'AVAILABLE_REMOTELY'
    static final DOWNLOADING = 'DOWNLOADING'
    static final DOWNLOADED = 'DOWNLOADED'
    static final DISTRIBUTING = 'DISTRIBUTING'
    static final DISTRIBUTED = 'DISTRIBUTED'
    static final ACTIVATED = 'ACTIVATED'

    static final int TEN_SECONDS = 10000
    static final int WAIT_IN_SECONDS = System.getProperty('ParcelStatusChecker.wait.in.seconds', '3600') as int


    String clusterName
    RootResourceV5 rootResource

    boolean waitForParcelsDownload(){
        for (counter in 1..WAIT_IN_SECONDS){
            sleep(TEN_SECONDS)
            boolean areDownloaded = true
            rootResource.clustersResource.getParcelsResource(clusterName).readParcels(DataView.SUMMARY).each { status ->
                if(status.stage == AVAILABLE_REMOTELY){
                    throw new RuntimeException("${this.class.simpleName} don't know how to control parcels. $AVAILABLE_REMOTELY is not allowed state. You need to issue downloadCommand before using this class")
                }
                if(status.stage == DOWNLOADING){
                    areDownloaded = false
                }
            }
            if(areDownloaded){
                return true
            }
        }
    }
}
