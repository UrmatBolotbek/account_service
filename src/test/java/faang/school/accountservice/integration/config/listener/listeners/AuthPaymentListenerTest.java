package faang.school.accountservice.integration.config.listener.listeners;

import faang.school.accountservice.integration.config.listener.TestEventMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class AuthPaymentListenerTest implements TestEventMessageListener {
    @Value("${spring.data.redis.channels.auth-payment.response}")
    private String topicName;
    private String receivedMessage;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        receivedMessage = message.toString();
    }

    @Override
    public Topic getTopic() {
        return new ChannelTopic(topicName);
    }

    @Override
    public String getReceivedMessage() {
        return receivedMessage;
    }
}
