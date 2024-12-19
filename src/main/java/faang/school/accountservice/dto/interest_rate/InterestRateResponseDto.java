package faang.school.accountservice.dto.interest_rate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterestRateResponseDto {
    private Long id;
    private Double interestRate;
}