package faang.school.accountservice.integration.config.listener;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.Topic;

public interface TestEventMessageListener extends MessageListener {
    Topic getTopic();

    String getReceivedMessage();
}
