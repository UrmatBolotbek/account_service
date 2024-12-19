package faang.school.accountservice.dto.tariff;

import faang.school.accountservice.model.tariff.TariffType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffResponseDto {
    private Long id;
    private TariffType tariffType;
    private Double currentInterestRate;
}