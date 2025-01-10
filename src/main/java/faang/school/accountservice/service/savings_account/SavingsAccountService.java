package faang.school.accountservice.service.savings_account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.calculator.savings_acccount.InterestCalculator;
import faang.school.accountservice.dto.savings_account.SavingsAccountRequestDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.mapper.savings_account_mapper.SavingsAccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import faang.school.accountservice.model.tariff.TariffType;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.tariff.TariffService;
import faang.school.accountservice.validator.savings_account.SavingsAccountValidator;
import faang.school.accountservice.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class SavingsAccountService {
    private final ObjectMapper objectMapper;
    private final SavingsAccountMapper savingsAccountMapper;
    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountValidator savingsAccountValidator;
    private final TariffService tariffService;
    private final AccountService accountService;
    private final InterestCalculator interestCalculator;
    private final UserValidator userValidator;

    @Transactional
    public SavingsAccountResponseDto create(SavingsAccountRequestDto savingsAccountRequestDto, long userId) {
        userValidator.validateUserExists(userId);
        SavingsAccount savingsAccount = savingsAccountMapper.toEntity(savingsAccountRequestDto);

        Account account = accountService.getAccountEntity(savingsAccountRequestDto.getAccountId());
        savingsAccount.setAccount(account);
        savingsAccount.setTariff(tariffService.getTariffEntity(savingsAccountRequestDto.getTariffId()));
        savingsAccount.setBalance(BigDecimal.ZERO);

        addTariffToHistory(savingsAccount, savingsAccountRequestDto);
        SavingsAccount createdAccount = savingsAccountRepository.save(savingsAccount);
        log.info("Savings account created: {}", createdAccount);
        return savingsAccountMapper.toDto(createdAccount);
    }

    public SavingsAccountResponseDto getById(Long savingsAccountId) {
        log.info("Retrieving SavingsAccount with id: {}", savingsAccountId);
        SavingsAccount savingsAccount = savingsAccountValidator.validateSavingsAccountExists(savingsAccountId);
        return savingsAccountMapper.toDto(savingsAccount);
    }

    @Transactional(readOnly = true)
    public List<SavingsAccountResponseDto> getAllByOwnerId(Long ownerId) {
        log.info("Retrieving all savings accounts for owner with id: {}", ownerId);
        List<SavingsAccount> savingsAccounts = savingsAccountRepository.findByOwnerId(ownerId);
        log.info("Retrieved {} savings accounts for owner with id: {}", savingsAccounts.size(), ownerId);
        return savingsAccounts.stream()
                .map(savingsAccountMapper::toDto)
                .toList();
    }

    @Async("executor")
    public void calculatePercents() {
        log.info("start calculatePercents, thread name: {}", Thread.currentThread().getName());
        List<SavingsAccount> existingSavingsAccounts =
                savingsAccountRepository.findByLastInterestDateIsNullOrLastInterestDateLessThan(OffsetDateTime.now());

        interestCalculator.calculate(existingSavingsAccounts);
        savingsAccountRepository.saveAll(existingSavingsAccounts);
        log.info("finish calculatePercents, thread name: {}", Thread.currentThread().getName());
    }

    private void addTariffToHistory(SavingsAccount savingsAccount, SavingsAccountRequestDto savingsAccountRequestDto) {
        try {
            List<TariffType> tariffs = new ArrayList<>();
            if (savingsAccount.getTariffHistory() != null) {
                tariffs = objectMapper.readValue(savingsAccount.getTariffHistory(), new TypeReference<>() {
                });
            }
            tariffs.add(tariffService.getTariffEntity(savingsAccountRequestDto.getTariffId()).getTariffType());
            savingsAccount.setTariffHistory(objectMapper.writeValueAsString(tariffs));
        } catch (JsonProcessingException e) {
            log.error("Json processing error", e);
            throw new RuntimeException("Json processing error", e);
        }
    }
}