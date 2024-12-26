package faang.school.accountservice.service.free_account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.Currency;
import faang.school.accountservice.model.account_number.AccountNumberSequence;
import faang.school.accountservice.model.account_number.FreeAccountId;
import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper mapper;

    public AccountDto getNewAccount(AccountType type, Currency currency) {
        FreeAccountNumber freeAccountNumber = freeAccountNumbersRepository.retrieveFirst(type.name(), currency.name())
                .orElseThrow(() -> new IllegalArgumentException("No more free accounts in table 'free_account_numbers'"));

        Account account = Account.builder()
                .number(freeAccountNumber.getId().getAccountNumber())
                .currency(currency)
                .accountType(type)
                .build();
        accountRepository.save(account);

        return mapper.toDto(account);
    }

    @Transactional
    public void generateAccountNumbers(int batchSize) {
        for (AccountType type : AccountType.values()) {
            for (Currency currency : Currency.values()) {
                AccountNumberSequence period = accountNumbersSequenceRepository.incrementCounter(type.name(), currency.name(), batchSize);
                List<FreeAccountNumber> freeAccounts = new ArrayList<>();
                for (long i = period.getInitialValue(); i < period.getId().getCurrent(); i++) {
                    freeAccounts.add(new FreeAccountNumber(new FreeAccountId(
                            generateInitialAccountNumber(type, currency) + i,
                            type,
                            currency
                    )));
                }
                freeAccountNumbersRepository.saveAll(freeAccounts);
            }
        }
    }

    private int generateInitialAccountNumber(AccountType type, Currency currency) {
        return Integer.parseInt(String.valueOf(type.getAccountTypeNumber() + currency.getCurrencyNumber())) * (int) Math.pow(10, 12);
    }
}
