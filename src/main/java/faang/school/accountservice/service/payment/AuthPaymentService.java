package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.payment.request.AuthPaymentRequest;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.balance.BalanceHasBeenUpdatedException;
import faang.school.accountservice.exception.payment.AuthPaymentHasBeenUpdatedException;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.payment.AuthPayment;
import faang.school.accountservice.repository.AuthPaymentRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validator.payment.AuthPaymentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.accountservice.model.payment.AuthPaymentStatus.CLOSED;
import static faang.school.accountservice.model.payment.AuthPaymentStatus.REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthPaymentService {

    private final AuthPaymentRepository authPaymentRepository;
    private final BalanceRepository balanceRepository;
    private final AuthPaymentValidator authPaymentValidator;

    @Transactional
    public AuthPayment authorizePayment(AuthPaymentRequest request) {
        log.info("Authorize payment, operationId={}", request.getOperationId());
        Balance source = findBalance(request.getSourceAccountId());
        Balance target = findBalance(request.getTargetAccountId());
        authPaymentValidator.checkFreeAmount(request.getOperationId(), source, request.getAmount());
        adjustSourceBalanceForAuthorization(source, request.getAmount());
        AuthPayment payment = AuthPayment.builder()
                .amount(request.getAmount())
                .category(request.getCategory())
                .sourceBalance(source)
                .targetBalance(target)
                .build();
        saveBalance(source);
        AuthPayment savedPayment = saveAuthPayment(payment);
        log.info("Payment authorized, paymentId={}", savedPayment.getId());
        return savedPayment;
    }

    @Transactional
    public AuthPayment clearingPayment(ClearingPaymentRequest request) {
        log.info("Clearing payment, operationId={}", request.getOperationId());
        AuthPayment payment = findAuthPaymentById(request.getOperationId());
        authPaymentValidator.checkAuthPaymentStatus(payment, "accepted");
        Balance source = payment.getSourceBalance();
        Balance target = payment.getTargetBalance();
        clearSourceBalance(source, payment.getAmount());
        increaseTargetBalance(target, payment.getAmount());
        payment.setStatus(CLOSED);
        saveBalance(source);
        saveBalance(target);
        AuthPayment savedPayment = saveAuthPayment(payment);
        log.info("Payment cleared, paymentId={}", savedPayment.getId());
        return savedPayment;
    }
//TODO добавить аудит после трансакций
    @Transactional
    public AuthPayment cancelPayment(CancelPaymentRequest request) {
        log.info("Cancel payment, operationId={}", request.getOperationId());
        AuthPayment payment = rejectPayment(request.getOperationId());
        log.info("Payment cancelled, paymentId={}", payment.getId());
        return payment;
    }

    @Transactional
    public AuthPayment errorPayment(ErrorPaymentRequest request) {
        log.info("Error payment, operationId={}", request.getOperationId());
        AuthPayment payment = rejectPayment(request.getOperationId());
        log.info("Payment errored, paymentId={}", payment.getId());
        return payment;
    }

    private AuthPayment rejectPayment(UUID paymentId) {
        AuthPayment payment = findAuthPaymentById(paymentId);
        authPaymentValidator.checkAuthPaymentStatus(payment, "rejected");
        Balance source = payment.getSourceBalance();
        revertSourceBalance(source, payment.getAmount());
        payment.setStatus(REJECTED);
        saveBalance(source);
        return saveAuthPayment(payment);
    }

    private AuthPayment findAuthPaymentById(UUID id) {
        return authPaymentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("AuthPayment with ID %s not found".formatted(id))
        );
    }

    private Balance findBalance(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.warn("Balance not found for accountId={}", accountId);
                    return new AccountNotFoundException("Account with id=%d not found".formatted(accountId));
                });
    }

    private void adjustSourceBalanceForAuthorization(Balance balance, BigDecimal amount) {
        balance.setAuthorizationBalance(balance.getAuthorizationBalance().add(amount));
        balance.setActualBalance(balance.getActualBalance().subtract(amount));
    }

    private void clearSourceBalance(Balance balance, BigDecimal amount) {
        balance.setAuthorizationBalance(balance.getAuthorizationBalance().subtract(amount));
    }

    private void increaseTargetBalance(Balance balance, BigDecimal amount) {
        balance.setActualBalance(balance.getActualBalance().add(amount));
    }

    private void revertSourceBalance(Balance balance, BigDecimal amount) {
        balance.setAuthorizationBalance(balance.getAuthorizationBalance().subtract(amount));
        balance.setActualBalance(balance.getActualBalance().add(amount));
    }

    private void saveBalance(Balance balance) {
        try {
            balanceRepository.saveAndFlush(balance);
        } catch (OptimisticLockingFailureException ex) {
            throw new BalanceHasBeenUpdatedException("Balance with id=%s has been updated. Reload information."
                    .formatted(balance.getId()));
        }
    }

    private AuthPayment saveAuthPayment(AuthPayment payment) {
        try {
            return authPaymentRepository.saveAndFlush(payment);
        } catch (OptimisticLockingFailureException ex) {
            throw new AuthPaymentHasBeenUpdatedException("AuthPayment with id=%s has been updated. Reload information."
                    .formatted(payment.getId()));
        }
    }
}
