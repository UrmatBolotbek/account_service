package faang.school.accountservice.config.account_generation;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "scheduler.account.generation")
@Configuration
@EnableScheduling
@Profile("!test")
public class AccountGenerationConfig {

    private Map<String, Integer> requiredBatch;

    public int getRequiredBatchSize(AccountType type, Currency currency) {
        String key = type.name() + "_" + currency.name();
        return requiredBatch.getOrDefault(key, 0);
    }
}