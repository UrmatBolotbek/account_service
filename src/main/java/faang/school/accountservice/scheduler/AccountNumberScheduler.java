package faang.school.accountservice.scheduler;

import faang.school.accountservice.enums.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {

    @Value("${account.number.batch-size}")
    private int batchSize;

    private final FreeAccountNumberService freeAccountNumberService;

    @Scheduled(cron = "${account.number.cron-debit}")
    public void generateAccountNumberDebitType() {
        freeAccountNumberService.generateAccountNumbers(AccountType.DEBIT, batchSize);
    }

    @Scheduled(cron = "${account.number.cron-credit}")
    public void generateAccountNumberCreditType() {
        freeAccountNumberService.generateAccountNumbers(AccountType.CREDIT, batchSize);
    }
}