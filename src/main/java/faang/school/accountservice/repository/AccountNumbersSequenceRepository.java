package faang.school.accountservice.repository;

import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.Currency;
import faang.school.accountservice.model.account_number.AccountNumberSequence;
import faang.school.accountservice.model.account_number.AccountSequenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, AccountSequenceId> {

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence SET counter = counter + :batchSize
            WHERE type = :type AND currency = :currency
            RETURNING type, currency, counter, old.counter AS initialValue
            """)
    @Modifying
    AccountNumberSequence incrementCounter(String type, String currency, int batchSize);
}
