package faang.school.accountservice.validator.savings_account;

import faang.school.accountservice.exception.savings_account.SavingsAccountNotFoundException;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import faang.school.accountservice.repository.SavingsAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SavingsAccountValidatorTest {

    @InjectMocks
    private SavingsAccountValidator savingsAccountValidator;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Test
    void validateSavingsAccountExists_ShouldThrowException_WhenAccountNotFound() {
        Long accountId = 1L;
        Mockito.when(savingsAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        SavingsAccountNotFoundException exception = assertThrows(
                SavingsAccountNotFoundException.class,
                () -> savingsAccountValidator.validateSavingsAccountExists(accountId)
        );

        assertEquals("Savings account with id: " + accountId + " does not exist", exception.getMessage());
        Mockito.verify(savingsAccountRepository).findById(accountId);
    }

    @Test
    void validateSavingsAccountExists_ShouldReturnSavingsAccount_WhenAccountExists() {
        Long accountId = 1L;
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(accountId);

        Mockito.when(savingsAccountRepository.findById(accountId)).thenReturn(Optional.of(savingsAccount));

        SavingsAccount result = savingsAccountValidator.validateSavingsAccountExists(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        Mockito.verify(savingsAccountRepository).findById(accountId);
    }
}