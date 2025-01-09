package faang.school.accountservice.controller.tariff;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.model.interest_rate.InterestRate;
import faang.school.accountservice.model.tariff.TariffChangeRecord;
import faang.school.accountservice.model.tariff.TariffType;
import faang.school.accountservice.service.tariff.TariffService;
import faang.school.accountservice.validator.user.UserValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TariffController.class)
class TariffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffService tariffService;

    @MockBean
    private UserValidator userValidator;

    @MockBean
    private UserContext userContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_HEADER = "x-user-id";

    @Test
    void create_ShouldReturnCreatedTariff() throws Exception {
        InterestRate interestRate = new InterestRate();
        interestRate.setId(5L);
        interestRate.setInterestRate(BigDecimal.valueOf(5));

        TariffRequestDto requestDto = new TariffRequestDto();
        requestDto.setTariffType(TariffType.GENERAL);
        requestDto.setInterestRateId(5L);

        TariffResponseDto responseDto = new TariffResponseDto();
        responseDto.setId(1L);
        responseDto.setTariffType(TariffType.GENERAL);
        responseDto.setCurrentInterestRate(BigDecimal.valueOf(5));

        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(tariffService.create(Mockito.any(TariffRequestDto.class), Mockito.eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/tariffs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(USER_HEADER, "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tariffType").value("GENERAL"));

        Mockito.verify(userValidator).validateUserExists(1L);
        Mockito.verify(tariffService).create(Mockito.any(TariffRequestDto.class), Mockito.eq(1L));
    }

    @Test
    void update_ShouldReturnUpdatedTariff() throws Exception {
        InterestRate interestRate = new InterestRate();
        interestRate.setId(5L);
        interestRate.setInterestRate(BigDecimal.valueOf(5));

        TariffRequestDto requestDto = new TariffRequestDto();
        requestDto.setTariffType(TariffType.PROMO);
        requestDto.setInterestRateId(5L);

        TariffResponseDto responseDto = new TariffResponseDto();
        responseDto.setId(1L);
        responseDto.setTariffType(TariffType.PROMO);
        responseDto.setCurrentInterestRate(BigDecimal.valueOf(5));

        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(tariffService.update(Mockito.eq(1L), Mockito.any(TariffRequestDto.class), Mockito.eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/tariffs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tariffType").value("PROMO"));

        Mockito.verify(userValidator).validateUserExists(1L);
        Mockito.verify(tariffService).update(Mockito.eq(1L), Mockito.any(TariffRequestDto.class), Mockito.eq(1L));
    }

    @Test
    void get_ShouldReturnTariff() throws Exception {
        TariffResponseDto responseDto = new TariffResponseDto();
        responseDto.setId(1L);
        responseDto.setTariffType(TariffType.SUBSCRIPTION);

        Mockito.when(tariffService.get(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/tariffs/1")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tariffType").value("SUBSCRIPTION"));
    }

    @Test
    void getAll_ShouldReturnListOfTariffs() throws Exception {
        TariffResponseDto tariff1 = new TariffResponseDto();
        tariff1.setId(1L);
        tariff1.setTariffType(TariffType.GENERAL);

        TariffResponseDto tariff2 = new TariffResponseDto();
        tariff2.setId(2L);
        tariff2.setTariffType(TariffType.PROMO);

        Mockito.when(tariffService.getAll()).thenReturn(List.of(tariff1, tariff2));

        mockMvc.perform(get("/api/v1/tariffs")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tariffType").value("GENERAL"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].tariffType").value("PROMO"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        Mockito.when(userContext.getUserId()).thenReturn(1L);

        mockMvc.perform(delete("/api/v1/tariffs/1")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userValidator).validateUserExists(1L);
        Mockito.verify(tariffService).delete(1L);
    }

    @Test
    void getHistory_ShouldReturnTariffChangeHistory() throws Exception {
        TariffChangeRecord record1 = new TariffChangeRecord();
        record1.setUserId(1L);
        record1.setAction("CREATE");

        TariffChangeRecord record2 = new TariffChangeRecord();
        record2.setUserId(1L);
        record2.setAction("UPDATE");

        List<TariffChangeRecord> history = List.of(record1, record2);

        Mockito.when(tariffService.getTariffChangeRecords(1L)).thenReturn(history);

        mockMvc.perform(get("/api/v1/tariffs/1/history")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.changeHistory[0].userId").value(1))
                .andExpect(jsonPath("$.changeHistory[0].action").value("CREATE"))
                .andExpect(jsonPath("$.changeHistory[1].userId").value(1))
                .andExpect(jsonPath("$.changeHistory[1].action").value("UPDATE"));
    }
}