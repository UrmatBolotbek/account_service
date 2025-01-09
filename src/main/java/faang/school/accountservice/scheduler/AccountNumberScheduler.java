package faang.school.accountservice.scheduler;

import faang.school.accountservice.config.account_generation.AccountGenerationConfig;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.service.account.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountNumberScheduler {

    private final FreeAccountNumbersService freeAccountNumbersService;
    private final AccountGenerationConfig config;

    @Scheduled(cron = "${account.generation.cron}")
    public void generateAccountNumbers() {
        for (AccountType type : AccountType.values()) {
            for (Currency currency : Currency.values()) {
                try {
                    int batchSize = config.getRequiredBatchSize(type, currency);
                    freeAccountNumbersService.generateAccountNumbers(type, currency, batchSize);
                    log.info("Generated {} account numbers for type {} and currency {}",
                            batchSize, type, currency);
                } catch (Exception e) {
                    log.error("Failed to generate account numbers for type {} and currency {}: {}",
                            type, currency, e.getMessage(), e);
                }
            }
        }
    }
}