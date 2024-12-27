package faang.school.accountservice.dto.payment.response;

import faang.school.accountservice.model.payment.AccountBalanceStatus;
import faang.school.accountservice.model.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthPaymentResponse {
    private UUID operationId;
    private AccountBalanceStatus status;
    private PaymentStatus paymentStatus;
}