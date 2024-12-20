package faang.school.accountservice.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBalanceDto {
    private long id;
    private BigDecimal authorizationBalance;
    private BigDecimal actualBalance;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
