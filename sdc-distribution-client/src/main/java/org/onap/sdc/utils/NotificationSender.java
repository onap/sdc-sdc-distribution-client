package org.onap.sdc.utils;

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaPublisher;
import fj.data.Either;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientResultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(NotificationSender.class);
    private static final long PUBLISHER_CLOSING_TIMEOUT = 10L;
    private static final long SLEEP_TIME = 1000L;

    private final Either<CambriaBatchingPublisher, IDistributionClientResult> cambriaPublisher;
    private final List<String> brokerServers;

    public NotificationSender(Either<CambriaBatchingPublisher, IDistributionClientResult> cambriaPublisher, List<String> brokerServers) {
        this.cambriaPublisher = cambriaPublisher;
        this.brokerServers = brokerServers;
    }

    public IDistributionClientResult send(String status) {
        log.info("DistributionClient - sendStatus");
        if (cambriaPublisher.isRight()) {
            return cambriaPublisher.right().value();
        }
        CambriaBatchingPublisher publisher = cambriaPublisher.left().value();

        return sendStatus(status, publisher);
    }

    private DistributionClientResultImpl sendStatus(String status, CambriaBatchingPublisher publisher) {
        DistributionClientResultImpl distributionResult;
        try {
            log.debug("Publisher server list: {}", brokerServers);
            log.debug("Trying to send status: {}", status);
            publisher.send("MyPartitionKey", status);
            Thread.sleep(SLEEP_TIME);
        } catch (IOException e) {
            log.error("DistributionClient - sendDownloadStatus. Failed to send download status");
        } catch (InterruptedException e) {
            log.error("DistributionClient - sendDownloadStatus. thread was interrupted");
            Thread.currentThread().interrupt();
        } finally {
            distributionResult = closePublisher(publisher);
        }
        return distributionResult;
    }

    private DistributionClientResultImpl closePublisher(CambriaBatchingPublisher publisher) {
        DistributionClientResultImpl distributionResult = new DistributionClientResultImpl(DistributionActionResultEnum.GENERAL_ERROR, "Failed to send status");
        try {
            List<CambriaPublisher.message> notSentMessages = publisher.close(PUBLISHER_CLOSING_TIMEOUT, TimeUnit.SECONDS);
            if (notSentMessages.isEmpty()) {
                distributionResult = new DistributionClientResultImpl(DistributionActionResultEnum.SUCCESS, "Messages successfully sent");
            } else {
                log.debug("DistributionClient - sendDownloadStatus. {} messages were not sent", notSentMessages.size());
            }
        } catch (IOException e) {
            log.error("DistributionClient - sendDownloadStatus. Failed to send messages and close publisher.");
        } catch (InterruptedException e) {
            log.error("DistributionClient - sendDownloadStatus. Failed to send messages and close publisher.");
            Thread.currentThread().interrupt();
        }
        return distributionResult;
    }
}
