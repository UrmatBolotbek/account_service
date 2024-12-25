package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceHasBeenUpdatedException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final BalanceAuditService balanceAuditService;


    @Transactional(readOnly = true)
    public ResponseBalanceDto getBalance(Long accountId) {
        return balanceMapper.toDto(findBalance(accountId));
    }

    @Transactional
    public Balance createBalance(Account account) {
        log.info("Creating a balance for account with id {}", account.getId());
        Balance balance = saveBalance(Balance.builder().account(account).build());
        account.setBalance(balance);
        balanceAuditService.createAudit(balance, null);
        log.info("Balance with id {} is created", balance.getId());
        return balance;
    }

    @Transactional
    public ResponseBalanceDto updateBalance(UpdateBalanceDto balanceDto, long operationId) {
        log.info("Updating balance for account with id {}", balanceDto.getAccountId());
        Balance balance = findBalance(balanceDto.getAccountId());
        balanceMapper.toUpdateDto(balance);
        Balance updatedBalance = saveBalance(balance);
        balanceAuditService.createAudit(updatedBalance, operationId);

        log.info("Balance with id {} updated", balance.getId());
        return balanceMapper.toDto(updatedBalance);
    }

    private Balance findBalance(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.warn("Balance not found for account with id: {}", accountId);
                    return new AccountNotFoundException("Account not found");
                });
    }

    private Balance saveBalance(Balance balance) {
        try {
            return balanceRepository.saveAndFlush(balance);
        } catch (OptimisticLockingFailureException exception) {
            throw new BalanceHasBeenUpdatedException(balance.getId());
        }
    }
}
