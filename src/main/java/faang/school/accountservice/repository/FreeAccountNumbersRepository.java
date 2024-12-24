package faang.school.accountservice.repository;

import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account_number.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {

    FreeAccountNumber findByType(AccountType type);
}
