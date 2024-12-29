package faang.school.accountservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    protected void publish(T event, String topic) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while working with JSON: ", e);
            throw new RuntimeException(e);
        }
    }
}
