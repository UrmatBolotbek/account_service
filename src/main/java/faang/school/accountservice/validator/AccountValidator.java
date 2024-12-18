package faang.school.accountservice.validator;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountValidator {

    private final AccountRepository accountRepository;

    public Account checkAccount(long accountId, long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account with id " + accountId + " not found"));
        if (account.getOwnerId() != userId) {
            log.error("Account with id {} owner id {} not match", accountId, userId);
            throw new IllegalArgumentException("The account with id " + accountId + " does not belong to the user " + userId);
        }
        return account;
    }

    public void checkStatusAccount(Account account) {
        if (account.getStatus() == AccountStatus.CLOSE) {
            log.warn("The account with id {} is already closed", account.getId());
            throw new IllegalArgumentException("The account with id " + account.getId() + " is already closed");
        }
    }

}
