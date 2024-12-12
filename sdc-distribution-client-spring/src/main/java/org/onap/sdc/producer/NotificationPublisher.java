package org.onap.sdc.producer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(NotificationPublisher.class)
public class NotificationPublisher {

}
