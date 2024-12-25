package faang.school.accountservice.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBalanceDto {
    private Long accountId;
    private BigDecimal authorizedAmount;
    private BigDecimal actualAmount;
    private OffsetDateTime updatedAt;
}
