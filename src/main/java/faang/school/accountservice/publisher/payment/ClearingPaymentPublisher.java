package faang.school.accountservice.publisher.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.response.ClearingPaymentResponse;
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
public class ClearingPaymentPublisher extends AbstractPaymentPublisher<ClearingPaymentResponse> {

    @Value("${spring.data.redis.channels.clearing-payment.response}")
    private String topicName;

    public ClearingPaymentPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, ClearingPaymentResponse.class);
    }

    @Override
    public <I> void makeResponse(I input) {
        if (input instanceof Payment payment) {
            setResponse(new ClearingPaymentResponse(payment.getId(), SUCCESS));
        } else {
            log.warn("CancelPaymentPublisher.makeResponse: incompatible type {}", input.getClass());
        }
    }

    @Override
    public <R, E extends Exception> void makeErrorResponse(R request, E exception) {
        if (request instanceof ClearingPaymentRequest clearingRequest) {
            setResponse(new ClearingPaymentResponse(clearingRequest.getOperationId(), FAILED));
        } else {
            log.warn("ClearingPaymentPublisher.makeErrorResponse: incompatible type {}", request.getClass());
        }
    }
}