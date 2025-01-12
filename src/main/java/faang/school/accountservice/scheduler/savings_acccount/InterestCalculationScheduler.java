package faang.school.accountservice.scheduler.savings_acccount;


import faang.school.accountservice.service.savings_account.SavingsAccountService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class InterestCalculationScheduler {

    private final SavingsAccountService savingsAccountService;

    @Retryable(
            retryFor = {IOException.class, OptimisticLockException.class},
            maxAttemptsExpression = "#{${retry.interestCalculator.maxAttempts}}"
    )
    @Scheduled(cron = "${scheduler.calculatePercents.cron}")
    public void calculatePercentsForSavingsAccount() {
        savingsAccountService.calculatePercents();
    }
}