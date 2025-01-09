package faang.school.accountservice.publisher.payment.abstracts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractPaymentPublisherTest {
    private static final String JSON = "{\"message\":\"This is a valid message\"}";
    private static final String TOPIC = "topic";

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TestPublisher testPublisher;

    @Test
    void testPublish_jsonParseException() throws JsonProcessingException {
        TestEvent event = new TestEvent("This is a valid message");
        testPublisher.makeResponse(event);

        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("") {
        });

        assertThatThrownBy(testPublisher::publish).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testPublish_successful() throws JsonProcessingException {
        TestEvent event = new TestEvent("This is a valid message");
        testPublisher.makeResponse(event);

        when(objectMapper.writeValueAsString(event)).thenReturn(JSON);

        testPublisher.publish();

        verify(redisTemplate).convertAndSend(TOPIC, JSON);
    }

    @Test
    void testGetInstance_successful() {
        assertThat(testPublisher.getInstance()).isEqualTo(TestEvent.class);
    }

    @Slf4j
    private static class TestPublisher extends AbstractPaymentPublisher<TestEvent> {
        private static final String TOPIC = "topic";

        public TestPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
            super(redisTemplate, objectMapper, TestEvent.class);
        }

        @Override
        public <I> void makeResponse(I input) {
            if (input instanceof TestEvent testEvent) {
                setResponse(testEvent);
            } else {
                throw new IllegalArgumentException("Expected TestEvent, but got " + input.getClass());
            }
        }

        @Override
        public <R, E extends Exception> void makeErrorResponse(R request, E exception) {
            if (request instanceof TestEvent testEvent) {
                setResponse(new TestEvent("This is a not valid message"));
            } else {
                log.warn("TestPublisher.makeErrorResponse: incompatible type {}", request.getClass());
            }
        }

        @Override
        public String getTopicName() {
            return TOPIC;
        }
    }

    @Getter
    @Setter
    private static class TestEvent {
        private String message;

        public TestEvent(String message) {
            this.message = message;
        }
    }
}