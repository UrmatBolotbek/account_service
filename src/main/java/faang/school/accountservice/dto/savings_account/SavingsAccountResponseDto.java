package faang.school.accountservice.dto.savings_account;

import faang.school.accountservice.model.tariff.TariffType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsAccountResponseDto {
    private Long accountId;
    private TariffType currentTariffId;
    private OffsetDateTime lastInterestDate;
}