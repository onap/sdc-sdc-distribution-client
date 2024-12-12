package org.onap.sdc.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.onap.sdc.IntegrationTest;
import org.onap.sdc.api.notification.StatusMessage;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.DistributionStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EnableAutoConfiguration
@EmbeddedKafka(topics = "someTopic")
@SpringBootTest(classes = NotificationPublisher.class)
public class NotificationPublisherTest extends IntegrationTest {

  @Autowired
  NotificationPublisher publisher;

  @Test
  void thatStatusMessageCanBePublished() {
    StatusMessage statusMessage = new StatusMessage("distributionId",
     "consumerId", 0, "example.com", DistributionStatusEnum.NOTIFIED);
    IDistributionClientResult result = publisher.publishStatusMessage("someTopic", statusMessage);

    assertEquals(DistributionActionResultEnum.SUCCESS, result.getDistributionActionResult());
  }
}
