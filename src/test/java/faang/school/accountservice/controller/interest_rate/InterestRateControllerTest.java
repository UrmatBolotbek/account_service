package faang.school.accountservice.controller.interest_rate;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.interest_rate.InterestRateDto;
import faang.school.accountservice.model.interest_rate.InterestRateChangeRecord;
import faang.school.accountservice.service.interest_rate.InterestRateService;
import faang.school.accountservice.validator.interest_rate.InterestRateValidator;
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

@WebMvcTest(InterestRateController.class)
class InterestRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterestRateValidator interestRateValidator;

    @MockBean
    private InterestRateService interestRateService;

    @MockBean
    private UserContext userContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_HEADER = "x-user-id";

    @Test
    void create_ShouldReturnCreatedInterestRate() throws Exception {
        InterestRateDto requestDto = new InterestRateDto();
        requestDto.setInterestRate(new BigDecimal("5.0"));

        InterestRateDto responseDto = new InterestRateDto();
        responseDto.setId(1L);
        responseDto.setInterestRate(new BigDecimal("5.0"));

        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(interestRateService.create(Mockito.any(InterestRateDto.class), Mockito.eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/interest_rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(USER_HEADER, "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.interestRate").value(5.0));

        Mockito.verify(interestRateValidator).validateInterestRateDoesNotExceedMax(requestDto);
    }

    @Test
    void update_ShouldReturnUpdatedInterestRate() throws Exception {
        InterestRateDto requestDto = new InterestRateDto();
        requestDto.setInterestRate(new BigDecimal("6.0"));

        InterestRateDto responseDto = new InterestRateDto();
        responseDto.setId(1L);
        responseDto.setInterestRate(new BigDecimal("6.0"));

        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(interestRateService.update(Mockito.eq(1L), Mockito.any(InterestRateDto.class), Mockito.eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/interest_rates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.interestRate").value(6.0));

        Mockito.verify(interestRateValidator).validateInterestRateDoesNotExceedMax(requestDto);
    }

    @Test
    void get_ShouldReturnInterestRate() throws Exception {
        InterestRateDto responseDto = new InterestRateDto();
        responseDto.setId(1L);
        responseDto.setInterestRate(new BigDecimal("5.0"));

        Mockito.when(interestRateService.get(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/interest_rates/1")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.interestRate").value(5.0));
    }

    @Test
    void getAll_ShouldReturnListOfInterestRates() throws Exception {
        InterestRateDto dto1 = new InterestRateDto();
        dto1.setId(1L);
        dto1.setInterestRate(new BigDecimal("5.0"));

        InterestRateDto dto2 = new InterestRateDto();
        dto2.setId(2L);
        dto2.setInterestRate(new BigDecimal("6.0"));

        Mockito.when(interestRateService.getAll()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/v1/interest_rates")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].interestRate").value(5.0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].interestRate").value(6.0));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        Mockito.when(userContext.getUserId()).thenReturn(1L);


        mockMvc.perform(delete("/api/v1/interest_rates/1")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isNoContent());

        Mockito.verify(interestRateService).delete(1L, 1L);
    }

    @Test
    void getHistory_ShouldReturnInterestRateChangeHistory() throws Exception {
        Long interestRateId = 1L;

        InterestRateChangeRecord record1 = new InterestRateChangeRecord();
        record1.setUserId(1L);
        record1.setAction("CREATE");
        record1.setOldValue(null);
        record1.setNewValue(new BigDecimal("5.0"));

        InterestRateChangeRecord record2 = new InterestRateChangeRecord();
        record2.setUserId(2L);
        record2.setAction("UPDATE");
        record2.setOldValue(new BigDecimal("5.0"));
        record2.setNewValue(new BigDecimal("6.0"));

        List<InterestRateChangeRecord> history = List.of(record1, record2);

        Mockito.when(interestRateService.getInterestRateChangeRecords(interestRateId)).thenReturn(history);

        mockMvc.perform(get("/api/v1/interest_rates/1/history")
                        .header("x-user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.changeHistory[0].userId").value(1))
                .andExpect(jsonPath("$.changeHistory[0].action").value("CREATE"))
                .andExpect(jsonPath("$.changeHistory[0].oldValue").doesNotExist())
                .andExpect(jsonPath("$.changeHistory[0].newValue").value(5.0))
                .andExpect(jsonPath("$.changeHistory[1].userId").value(2))
                .andExpect(jsonPath("$.changeHistory[1].action").value("UPDATE"))
                .andExpect(jsonPath("$.changeHistory[1].oldValue").value(5.0))
                .andExpect(jsonPath("$.changeHistory[1].newValue").value(6.0));

        Mockito.verify(interestRateService).getInterestRateChangeRecords(interestRateId);
    }
}