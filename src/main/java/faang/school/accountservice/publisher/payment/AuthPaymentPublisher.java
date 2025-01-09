package faang.school.accountservice.publisher.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.PaymentRequest;
import faang.school.accountservice.dto.payment.response.PaymentResponse;
import faang.school.accountservice.exception.payment.InsufficientFundsException;
import faang.school.accountservice.model.payment.Payment;
import faang.school.accountservice.publisher.payment.abstracts.AbstractPaymentPublisher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static faang.school.accountservice.model.payment.AccountBalanceStatus.BALANCE_NOT_VERIFIED;
import static faang.school.accountservice.model.payment.AccountBalanceStatus.INSUFFICIENT_FUNDS;
import static faang.school.accountservice.model.payment.AccountBalanceStatus.SUFFICIENT_FUNDS;
import static faang.school.accountservice.model.payment.PaymentResponseStatus.FAILED;
import static faang.school.accountservice.model.payment.PaymentResponseStatus.SUCCESS;

@Slf4j
@Getter
@Component
public class AuthPaymentPublisher extends AbstractPaymentPublisher<PaymentResponse> {

    @Value("${spring.data.redis.channels.auth-payment.response}")
    private String topicName;

    public AuthPaymentPublisher(RedisTemplate<String, Object> redisTemplate,
                                ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, PaymentResponse.class);
    }

    @Override
    public <I> void makeResponse(I input) {
        if (input instanceof Payment payment) {
            setResponse(new PaymentResponse(payment.getId(), SUFFICIENT_FUNDS, SUCCESS));
        } else {
            log.warn("AuthPaymentPublisher.makeResponse called with incompatible type: {}", input.getClass());
        }
    }

    @Override
    public <R, E extends Exception> void makeErrorResponse(R request, E exception) {
        if (request instanceof PaymentRequest paymentRequest) {
            if (exception instanceof InsufficientFundsException) {
                setResponse(new PaymentResponse(paymentRequest.getOperationId(), INSUFFICIENT_FUNDS, FAILED));
            } else {
                setResponse(new PaymentResponse(paymentRequest.getOperationId(), BALANCE_NOT_VERIFIED, FAILED));
            }
        } else {
            log.warn("AuthPaymentPublisher.makeErrorResponse called with incompatible type: {}", request.getClass());
        }
    }
}