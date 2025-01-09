package faang.school.accountservice.service.interest_rate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.interest_rate.InterestRateDto;
import faang.school.accountservice.mapper.interest_rate.InterestRateMapper;
import faang.school.accountservice.model.interest_rate.InterestRate;
import faang.school.accountservice.model.interest_rate.InterestRateChangeRecord;
import faang.school.accountservice.repository.InterestRateRepository;
import faang.school.accountservice.validator.interest_rate.InterestRateValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class InterestRateServiceTest {

    @InjectMocks
    private InterestRateService interestRateService;

    @Mock
    private InterestRateRepository interestRateRepository;

    @Mock
    private InterestRateValidator interestRateValidator;

    @Mock
    private InterestRateMapper interestRateMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldSaveAndReturnInterestRateDto() throws Exception {
        InterestRateDto requestDto = new InterestRateDto();
        requestDto.setInterestRate(new BigDecimal("5.0"));

        InterestRate interestRate = new InterestRate();
        interestRate.setInterestRate(new BigDecimal("5.0"));

        InterestRate savedInterestRate = new InterestRate();
        savedInterestRate.setId(1L);
        savedInterestRate.setInterestRate(new BigDecimal("5.0"));

        InterestRateDto responseDto = new InterestRateDto();
        responseDto.setId(1L);
        responseDto.setInterestRate(new BigDecimal("5.0"));

        Mockito.when(interestRateMapper.toEntity(requestDto)).thenReturn(interestRate);
        Mockito.when(interestRateRepository.save(interestRate)).thenReturn(savedInterestRate);
        Mockito.when(interestRateMapper.toDto(savedInterestRate)).thenReturn(responseDto);

        InterestRateDto result = interestRateService.create(requestDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("5.0"), result.getInterestRate());
        Mockito.verify(interestRateRepository).save(interestRate);
    }

    @Test
    void update_ShouldUpdateAndReturnInterestRateDto() throws Exception {
        Long interestRateId = 1L;
        InterestRateDto requestDto = new InterestRateDto();
        requestDto.setInterestRate(new BigDecimal("6.0"));

        InterestRate existingInterestRate = new InterestRate();
        existingInterestRate.setId(interestRateId);
        existingInterestRate.setInterestRate(new BigDecimal("5.0"));

        InterestRate updatedInterestRate = new InterestRate();
        updatedInterestRate.setId(interestRateId);
        updatedInterestRate.setInterestRate(new BigDecimal("6.0"));

        InterestRateDto responseDto = new InterestRateDto();
        responseDto.setId(interestRateId);
        responseDto.setInterestRate(new BigDecimal("6.0"));

        Mockito.when(interestRateValidator.validateInterestRateExists(interestRateId)).thenReturn(existingInterestRate);
        Mockito.when(interestRateRepository.save(existingInterestRate)).thenReturn(updatedInterestRate);
        Mockito.when(interestRateMapper.toDto(updatedInterestRate)).thenReturn(responseDto);

        InterestRateDto result = interestRateService.update(interestRateId, requestDto, 1L);

        assertNotNull(result);
        assertEquals(interestRateId, result.getId());
        assertEquals(new BigDecimal("6.0"), result.getInterestRate());
        Mockito.verify(interestRateRepository).save(existingInterestRate);
    }

    @Test
    void get_ShouldReturnInterestRateDto() {
        Long interestRateId = 1L;
        InterestRate interestRate = new InterestRate();
        interestRate.setId(interestRateId);

        InterestRateDto responseDto = new InterestRateDto();
        responseDto.setId(interestRateId);

        Mockito.when(interestRateValidator.validateInterestRateExists(interestRateId)).thenReturn(interestRate);
        Mockito.when(interestRateMapper.toDto(interestRate)).thenReturn(responseDto);

        InterestRateDto result = interestRateService.get(interestRateId);

        assertNotNull(result);
        assertEquals(interestRateId, result.getId());
    }

    @Test
    void getAll_ShouldReturnListOfInterestRateDtos() {
        List<InterestRate> interestRates = List.of(new InterestRate(), new InterestRate());
        List<InterestRateDto> responseDtos = List.of(new InterestRateDto(), new InterestRateDto());

        Mockito.when(interestRateRepository.findAll()).thenReturn(interestRates);
        Mockito.when(interestRateMapper.toListDto(interestRates)).thenReturn(responseDtos);

        List<InterestRateDto> result = interestRateService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void delete_ShouldCallRepositoryToDelete() {
        Long interestRateId = 1L;

        Mockito.doNothing().when(interestRateRepository).deleteById(interestRateId);
        Mockito.when(interestRateValidator.validateInterestRateExists(interestRateId)).thenReturn(new InterestRate());

        interestRateService.delete(interestRateId);

        Mockito.verify(interestRateRepository).deleteById(interestRateId);
    }

    @Test
    void getInterestRateChangeRecords_ShouldReturnListOfChangeRecords() throws Exception {
        Long interestRateId = 1L;
        InterestRate interestRate = new InterestRate();
        interestRate.setChangedByUserHistory("[{\"userId\":1,\"action\":\"CREATE\",\"oldValue\":null,\"newValue\":5.0}]");

        Mockito.when(interestRateValidator.validateInterestRateExists(interestRateId)).thenReturn(interestRate);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class)))
                .thenReturn(List.of(new InterestRateChangeRecord()));

        List<InterestRateChangeRecord> result = interestRateService.getInterestRateChangeRecords(interestRateId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}