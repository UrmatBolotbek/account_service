package faang.school.accountservice.service.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {

    private BigInteger generateInitialAccountNumber(AccountType type, Currency currency) {
        String initialNumber = type.getAccountTypeNumber() + String.valueOf(currency.getCurrencyNumber());
        return new BigInteger(initialNumber).multiply(BigInteger.valueOf((long) Math.pow(10, 12)));
    }
}
