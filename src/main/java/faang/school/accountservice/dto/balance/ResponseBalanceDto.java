package faang.school.accountservice.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBalanceDto {
    private long id;
    //TODO нужно ли это поле?
    private BigDecimal authorizationBalance;
    private BigDecimal actualBalance;
    //TODO нужно ли это поле?
    private LocalDateTime createdAt;
    //TODO нужно ли это поле?
    private LocalDateTime updatedAt;
}
