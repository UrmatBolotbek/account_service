package faang.school.accountservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.PaymentRequest;
import faang.school.accountservice.dto.payment.response.PaymentResponse;
import faang.school.accountservice.model.payment.Payment;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "redis")
@Component
public class AuthPaymentPublisher extends AbstractPublisher<PaymentResponse> {
    @Value("${spring.data.redis.channel.auth-payment.response}")
    private String topicName;

    public AuthPaymentPublisher(RedisTemplate<String, Object> redisTemplate,
                                ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void makeResponse(Object... args) {
        Payment authPayment = (Payment) args[0];
        setResponse(new AuthPaymentResponse(authPayment.getId(), SUFFICIENT_FUNDS, SUCCESS));
    }

    public void makeErrorResponse(Object... args) {
        PaymentRequest paymentRequest = (PaymentRequest) args[0];
        Exception exception = (Exception) args[1];

        if (exception instanceof ValidationException) {
            setResponse(new AuthPaymentResponse(authPaymentRequest.getOperationId(), INSUFFICIENT_FUNDS, FAILED));
        } else {
            setResponse(new AuthPaymentResponse(authPaymentRequest.getOperationId(), BALANCE_NOT_VERIFIED, FAILED));
        }
    }
}
