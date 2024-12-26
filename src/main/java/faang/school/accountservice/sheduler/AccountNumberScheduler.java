package faang.school.accountservice.sheduler;

import faang.school.accountservice.service.free_account.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountNumberScheduler {
    private final FreeAccountNumbersService service;

    @Value("${scheduler.free_account.batchSize}")
    private int batchSize;

    @Scheduled(cron = "${scheduler.free_account.cron}")
    public void generateNewNumbers() {
        service.generateAccountNumbers(batchSize);
    }
}
