package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestAccountDto;
import faang.school.accountservice.dto.ResponseAccountDto;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator validator;

    @Transactional
    public ResponseAccountDto createAccount(RequestAccountDto requestAccountDto, long userId) {
        Account account = accountMapper.toAccount(requestAccountDto);
        int version = account.getVersion();
        account.setVersion(++version);
        account.setOwnerId(userId);
        account.setStatus(AccountStatus.OPEN);
        accountRepository.save(account);
        log.info("Created account for user {}", userId);
        return accountMapper.toResponseAccountDto(account);
    }

    @Transactional(readOnly = true)
    public ResponseAccountDto getAccount(long accountId, long userId) {
        Account account = validator.checkAccount(accountId, userId);
        log.info(" Getting an account with id {} user with id {}", accountId, userId);
        return accountMapper.toResponseAccountDto(account);
    }

    @Transactional
    public ResponseAccountDto blockAccount(long accountId, long userId) {
        Account account = validator.checkAccount(accountId, userId);
        validator.checkStatusAccount(account);
        account.setStatus(AccountStatus.FREEZE);
        account.setVersion(account.getVersion() + 1);
        accountRepository.save(account);
        return accountMapper.toResponseAccountDto(account);
    }

    @Transactional
    public ResponseAccountDto unblockAccount(long accountId, long userId) {
        Account account = validator.checkAccount(accountId, userId);
        validator.checkStatusAccount(account);
        account.setStatus(AccountStatus.OPEN);
        account.setVersion(account.getVersion() + 1);
        accountRepository.save(account);
        log.info("Unblocking an account with id {} user with id {}", accountId, userId);
        return accountMapper.toResponseAccountDto(account);
    }

    @Transactional
    public ResponseAccountDto closeAccount(long accountId, long userId) {
        Account account = validator.checkAccount(accountId, userId);
        validator.checkStatusAccount(account);
        account.setStatus(AccountStatus.CLOSE);
        account.setVersion(account.getVersion() + 1);
        accountRepository.save(account);
        account.setClosedAt(LocalDateTime.now());
        log.info("Closing an account with id {} user with id {}", accountId, userId);
        return accountMapper.toResponseAccountDto(account);
    }

}
