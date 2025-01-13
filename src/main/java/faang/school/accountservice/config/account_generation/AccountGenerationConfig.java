package faang.school.accountservice.config.account_generation;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "scheduler.account.generation")
@Data
public class AccountGenerationConfig {

    private Map<String, Integer> requiredBatch;

    public int getRequiredBatchSize(AccountType type, Currency currency) {
        String key = type.name() + "_" + currency.name();
        return requiredBatch.getOrDefault(key, 0);
    }
}