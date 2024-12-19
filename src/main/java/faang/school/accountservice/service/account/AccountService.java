package faang.school.accountservice.service.account;

import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getAccountEntity(long id) {
        log.info("getAccountEntity by id: {}: ", id);
        return accountRepository.getReferenceById(id);
    }

    public Account getAccountByOwnerId(long id) {
        log.info("Getting Account by owner id: {}: ", id);
        return accountRepository.findByOwnerId(id)
                .orElseThrow(() -> new NoSuchElementException("SavingsAccount with ownerId: " + id + " doesn't exist"));
    }
}