package org.onap.sdc.consumer;

import org.onap.sdc.api.consumer.IStatusCallback;
import org.onap.sdc.api.notification.IStatusData;
import org.springframework.stereotype.Component;

/**
 * This is normally implemented by the consumer of this client library
 */
@Component
public class TestStatusCallback implements IStatusCallback {

  @Override
  public void activateCallback(IStatusData data) {}

}
