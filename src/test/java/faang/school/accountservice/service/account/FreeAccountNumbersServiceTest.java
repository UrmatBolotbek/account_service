package faang.school.accountservice.service.account;

import faang.school.accountservice.cache.FreeAccountNumberCache;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.account_number.AccountNumberSequence;
import faang.school.accountservice.model.account_number.AccountSequenceId;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FreeAccountNumbersServiceTest {

    @Mock
    private AccountNumbersSequenceRepository sequenceRepository;

    @Mock
    private FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Mock
    private FreeAccountNumberCache freeAccountNumberCache;

    @InjectMocks
    private FreeAccountNumbersService service;

    @Test
    void testGenerateAccountNumbers() {
        AccountType type = AccountType.FL;
        Currency currency = Currency.RUB;
        int batchSize = 10;
        BigInteger initialAccountNumber = new BigInteger("4081781000000000000000");
        AccountNumberSequence sequence = new AccountNumberSequence(new AccountSequenceId(type, currency), 1010L);

        when(sequenceRepository.incrementCounter(type.name(), currency.name(), batchSize)).thenReturn(sequence);

        service.generateAccountNumbers(type, currency, batchSize);

        verify(sequenceRepository).incrementCounter(type.name(), currency.name(), batchSize);
        verify(freeAccountNumbersRepository).saveAll(anyList());
        verify(freeAccountNumberCache).updateRedisCache(anyList());
    }

    @Test
    void testEnsureAccountNumbers_ShouldGenerateNumbers() {
        AccountType type = AccountType.FL;
        Currency currency = Currency.RUB;
        int requiredCount = 100;
        long existingCount = 90;
        int batchSize = requiredCount - (int) existingCount;
        AccountNumberSequence sequence = new AccountNumberSequence(new AccountSequenceId(type, currency), 1010L);

        when(freeAccountNumbersRepository.countByTypeAndCurrency(type.name(), currency.name())).thenReturn(existingCount);
        when(sequenceRepository.incrementCounter(type.name(), currency.name(), batchSize)).thenReturn(sequence);

        service.ensureAccountNumbers(type, currency, requiredCount);

        verify(freeAccountNumbersRepository).countByTypeAndCurrency(type.name(), currency.name());
        verify(sequenceRepository).incrementCounter(type.name(), currency.name(), batchSize);
        verify(freeAccountNumbersRepository).saveAll(anyList());
        verify(freeAccountNumberCache).updateRedisCache(anyList());
    }

    @Test
    void testEnsureAccountNumbers_ShouldDoNothing() {
        AccountType type = AccountType.FL;
        Currency currency = Currency.RUB;
        int requiredCount = 100;
        long existingCount = 100;

        when(freeAccountNumbersRepository.countByTypeAndCurrency(type.name(), currency.name())).thenReturn(existingCount);

        service.ensureAccountNumbers(type, currency, requiredCount);

        verify(freeAccountNumbersRepository).countByTypeAndCurrency(type.name(), currency.name());
        verifyNoMoreInteractions(sequenceRepository, freeAccountNumbersRepository, freeAccountNumberCache);
    }
}