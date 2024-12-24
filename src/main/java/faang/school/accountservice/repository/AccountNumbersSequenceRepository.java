package faang.school.accountservice.repository;

import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account_number.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {

    @Query(nativeQuery = true,
            value = """
                    update account_number_sequence
                    set current = current + 1
                    where type = :type  and current = 2
                    returning current
                    """)
    boolean isIncremented(AccountType type);

    AccountNumberSequence findByType(AccountType type);
}
