package faang.school.accountservice.repository;

import faang.school.accountservice.model.payment.AuthPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthPaymentRepository extends JpaRepository<AuthPayment, UUID> {
}
