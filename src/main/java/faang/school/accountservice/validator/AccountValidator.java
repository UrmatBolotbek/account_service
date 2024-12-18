package faang.school.accountservice.validator;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountValidator {

    private final AccountRepository accountRepository;

    public Account validateAccount(long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account with id " + accountId + " not found"));
    }

    public void checkStatusCLOSEAccount(Account account) {
        if (account.getStatus() == AccountStatus.CLOSE) {
            log.warn("The account with id {} is already closed", account.getId());
            throw new IllegalArgumentException("The account with id " + account.getId() + " is already closed");
        }
    }

    public void checkStatusFREEZEAccount(Account account) {
        if (account.getStatus() == AccountStatus.FREEZE) {
            log.warn("The account with id {} is already freeze", account.getId());
            throw new IllegalArgumentException("The account with id " + account.getId() + " is already freeze");
        }
    }

    public void checkStatusUnblockAccount(Account account) {
        if (account.getStatus() == AccountStatus.FREEZE) {
            log.warn("The account with id {} is already freeze", account.getId());
            throw new IllegalArgumentException("The account with id " + account.getId() + " is already freeze");
        }
    }

    public void checkAccountToUser(Account account, long userId) {
        if (account.getOwnerId() != account.getId()) {
            log.error("Account with id {} owner id {} not match", account.getId(), userId);
            throw new IllegalArgumentException("The account with id " + account.getId() + " does not belong to the user " + userId);
        }
    }

    public Account validateAccount(String number) {
        Optional<Account> account = accountRepository.findByNumber(number);
        if (account.isEmpty()) {
            log.error("Account with number {} not found", number);
            throw new EntityNotFoundException("The account with number " + number + " not found");
        }
        return account.get();
    }
}
