package faang.school.accountservice.scheduler.savings_acccount;

import faang.school.accountservice.service.savings_account.SavingsAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterestCalculationSchedulerTest {

    @InjectMocks
    private InterestCalculationScheduler scheduler;

    @Mock
    private SavingsAccountService savingsAccountService;

    @Test
    void calculatePercentsForSavingsAccount_ShouldInvokeSavingsAccountService() {
        scheduler.calculatePercentsForSavingsAccount();
        Mockito.verify(savingsAccountService).calculatePercents();
    }
}