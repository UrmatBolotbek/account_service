package faang.school.accountservice.service.free_account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.Currency;
import faang.school.accountservice.model.account_number.AccountNumberSequence;
import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper mapper;


    @Transactional
    public AccountDto getNewAccount(AccountType type) {
       FreeAccountNumber freeAccountNumber = freeAccountNumbersRepository.findByType(type);

        Account account = new Account();
        account.setAccountType(type);
        account.setNumber(String.valueOf(freeAccountNumber.getAccountNumber()));
        accountRepository.save(account);

        freeAccountNumbersRepository.delete(freeAccountNumber);

        return mapper.toDto(account);
    }

    public void generateAccountNumber(AccountType type, Currency currency) {
        accountNumbersSequenceRepository.isIncremented(type);

        String accountNumberFirstPart = String.valueOf(type.getAccountTypeTypeNumber() + currency.getCurrencyNumber());
        AccountNumberSequence accountNumberSequence = accountNumbersSequenceRepository.findByType(type);
        int accountNumberFinal = Integer.parseInt(accountNumberFirstPart) * (int) Math.pow(10, 12) + accountNumberSequence.getCurrent();

        FreeAccountNumber freeAccountNumber = new FreeAccountNumber();
        freeAccountNumber.setAccountNumber(accountNumberFinal);
        freeAccountNumber.setType(type);

        freeAccountNumbersRepository.save(freeAccountNumber);
    }
}
