package cloudera.services.installer.utility

import com.cloudera.api.DataView
import com.cloudera.api.v5.RootResourceV5

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * User: sergey.sheypak
 * Date: 05.01.14
 * Time: 23:12
 *
 * The aim of this class is to force parcel distribution over cluster HOSTS
 */
class ParcelActivator {
    private static final Logger LOG = LoggerFactory.getLogger(ParcelActivator.class)

    static final AVAILABLE_REMOTELY = 'AVAILABLE_REMOTELY'
    static final DOWNLOADING = 'DOWNLOADING'
    static final DOWNLOADED = 'DOWNLOADED'
    static final DISTRIBUTING = 'DISTRIBUTING'
    static final DISTRIBUTED = 'DISTRIBUTED'
    static final ACTIVATED = 'ACTIVATED'

    static final int TEN_SECONDS = 10000
    static final int WAIT_IN_SECONDS = System.getProperty('ParcelStatusChecker.wait.in.seconds', '3600') as int


    String clusterName
    RootResourceV5 root
    List<Map> products

    boolean waitForDownload() {
        LOG.info("Waiting for parcel download: $products")
        checkStatus(DOWNLOADING, AVAILABLE_REMOTELY)
    }

    //TODO: use wait method of CommandApi
    boolean checkStatus(String falseStatus, String unexpectedStatus = null) {
        for (counter in 1..WAIT_IN_SECONDS) {
            sleep(TEN_SECONDS)
            boolean areDownloaded = true
            readParcels().each { status ->
                if (unexpectedStatus != null && status.stage == unexpectedStatus) {
                    LOG.error("${this.class.simpleName} $unexpectedStatus is not allowed state.")
                    throw new RuntimeException("${this.class.simpleName} $unexpectedStatus is not allowed state.")
                }
                if (status.stage == falseStatus) {
                    areDownloaded = false
                }
            }
            if (areDownloaded) {
                return true
            }
        }
    }

    boolean waitForStatus(String expectedStatus) {
        for (counter in 1..WAIT_IN_SECONDS) {
            Thread.sleep(TEN_SECONDS)
            boolean allOk = true
            readParcels().each { parcel ->
                if (!parcel.stage.equals(expectedStatus)) {
                    allOk = false;
                }
            }
            if (allOk) {
                return true
            }
        }
        throw new RuntimeException("Timeout for status $expectedStatus")
    }

    boolean waitForDistribution() {
        LOG.info("Waiting for parcel distirbution: $products")
        checkStatus(DISTRIBUTING)
    }

    boolean waitForActivation() {
        LOG.info("Waiting for parcel activation: $products")
        waitForStatus(ACTIVATED)
    }

    def readParcels() {
        root.clustersResource.getParcelsResource(clusterName).readParcels(DataView.SUMMARY)
    }

    def getParcelResource(String productName, String version) {
        root.getClustersResource().getParcelsResource(clusterName).getParcelResource(productName, version)
    }

    def startDownload() {
        products.each { product ->
            getParcelResource(product.name, product.version).startDownloadCommand().wait()
        }
    }

    def startDistribution() {
        products.each { product ->
            getParcelResource(product.name, product.version).startDistributionCommand()
        }
    }

    def startActivation() {
        products.each { product ->
            getParcelResource(product.name, product.version).activateCommand()
        }
    }


    boolean activate() {
        startDownload()
        waitForDownload()

        startDistribution()
        waitForDistribution()

        startActivation()
        waitForActivation()
    }
}
