package faang.school.accountservice.calculator.savings_acccount;

import faang.school.accountservice.model.savings_account.SavingsAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Component
public class InterestCalculator {
    private static final BigDecimal ONE_HUNDRED_PERCENT = BigDecimal.valueOf(100);

    public void calculate(List<SavingsAccount> savingsAccounts) {
        log.info("start calculate, thread name: {}", Thread.currentThread().getName());
        savingsAccounts.forEach(savingsAccount -> {
            log.info("Thread name: {} take Savings Account id: {}", Thread.currentThread().getName(), savingsAccount.getId());
            BigDecimal updatedBalance = savingsAccount.getBalance()
                    .divide(ONE_HUNDRED_PERCENT, RoundingMode.HALF_UP)
                    .multiply(savingsAccount.getTariff().getInterestRate().getInterestRate())
                    .setScale(2, RoundingMode.HALF_UP);

            log.info("Thread name: {} save Savings Account id: {}", Thread.currentThread().getName(), savingsAccount.getId());
            savingsAccount.setBalance(updatedBalance);
        });
    }
}