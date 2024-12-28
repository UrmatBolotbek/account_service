package faang.school.accountservice.validator.payment;

import faang.school.accountservice.exception.payment.InsufficientFundsException;
import faang.school.accountservice.exception.payment.InvalidPaymentStatusException;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.accountservice.model.payment.PaymentStatus.CLOSED;
import static faang.school.accountservice.model.payment.PaymentStatus.ACTIVE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class PaymentValidatorTest {
    private static final Long BALANCE_ID = 1L;
    private static final UUID OPERATION_ID = UUID.randomUUID();
    private static final UUID PAYMENT_ID = UUID.randomUUID();
    private static final BigDecimal ACTUAL_BALANCE = BigDecimal.valueOf(1.0);
    private static final BigDecimal AUTH_BALANCE = BigDecimal.valueOf(1.0);
    private static final BigDecimal FIRST_AMOUNT = BigDecimal.valueOf(2.0);
    private static final BigDecimal SECOND_AMOUNT = BigDecimal.valueOf(2.0);
    private final static String OPERATION_NAME = "accept";

    @InjectMocks
    private PaymentValidator paymentValidator;

    private Balance sourceBalance;
    private Payment closedPayment;
    private Payment activePayment;

    @BeforeEach
    public void setUp() {
        sourceBalance = Balance.builder()
                .id(BALANCE_ID)
                .actualBalance(ACTUAL_BALANCE)
                .authorizationBalance(AUTH_BALANCE)
                .build();

        closedPayment = Payment.builder()
                .id(PAYMENT_ID)
                .amount(SECOND_AMOUNT)
                .status(CLOSED)
                .build();

        activePayment = Payment.builder()
                .id(PAYMENT_ID)
                .amount(SECOND_AMOUNT)
                .status(ACTIVE)
                .build();
    }

    @Test
    void testCheckFreeAmount_exception() {
        assertThatThrownBy(() -> paymentValidator.checkFreeAmount(OPERATION_ID, sourceBalance, FIRST_AMOUNT))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Insufficient funds, operationId=%s, balanceId=%s",
                        OPERATION_ID, BALANCE_ID);
    }

    @Test
    void testCheckFreeAmount_successful() {
        sourceBalance.setActualBalance(BigDecimal.valueOf(3));

        assertDoesNotThrow(() -> paymentValidator.checkFreeAmount(OPERATION_ID, sourceBalance, FIRST_AMOUNT));
    }

    @Test
    void testCheckAuthPaymentForAccept_exception() {
        assertThatThrownBy(() -> paymentValidator.checkAuthPaymentStatus(closedPayment, OPERATION_NAME))
                .isInstanceOf(InvalidPaymentStatusException.class)
                .hasMessageContaining("Payment with id=%s cannot be %s, current status=%s"
                        .formatted(closedPayment.getId(), OPERATION_NAME, closedPayment.getStatus()));
    }

    @Test
    void testCheckAuthPaymentForAccept_successful() {
        assertDoesNotThrow(() -> paymentValidator.checkAuthPaymentStatus(activePayment, OPERATION_NAME));
    }
}