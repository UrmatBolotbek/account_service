package faang.school.accountservice.controller.savings_account;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.savings_account.SavingsAccountRequestDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.model.tariff.TariffType;
import faang.school.accountservice.service.savings_account.SavingsAccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SavingsAccountController.class)
class SavingsAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SavingsAccountService savingsAccountService;

    @MockBean
    private UserContext userContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_HEADER = "x-user-id";

    @Test
    void create_ShouldReturnCreatedSavingsAccount() throws Exception {
        SavingsAccountRequestDto requestDto = SavingsAccountRequestDto.builder()
                .accountId(1L)
                .tariffId(2L)
                .build();

        TariffResponseDto tariffResponseDto = TariffResponseDto.builder()
                .id(2L)
                .tariffType(TariffType.GENERAL)
                .build();

        SavingsAccountResponseDto responseDto = SavingsAccountResponseDto.builder()
                .savingsAccountId(1L)
                .tariffAndInterestRate(tariffResponseDto)
                .build();

        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(savingsAccountService.create(Mockito.any(SavingsAccountRequestDto.class), Mockito.eq(1L))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/savings_accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(USER_HEADER, "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.savingsAccountId").value(1))
                .andExpect(jsonPath("$.tariffAndInterestRate.id").value(2))
                .andExpect(jsonPath("$.tariffAndInterestRate.tariffType").value("GENERAL"));

        Mockito.verify(savingsAccountService).create(Mockito.any(SavingsAccountRequestDto.class), Mockito.eq(1L));
    }

    @Test
    void getById_ShouldReturnSavingsAccount() throws Exception {
        TariffResponseDto tariffResponseDto = TariffResponseDto.builder()
                .id(2L)
                .tariffType(TariffType.GENERAL)
                .build();

        SavingsAccountResponseDto responseDto = SavingsAccountResponseDto.builder()
                .savingsAccountId(1L)
                .tariffAndInterestRate(tariffResponseDto)
                .build();

        Mockito.when(savingsAccountService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/savings_accounts/1")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.savingsAccountId").value(1))
                .andExpect(jsonPath("$.tariffAndInterestRate.id").value(2))
                .andExpect(jsonPath("$.tariffAndInterestRate.tariffType").value("GENERAL"));

        Mockito.verify(savingsAccountService).getById(1L);
    }

    @Test
    void getAllByOwnerId_ShouldReturnListOfSavingsAccounts() throws Exception {
        TariffResponseDto tariff1 = TariffResponseDto.builder()
                .id(2L)
                .tariffType(TariffType.GENERAL)
                .build();

        TariffResponseDto tariff2 = TariffResponseDto.builder()
                .id(3L)
                .tariffType(TariffType.PROMO)
                .build();

        SavingsAccountResponseDto account1 = SavingsAccountResponseDto.builder()
                .savingsAccountId(1L)
                .tariffAndInterestRate(tariff1)
                .build();

        SavingsAccountResponseDto account2 = SavingsAccountResponseDto.builder()
                .savingsAccountId(2L)
                .tariffAndInterestRate(tariff2)
                .build();

        Mockito.when(savingsAccountService.getAllByOwnerId(1L)).thenReturn(List.of(account1, account2));

        mockMvc.perform(get("/api/v1/savings_accounts/owner/1")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].savingsAccountId").value(1))
                .andExpect(jsonPath("$[0].tariffAndInterestRate.id").value(2))
                .andExpect(jsonPath("$[0].tariffAndInterestRate.tariffType").value("GENERAL"))
                .andExpect(jsonPath("$[1].savingsAccountId").value(2))
                .andExpect(jsonPath("$[1].tariffAndInterestRate.id").value(3))
                .andExpect(jsonPath("$[1].tariffAndInterestRate.tariffType").value("PROMO"));

        Mockito.verify(savingsAccountService).getAllByOwnerId(1L);
    }
}