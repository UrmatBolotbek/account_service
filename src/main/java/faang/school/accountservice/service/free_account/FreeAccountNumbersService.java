package faang.school.accountservice.service.free_account;

import faang.school.accountservice.cache.FreeAccountNumberCache;
import faang.school.accountservice.dto.account.RequestAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.mapper.account.MyAccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.account.AccountStatus;
import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.Currency;
import faang.school.accountservice.model.account_number.AccountNumberSequence;
import faang.school.accountservice.model.account_number.AccountSequenceId;
import faang.school.accountservice.model.account_number.FreeAccountId;
import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private final FreeAccountNumberCache freeAccountNumberCache;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final AccountRepository accountRepository;
    private final MyAccountMapper mapper;

    @Transactional
    public ResponseAccountDto getNewAccount(RequestAccountDto requestAccountDto) {
        FreeAccountNumber freeAccountNumber = freeAccountNumberCache.getFreeAccount(requestAccountDto.getAccountType(), requestAccountDto.getCurrency());
        freeAccountNumbersRepository.delete(freeAccountNumber);

        Account account = Account.builder()
                .number(String.valueOf(freeAccountNumber.getId().getAccountNumber()))
                .currency(requestAccountDto.getCurrency())
                .accountType(requestAccountDto.getAccountType())
                .ownerType(requestAccountDto.getOwnerType())
                .ownerId(requestAccountDto.getOwnerId())
                .status(AccountStatus.OPEN)
                .build();

        accountRepository.save(account);

        return mapper.toDto(account);
    }

    @Transactional
    public void generateAccountNumbers(int batchSize) {
        for (AccountType type : AccountType.values()) {
            for (Currency currency : Currency.values()) {
                BigInteger initialAccountNumber = new BigInteger(String.valueOf(generateInitialAccountNumber(type, currency)));
                AccountSequenceId id = new AccountSequenceId();
                id.setType(type);
                id.setCurrency(currency);
                if (!accountNumbersSequenceRepository.existsById(id)) {
                    AccountNumberSequence accountNumberSequence = new AccountNumberSequence();
                    accountNumberSequence.setId(new AccountSequenceId(type, currency));
                    accountNumbersSequenceRepository.save(accountNumberSequence);
                }
                AccountNumberSequence period = accountNumbersSequenceRepository.incrementCounter(type.name(), currency.name(), batchSize);
                List<FreeAccountNumber> numbers = getFreeAccounts(period,batchSize, initialAccountNumber, type, currency);

                freeAccountNumbersRepository.saveAll(numbers);
                freeAccountNumberCache.updateRedisCache(numbers);
            }
        }
    }

    private List<FreeAccountNumber> getFreeAccounts(AccountNumberSequence period, int batchSize, BigInteger initialAccountNumber, AccountType type, Currency currency) {
        List<FreeAccountNumber> freeAccounts = new ArrayList<>();

        for (long i = period.getCounter() - batchSize; i < period.getCounter(); i++) {
            BigInteger finalAccountNumber = initialAccountNumber.add(BigInteger.valueOf(i));
            freeAccounts.add(new FreeAccountNumber(new FreeAccountId(
                    String.valueOf(finalAccountNumber),
                    type,
                    currency
            )));
        }
        return freeAccounts;
    }

    private BigInteger generateInitialAccountNumber(AccountType type, Currency currency) {
        String initialNumber = type.getAccountTypeNumber() + String.valueOf(currency.getCurrencyNumber());
        return new BigInteger(initialNumber).multiply(BigInteger.valueOf((long) Math.pow(10, 12)));
    }
}
