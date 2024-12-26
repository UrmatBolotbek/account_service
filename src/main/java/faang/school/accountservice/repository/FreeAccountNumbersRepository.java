package faang.school.accountservice.repository;

import faang.school.accountservice.model.account_number.FreeAccountId;
import faang.school.accountservice.model.account_number.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Query(nativeQuery = true,
            value = """
                    DELETE FROM free_account_numbers
                    WHERE type = :type AND currency = :currency
                    RETURNING type, account_number, currency
                    LIMIT 1
                       """)
    @Modifying
    Optional<FreeAccountNumber> retrieveFirst(String type, String currency);
}
