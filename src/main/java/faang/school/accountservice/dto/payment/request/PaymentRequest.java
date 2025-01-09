package faang.school.accountservice.dto.payment.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.payment.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    @NotNull(message = "The operation ID should not be null")
    private UUID operationId;

    @NotNull(message = "The source account ID should not be null")
    private Long sourceAccountId;

    @NotNull(message = "The target account ID should not be null")
    private Long targetAccountId;

    @NotNull(message = "The amount should not be null")
    @DecimalMin(value = "0.01", message = "The amount should be at least 0.01")
    private BigDecimal amount;

    @NotNull(message = "The currency should not be null")
    private Currency currency;

    @NotNull(message = "The category should not be null")
    private Category category;
}
