package org.onap.sdc.api.notification;

import java.util.Map;
import lombok.Getter;

@Getter
public final class NotificationMessageContext {

    public static final NotificationMessageContext EMPTY = new NotificationMessageContext(null, -1, -1, Map.of());

    private final String topic;
    private final int partition;
    private final long offset;
    private final Map<String, String> headers;

    public NotificationMessageContext(String topic, int partition, long offset, Map<String, String> headers) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.headers = Map.copyOf(headers);
    }
}