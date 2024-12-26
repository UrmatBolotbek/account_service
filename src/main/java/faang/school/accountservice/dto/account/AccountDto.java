package faang.school.accountservice.dto.account;

import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.Currency;
import faang.school.accountservice.model.account.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private long number;
    private OwnerType ownerType;
    private AccountType accountType;
    private Currency currency;
    private ResponseBalanceDto balance;
}
