package faang.school.accountservice.service.savings_account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.calculator.savings_acccount.InterestCalculator;
import faang.school.accountservice.config.executor.ExecutorServiceConfig;
import faang.school.accountservice.dto.savings_account.SavingsAccountRequestDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.mapper.savings_account_mapper.SavingsAccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import faang.school.accountservice.model.tariff.Tariff;
import faang.school.accountservice.model.tariff.TariffType;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.service.tariff.TariffService;
import faang.school.accountservice.validator.savings_account.SavingsAccountValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SavingsAccountServiceTest {

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SavingsAccountMapper savingsAccountMapper;

    @Mock
    private SavingsAccountRepository savingsAccountRepository;

    @Mock
    private SavingsAccountValidator savingsAccountValidator;

    @Mock
    private TariffService tariffService;

    @Mock
    private AccountService accountService;

    @Mock
    private InterestCalculator interestCalculator;

    @Mock
    private ExecutorServiceConfig executorService;

    @Test
    void create_ShouldSaveAndReturnSavingsAccountResponseDto() throws Exception {
        SavingsAccountRequestDto requestDto = new SavingsAccountRequestDto();
        requestDto.setAccountId(1L);
        requestDto.setTariffId(2L);

        Account account = new Account();
        account.setId(1L);

        Tariff tariff = new Tariff();
        tariff.setId(2L);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccount(account);
        savingsAccount.setTariff(tariff);

        SavingsAccount savedSavingsAccount = new SavingsAccount();
        savedSavingsAccount.setId(1L);

        SavingsAccountResponseDto responseDto = new SavingsAccountResponseDto();
        responseDto.setSavingsAccountId(1L);

        Mockito.when(accountService.getAccountEntity(1L)).thenReturn(account);
        Mockito.when(tariffService.getTariffEntity(2L)).thenReturn(tariff);
        Mockito.when(savingsAccountMapper.toEntity(requestDto)).thenReturn(savingsAccount);
        Mockito.when(savingsAccountRepository.save(savingsAccount)).thenReturn(savedSavingsAccount);
        Mockito.when(savingsAccountMapper.toDto(savedSavingsAccount)).thenReturn(responseDto);

        SavingsAccountResponseDto result = savingsAccountService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getSavingsAccountId());
        Mockito.verify(savingsAccountRepository).save(savingsAccount);
    }

    @Test
    void getById_ShouldReturnSavingsAccountResponseDto() {
        Long savingsAccountId = 1L;
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(savingsAccountId);

        SavingsAccountResponseDto responseDto = new SavingsAccountResponseDto();
        responseDto.setSavingsAccountId(savingsAccountId);

        Mockito.when(savingsAccountValidator.validateSavingsAccountExists(savingsAccountId)).thenReturn(savingsAccount);
        Mockito.when(savingsAccountMapper.toDto(savingsAccount)).thenReturn(responseDto);

        SavingsAccountResponseDto result = savingsAccountService.getById(savingsAccountId);

        assertNotNull(result);
        assertEquals(savingsAccountId, result.getSavingsAccountId());
    }

    @Test
    void getAllByOwnerId_ShouldReturnListOfSavingsAccountResponseDtos() {
        Long ownerId = 1L;
        List<SavingsAccount> savingsAccounts = List.of(new SavingsAccount(), new SavingsAccount());
        List<SavingsAccountResponseDto> responseDtos = List.of(new SavingsAccountResponseDto(), new SavingsAccountResponseDto());

        Mockito.when(savingsAccountRepository.findByOwnerId(ownerId)).thenReturn(savingsAccounts);
        Mockito.when(savingsAccountMapper.toDto(Mockito.any(SavingsAccount.class))).thenReturn(new SavingsAccountResponseDto());

        List<SavingsAccountResponseDto> result = savingsAccountService.getAllByOwnerId(ownerId);

        assertNotNull(result);
        assertEquals(2, result.size());
        Mockito.verify(savingsAccountRepository).findByOwnerId(ownerId);
    }

    @Test
    void addTariffToHistory_ShouldAddTariffToHistory() throws Exception {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setTariffHistory("[{\"type\":\"GENERAL\"}]");

        Tariff tariff = new Tariff();
        tariff.setTariffType(TariffType.SUBSCRIPTION);

        SavingsAccountRequestDto requestDto = new SavingsAccountRequestDto();
        requestDto.setTariffId(2L);

        Mockito.when(objectMapper.readValue(Mockito.eq("[{\"type\":\"GENERAL\"}]"), Mockito.any(TypeReference.class)))
                .thenReturn(new ArrayList<>(List.of(TariffType.GENERAL)));

        Mockito.when(tariffService.getTariffEntity(2L)).thenReturn(tariff);

        Mockito.when(objectMapper.writeValueAsString(Mockito.any()))
                .thenReturn("[{\"type\":\"GENERAL\"},{\"type\":\"SUBSCRIPTION\"}]");

        Method addTariffToHistory = SavingsAccountService.class.getDeclaredMethod("addTariffToHistory", SavingsAccount.class, SavingsAccountRequestDto.class);
        addTariffToHistory.setAccessible(true);
        addTariffToHistory.invoke(savingsAccountService, savingsAccount, requestDto);

        assertEquals("[{\"type\":\"GENERAL\"},{\"type\":\"SUBSCRIPTION\"}]", savingsAccount.getTariffHistory());
    }
}