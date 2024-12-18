package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestAccountDto {

    @Max(20)
    @Min(16)
    @NotNull(message = "Number cannot be null")
    private String number;
    @NotNull(message = "OwnerType cannot be null")
    private OwnerType ownerType;
    @NotNull(message = "AccountType cannot be null")
    private AccountType accountType;
    @NotNull(message = "Currency cannot be null")
    private Currency.Currency currency;

}
