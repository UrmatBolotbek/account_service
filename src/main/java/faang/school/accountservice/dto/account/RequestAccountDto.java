package faang.school.accountservice.dto.account;

import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.Currency;
import faang.school.accountservice.model.account.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAccountDto {
    private long ownerId;
    private OwnerType ownerType;
    private AccountType accountType;
    private Currency currency;
}
