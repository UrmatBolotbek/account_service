package faang.school.accountservice.repository;

import faang.school.accountservice.model.savings_account.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    @Query("SELECT sa FROM SavingsAccount sa JOIN FETCH sa.account a  WHERE a.ownerId = :ownerId")
    List<SavingsAccount> findByOwnerId(@Param("ownerId") Long ownerId);

    List<SavingsAccount> findByLastInterestDateIsNullOrLastInterestDateLessThan(OffsetDateTime currentDate);
}