package faang.school.accountservice.integration.service.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.dto.payment.request.PaymentRequest;
import faang.school.accountservice.dto.payment.response.CancelPaymentResponse;
import faang.school.accountservice.dto.payment.response.ClearingPaymentResponse;
import faang.school.accountservice.dto.payment.response.ErrorPaymentResponse;
import faang.school.accountservice.dto.payment.response.PaymentResponse;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.integration.config.listener.listeners.AuthPaymentListenerTest;
import faang.school.accountservice.integration.config.listener.listeners.CancelPaymentListenerTest;
import faang.school.accountservice.integration.config.listener.listeners.ClearingPaymentListenerTest;
import faang.school.accountservice.integration.config.listener.listeners.ErrorPaymentListenerTest;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.payment.AccountBalanceStatus;
import faang.school.accountservice.model.payment.Category;
import faang.school.accountservice.model.payment.Payment;
import faang.school.accountservice.model.payment.PaymentResponseStatus;
import faang.school.accountservice.model.payment.PaymentStatus;
import faang.school.accountservice.repository.PaymentRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.util.BaseContextTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import static faang.school.accountservice.model.payment.PaymentStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = "/test-sql/insert-default-accounts-and-balances.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/test-sql/truncate-balance-account.sql", executionPhase = AFTER_TEST_METHOD)
public class PaymentServiceIntegrationTest extends BaseContextTest {

    private static final Long SOURCE_ACCOUNT_ID = 1L;
    private static final Long TARGET_ACCOUNT_ID = 2L;
    private static final Long SOURCE_BALANCE_ID = 3L;
    private static final Long TARGET_BALANCE_ID = 4L;
    private static final UUID OPERATION_ID = UUID.fromString("9c500958-a278-11ef-b864-0242ac120002");
    private static final BigDecimal SOURCE_BALANCE_ACTUAL_BALANCE = BigDecimal.valueOf(1000);
    private static final BigDecimal SOURCE_BALANCE_AUTH_BALANCE = BigDecimal.valueOf(0);
    private static final BigDecimal TARGET_BALANCE_ACTUAL_BALANCE = BigDecimal.valueOf(0);
    private static final BigDecimal TARGET_BALANCE_AUTH_BALANCE = BigDecimal.valueOf(0);
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(20);
    private static final BigDecimal AMOUNT_MORE_THAN_CURRENT_BALANCE = BigDecimal.valueOf(2000);
    private static final Currency CURRENCY = Currency.USD;
    private static final Category CATEGORY = Category.OTHER;

    @Value("${spring.data.redis.channels.auth-payment.request}")
    private String authPaymentRequestTopicName;

    @Value("${spring.data.redis.channels.clearing-payment.request}")
    private String clearingPaymentRequestTopicName;

    @Value("${spring.data.redis.channels.cancel-payment.request}")
    private String cancelPaymentRequestTopicName;

    @Value("${spring.data.redis.channels.error-payment.request}")
    private String errorPaymentRequestTopicName;

    @Autowired
    private AuthPaymentListenerTest authPaymentListener;

    @Autowired
    private ClearingPaymentListenerTest clearingPaymentListener;

    @Autowired
    private CancelPaymentListenerTest cancelPaymentListener;

    @Autowired
    private ErrorPaymentListenerTest errorPaymentListener;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testAuthorizePayment_successful() throws JsonProcessingException {
        PaymentRequest authPaymentRequest = PaymentRequest.builder()
                .operationId(OPERATION_ID)
                .sourceAccountId(SOURCE_ACCOUNT_ID)
                .targetAccountId(TARGET_ACCOUNT_ID)
                .amount(AMOUNT)
                .currency(CURRENCY)
                .category(CATEGORY)
                .build();
        String authPaymentRequestJson = objectMapper.writeValueAsString(authPaymentRequest);
        redisTemplate.convertAndSend(authPaymentRequestTopicName, authPaymentRequestJson);

        sleep(200);

        PaymentResponse expected = PaymentResponse.builder()
                .operationId(authPaymentRequest.getOperationId())
                .status(AccountBalanceStatus.SUFFICIENT_FUNDS)
                .paymentResponseStatus(PaymentResponseStatus.SUCCESS)
                .build();
        String actualJson = authPaymentListener.getReceivedMessage();
        PaymentResponse actual = objectMapper.readValue(actualJson, PaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE.subtract(AMOUNT))).isZero();
        assertThat(sourceBalance.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE.add(AMOUNT))).isZero();

        Payment payment = paymentRepository.findById(authPaymentRequest.getOperationId()).orElseThrow();
        assertThat(payment.getSourceBalance().getId()).isEqualTo(SOURCE_BALANCE_ID);
        assertThat(payment.getTargetBalance().getId()).isEqualTo(TARGET_BALANCE_ID);
        assertThat(payment.getAmount().compareTo(AMOUNT)).isZero();
        assertThat(payment.getStatus()).isEqualTo(ACTIVE);
        assertThat(payment.getCategory()).isEqualTo(CATEGORY);
    }

    @Test
    void testAuthorizePayment_validationException() throws JsonProcessingException {
        PaymentRequest authPaymentRequest = PaymentRequest.builder()
                .operationId(OPERATION_ID)
                .sourceAccountId(SOURCE_ACCOUNT_ID)
                .targetAccountId(TARGET_ACCOUNT_ID)
                .amount(AMOUNT_MORE_THAN_CURRENT_BALANCE)
                .currency(CURRENCY)
                .category(CATEGORY)
                .build();
        String authPaymentRequestJson = objectMapper.writeValueAsString(authPaymentRequest);
        redisTemplate.convertAndSend(authPaymentRequestTopicName, authPaymentRequestJson);

        sleep(200);

        PaymentResponse expected = PaymentResponse.builder()
                .operationId(authPaymentRequest.getOperationId())
                .status(AccountBalanceStatus.INSUFFICIENT_FUNDS)
                .paymentResponseStatus(PaymentResponseStatus.FAILED)
                .build();
        String actualJson = authPaymentListener.getReceivedMessage();
        PaymentResponse actual = objectMapper.readValue(actualJson, PaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE)).isZero();
        assertThat(sourceBalance.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE)).isZero();

        Optional<Payment> payment = paymentRepository.findById(authPaymentRequest.getOperationId());
        assertThat(payment).isEmpty();
    }

    @Test
    void testAuthPayment_optimisticLockException() throws JsonProcessingException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Thread parallelUpdateThread = new Thread(() -> {
            try {
                IntStream.rangeClosed(0, 100).forEach(i -> {
                    Balance sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
                    sb.setActualBalance(sb.getActualBalance().add(BigDecimal.ONE));
                    balanceRepository.save(sb);
                });
            } finally {
                latch.countDown();
            }
        });
        parallelUpdateThread.start();

        PaymentRequest authPaymentRequest = PaymentRequest.builder()
                .operationId(OPERATION_ID)
                .sourceAccountId(SOURCE_ACCOUNT_ID)
                .targetAccountId(TARGET_ACCOUNT_ID)
                .amount(AMOUNT)
                .currency(CURRENCY)
                .category(CATEGORY)
                .build();
        String authPaymentRequestJson = objectMapper.writeValueAsString(authPaymentRequest);
        redisTemplate.convertAndSend(authPaymentRequestTopicName, authPaymentRequestJson);

        sleep(200);

        PaymentResponse expected = PaymentResponse.builder()
                .operationId(authPaymentRequest.getOperationId())
                .status(AccountBalanceStatus.BALANCE_NOT_VERIFIED)
                .paymentResponseStatus(PaymentResponseStatus.FAILED)
                .build();
        String actualJson = authPaymentListener.getReceivedMessage();
        PaymentResponse actual = objectMapper.readValue(actualJson, PaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        Optional<Payment> payment = paymentRepository.findById(authPaymentRequest.getOperationId());
        assertThat(payment).isEmpty();

        latch.await();
    }

    @Test
    void testClearingPayment_successful() throws JsonProcessingException {
        Balance sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        sb.setActualBalance(sb.getActualBalance().subtract(AMOUNT));
        sb.setAuthorizationBalance(sb.getAuthorizationBalance().add(AMOUNT));
        balanceRepository.save(sb);

        Balance tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        Payment authPayment = Payment.builder()
                .id(OPERATION_ID)
                .sourceBalance(sb)
                .targetBalance(tb)
                .amount(AMOUNT)
                .status(ACTIVE)
                .category(CATEGORY)
                .build();
        paymentRepository.save(authPayment);

        ClearingPaymentRequest request = new ClearingPaymentRequest(OPERATION_ID);
        String requestJson = objectMapper.writeValueAsString(request);
        redisTemplate.convertAndSend(clearingPaymentRequestTopicName, requestJson);

        sleep(200);

        ClearingPaymentResponse expected = ClearingPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(PaymentResponseStatus.SUCCESS)
                .build();
        String actualJson = clearingPaymentListener.getReceivedMessage();
        ClearingPaymentResponse actual = objectMapper.readValue(actualJson, ClearingPaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sb.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(sb.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE.subtract(AMOUNT))).isZero();

        tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(tb.getAuthorizationBalance().compareTo(TARGET_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(tb.getActualBalance().compareTo(TARGET_BALANCE_ACTUAL_BALANCE.add(AMOUNT))).isZero();

        authPayment = paymentRepository.findById(OPERATION_ID).orElseThrow();
        assertThat(authPayment.getStatus()).isEqualTo(PaymentStatus.CLOSED);
    }

    @Test
    void testClearingPayment_validationException_statusClosed() throws JsonProcessingException {
        Balance sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();

        Payment payment = Payment.builder()
                .id(OPERATION_ID)
                .sourceBalance(sb)
                .targetBalance(tb)
                .amount(AMOUNT)
                .status(PaymentStatus.CLOSED)
                .category(CATEGORY)
                .build();
        paymentRepository.save(payment);

        ClearingPaymentRequest request = new ClearingPaymentRequest(OPERATION_ID);
        String requestJson = objectMapper.writeValueAsString(request);
        redisTemplate.convertAndSend(clearingPaymentRequestTopicName, requestJson);

        sleep(200);

        ClearingPaymentResponse expected = ClearingPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(PaymentResponseStatus.FAILED)
                .build();
        String actualJson = clearingPaymentListener.getReceivedMessage();
        ClearingPaymentResponse actual = objectMapper.readValue(actualJson, ClearingPaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(sourceBalance.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE)).isZero();

        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthorizationBalance().compareTo(TARGET_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(targetBalance.getActualBalance().compareTo(TARGET_BALANCE_ACTUAL_BALANCE)).isZero();
        assertThat(paymentRepository.findById(OPERATION_ID).orElseThrow().getStatus()).isEqualTo(PaymentStatus.CLOSED);
    }

    @Test
    void testClearingPayment_validationException_statusRejected() throws JsonProcessingException {
        Balance sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();

        Payment payment = Payment.builder()
                .id(OPERATION_ID)
                .sourceBalance(sb)
                .targetBalance(tb)
                .amount(AMOUNT)
                .status(PaymentStatus.REJECTED)
                .category(CATEGORY)
                .build();
        paymentRepository.save(payment);

        ClearingPaymentRequest request = new ClearingPaymentRequest(OPERATION_ID);
        String requestJson = objectMapper.writeValueAsString(request);
        redisTemplate.convertAndSend(clearingPaymentRequestTopicName, requestJson);

        sleep(200);

        ClearingPaymentResponse expected = ClearingPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(PaymentResponseStatus.FAILED)
                .build();
        String actualJson = clearingPaymentListener.getReceivedMessage();
        ClearingPaymentResponse actual = objectMapper.readValue(actualJson, ClearingPaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(sourceBalance.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE)).isZero();

        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthorizationBalance().compareTo(TARGET_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(targetBalance.getActualBalance().compareTo(TARGET_BALANCE_ACTUAL_BALANCE)).isZero();
        assertThat(paymentRepository.findById(OPERATION_ID).orElseThrow().getStatus()).isEqualTo(PaymentStatus.REJECTED);
    }

    @Test
    void testCancelPayment_successful() throws JsonProcessingException {
        Balance sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        sb.setActualBalance(sb.getActualBalance().subtract(AMOUNT));
        sb.setAuthorizationBalance(sb.getAuthorizationBalance().add(AMOUNT));
        balanceRepository.save(sb);

        Balance tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        Payment payment = Payment.builder()
                .id(OPERATION_ID)
                .sourceBalance(sb)
                .targetBalance(tb)
                .amount(AMOUNT)
                .status(ACTIVE)
                .category(CATEGORY)
                .build();
        paymentRepository.save(payment);

        CancelPaymentRequest request = new CancelPaymentRequest(OPERATION_ID);
        String requestJson = objectMapper.writeValueAsString(request);
        redisTemplate.convertAndSend(cancelPaymentRequestTopicName, requestJson);

        sleep(200);

        CancelPaymentResponse expected = CancelPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(PaymentResponseStatus.SUCCESS)
                .build();
        String actualJson = cancelPaymentListener.getReceivedMessage();
        CancelPaymentResponse actual = objectMapper.readValue(actualJson, CancelPaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sb.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(sb.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE)).isZero();

        tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(tb.getAuthorizationBalance().compareTo(TARGET_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(tb.getActualBalance().compareTo(TARGET_BALANCE_ACTUAL_BALANCE)).isZero();

        payment = paymentRepository.findById(OPERATION_ID).orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REJECTED);
    }

    @Test
    void testCancelPayment_validationException_statusClosed() throws JsonProcessingException {
        Balance sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        Payment payment = Payment.builder()
                .id(OPERATION_ID)
                .sourceBalance(sb)
                .targetBalance(tb)
                .amount(AMOUNT)
                .status(PaymentStatus.CLOSED)
                .category(CATEGORY)
                .build();
        paymentRepository.save(payment);

        CancelPaymentRequest request = new CancelPaymentRequest(OPERATION_ID);
        String requestJson = objectMapper.writeValueAsString(request);
        redisTemplate.convertAndSend(cancelPaymentRequestTopicName, requestJson);

        sleep(200);

        CancelPaymentResponse expected = CancelPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(PaymentResponseStatus.FAILED)
                .build();
        String actualJson = cancelPaymentListener.getReceivedMessage();
        CancelPaymentResponse actual = objectMapper.readValue(actualJson, CancelPaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(sourceBalance.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE)).isZero();

        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthorizationBalance().compareTo(TARGET_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(targetBalance.getActualBalance().compareTo(TARGET_BALANCE_ACTUAL_BALANCE)).isZero();
        assertThat(paymentRepository.findById(OPERATION_ID).orElseThrow().getStatus()).isEqualTo(PaymentStatus.CLOSED);
    }

    @Test
    void testCancelPayment_validationException_statusRejected() throws JsonProcessingException {
        Balance sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        Payment payment = Payment.builder()
                .id(OPERATION_ID)
                .sourceBalance(sb)
                .targetBalance(tb)
                .amount(AMOUNT)
                .status(PaymentStatus.REJECTED)
                .category(CATEGORY)
                .build();
        paymentRepository.save(payment);

        CancelPaymentRequest cancelRequest = new CancelPaymentRequest(OPERATION_ID);
        String cancelRequestJson = objectMapper.writeValueAsString(cancelRequest);
        redisTemplate.convertAndSend(cancelPaymentRequestTopicName, cancelRequestJson);

        sleep(200);

        CancelPaymentResponse expected = CancelPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(PaymentResponseStatus.FAILED)
                .build();
        String actualJson = cancelPaymentListener.getReceivedMessage();
        CancelPaymentResponse actual = objectMapper.readValue(actualJson, CancelPaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(sourceBalance.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE)).isZero();

        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthorizationBalance().compareTo(TARGET_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(targetBalance.getActualBalance().compareTo(TARGET_BALANCE_ACTUAL_BALANCE)).isZero();
        assertThat(paymentRepository.findById(OPERATION_ID).orElseThrow().getStatus()).isEqualTo(PaymentStatus.REJECTED);
    }

    @Test
    void testErrorPayment_successful() throws JsonProcessingException {
        Balance sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        sb.setActualBalance(sb.getActualBalance().subtract(AMOUNT));
        sb.setAuthorizationBalance(sb.getAuthorizationBalance().add(AMOUNT));
        balanceRepository.save(sb);

        Balance tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        Payment payment = Payment.builder()
                .id(OPERATION_ID)
                .sourceBalance(sb)
                .targetBalance(tb)
                .amount(AMOUNT)
                .status(ACTIVE)
                .category(CATEGORY)
                .build();
        paymentRepository.save(payment);

        ErrorPaymentRequest request = new ErrorPaymentRequest(OPERATION_ID);
        String requestJson = objectMapper.writeValueAsString(request);
        redisTemplate.convertAndSend(errorPaymentRequestTopicName, requestJson);

        sleep(200);

        ErrorPaymentResponse expected = ErrorPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(PaymentResponseStatus.SUCCESS)
                .build();
        String actualJson = errorPaymentListener.getReceivedMessage();
        ErrorPaymentResponse actual = objectMapper.readValue(actualJson, ErrorPaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sb.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(sb.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE)).isZero();

        tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(tb.getAuthorizationBalance().compareTo(TARGET_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(tb.getActualBalance().compareTo(TARGET_BALANCE_ACTUAL_BALANCE)).isZero();

        payment = paymentRepository.findById(OPERATION_ID).orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REJECTED);
    }

    @Test
    void testErrorPayment_validationException_statusClosed() throws JsonProcessingException {
        Balance sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();

        Payment payment = Payment.builder()
                .id(OPERATION_ID)
                .sourceBalance(sb)
                .targetBalance(tb)
                .amount(AMOUNT)
                .status(PaymentStatus.CLOSED)
                .category(CATEGORY)
                .build();
        paymentRepository.save(payment);

        ErrorPaymentRequest request = new ErrorPaymentRequest(OPERATION_ID);
        String requestJson = objectMapper.writeValueAsString(request);
        redisTemplate.convertAndSend(errorPaymentRequestTopicName, requestJson);

        sleep(200);

        ErrorPaymentResponse expected = ErrorPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(PaymentResponseStatus.FAILED)
                .build();
        String actualJson = errorPaymentListener.getReceivedMessage();
        ErrorPaymentResponse actual = objectMapper.readValue(actualJson, ErrorPaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(sourceBalance.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE)).isZero();

        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthorizationBalance().compareTo(TARGET_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(targetBalance.getActualBalance().compareTo(TARGET_BALANCE_ACTUAL_BALANCE)).isZero();
        assertThat(paymentRepository.findById(OPERATION_ID).orElseThrow().getStatus()).isEqualTo(PaymentStatus.CLOSED);
    }

    @Test
    void testErrorPayment_validationException_statusRejected() throws JsonProcessingException {
        Balance sb = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        Balance tb = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();

        Payment payment = Payment.builder()
                .id(OPERATION_ID)
                .sourceBalance(sb)
                .targetBalance(tb)
                .amount(AMOUNT)
                .status(PaymentStatus.REJECTED)
                .category(CATEGORY)
                .build();
        paymentRepository.save(payment);

        ErrorPaymentRequest errorRequest = new ErrorPaymentRequest(OPERATION_ID);
        String errorRequestJson = objectMapper.writeValueAsString(errorRequest);
        redisTemplate.convertAndSend(errorPaymentRequestTopicName, errorRequestJson);

        sleep(200);

        ErrorPaymentResponse expected = ErrorPaymentResponse.builder()
                .operationId(OPERATION_ID)
                .paymentResponseStatus(PaymentResponseStatus.FAILED)
                .build();
        String actualJson = errorPaymentListener.getReceivedMessage();
        ErrorPaymentResponse actual = objectMapper.readValue(actualJson, ErrorPaymentResponse.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

        Balance sourceBalance = balanceRepository.findById(SOURCE_BALANCE_ID).orElseThrow();
        assertThat(sourceBalance.getAuthorizationBalance().compareTo(SOURCE_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(sourceBalance.getActualBalance().compareTo(SOURCE_BALANCE_ACTUAL_BALANCE)).isZero();

        Balance targetBalance = balanceRepository.findById(TARGET_BALANCE_ID).orElseThrow();
        assertThat(targetBalance.getAuthorizationBalance().compareTo(TARGET_BALANCE_AUTH_BALANCE)).isZero();
        assertThat(targetBalance.getActualBalance().compareTo(TARGET_BALANCE_ACTUAL_BALANCE)).isZero();
        assertThat(paymentRepository.findById(OPERATION_ID).orElseThrow().getStatus()).isEqualTo(PaymentStatus.REJECTED);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
