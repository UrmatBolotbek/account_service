package faang.school.accountservice.service.account;

import faang.school.accountservice.cache.FreeAccountNumberCache;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.account_number.AccountNumberSequence;
import faang.school.accountservice.model.account_number.FreeAccountId;
import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeAccountNumbersService {

    private final AccountNumbersSequenceRepository sequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;
    private final FreeAccountNumberCache freeAccountNumberCache;

    @Transactional
    public void generateAccountNumbers(AccountType type, Currency currency, int batchSize) {
        BigInteger initialAccountNumber = generateInitialAccountNumber(type, currency);
        AccountNumberSequence sequence = sequenceRepository.incrementCounter(type.name(), currency.name(), batchSize);
        long startCounter = sequence.getCounter() - batchSize + 1;

        List<FreeAccountNumber> newNumbers = IntStream.range(0, batchSize)
                .mapToObj(i -> createAccountNumber(type, currency, initialAccountNumber, startCounter + i))
                .toList();

        freeAccountNumbersRepository.saveAll(newNumbers);
        log.info("Generated {} new account numbers for type {} and currency {}", batchSize, type, currency);

        freeAccountNumberCache.updateRedisCache(newNumbers);
    }

    @Transactional
    public void ensureAccountNumbers(AccountType type, Currency currency, int requiredCount) {
        long existingCount = freeAccountNumbersRepository.countByTypeAndCurrency(type.name(), currency.name());
        if (existingCount < requiredCount) {
            generateAccountNumbers(type, currency, (int) (requiredCount - existingCount));
        }
    }

    private FreeAccountNumber createAccountNumber(AccountType type, Currency currency,
                                                  BigInteger initialAccountNumber, long counter) {
        BigInteger accountNumber = initialAccountNumber.add(BigInteger.valueOf(counter));
        FreeAccountId id = new FreeAccountId(accountNumber.toString(), type, currency);
        return FreeAccountNumber.builder().id(id).build();
    }

    private BigInteger generateInitialAccountNumber(AccountType type, Currency currency) {
        String initialNumber = type.getAccountTypeNumber() + String.valueOf(currency.getCurrencyNumber());
        return new BigInteger(initialNumber).multiply(BigInteger.valueOf((long) Math.pow(10, 12)));
    }
}