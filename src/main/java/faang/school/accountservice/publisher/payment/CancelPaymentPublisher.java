package faang.school.accountservice.publisher.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.response.CancelPaymentResponse;
import faang.school.accountservice.model.payment.Payment;
import faang.school.accountservice.publisher.payment.abstracts.AbstractPaymentPublisher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static faang.school.accountservice.model.payment.PaymentResponseStatus.FAILED;
import static faang.school.accountservice.model.payment.PaymentResponseStatus.SUCCESS;

@Slf4j
@Getter
@Component
public class CancelPaymentPublisher extends AbstractPaymentPublisher<CancelPaymentResponse> {

    @Value("${spring.data.redis.channels.cancel-payment.response}")
    private String topicName;

    public CancelPaymentPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, CancelPaymentResponse.class);
    }

    @Override
    public <I> void makeResponse(I input) {
        if (input instanceof Payment payment) {
            setResponse(new CancelPaymentResponse(payment.getId(), SUCCESS));
        } else {
            log.warn("CancelPaymentPublisher.makeResponse: incompatible type {}", input.getClass());
        }
    }

    @Override
    public <R, E extends Exception> void makeErrorResponse(R request, E exception) {
        if (request instanceof CancelPaymentRequest cancelRequest) {
            setResponse(new CancelPaymentResponse(cancelRequest.getOperationId(), FAILED));
        } else {
            log.warn("CancelPaymentPublisher.makeErrorResponse: incompatible type {}", request.getClass());
        }
    }
}

