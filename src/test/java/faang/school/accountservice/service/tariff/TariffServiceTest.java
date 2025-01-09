package faang.school.accountservice.service.tariff;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.mapper.tariff.TariffMapper;
import faang.school.accountservice.model.interest_rate.InterestRate;
import faang.school.accountservice.model.tariff.Tariff;
import faang.school.accountservice.model.tariff.TariffChangeRecord;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.interest_rate.InterestRateService;
import faang.school.accountservice.validator.tariff.TariffValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    @InjectMocks
    private TariffService tariffService;

    @Mock
    private InterestRateService interestRateService;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private TariffMapper tariffMapper;

    @Mock
    private TariffValidator tariffValidator;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldSaveAndReturnTariffResponseDto() throws Exception {
        TariffRequestDto requestDto = new TariffRequestDto();
        requestDto.setInterestRateId(1L);

        Tariff tariff = new Tariff();
        Tariff savedTariff = new Tariff();
        savedTariff.setId(1L);

        TariffResponseDto responseDto = new TariffResponseDto();
        responseDto.setId(1L);

        Mockito.when(tariffMapper.toEntity(requestDto)).thenReturn(tariff);
        Mockito.when(tariffRepository.save(tariff)).thenReturn(savedTariff);
        Mockito.when(tariffMapper.toDto(savedTariff)).thenReturn(responseDto);

        TariffResponseDto result = tariffService.create(requestDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        Mockito.verify(tariffRepository).save(tariff);
    }

    @Test
    void update_ShouldUpdateAndReturnTariffResponseDto() throws Exception {
        Long tariffId = 1L;
        TariffRequestDto requestDto = new TariffRequestDto();
        requestDto.setInterestRateId(2L);

        Tariff existingTariff = new Tariff();
        existingTariff.setId(tariffId);
        InterestRate oldInterestRate = new InterestRate();
        oldInterestRate.setId(1L);
        existingTariff.setInterestRate(oldInterestRate);

        InterestRate newInterestRate = new InterestRate();
        newInterestRate.setId(2L);
        Tariff updatedTariff = new Tariff();
        updatedTariff.setId(tariffId);
        updatedTariff.setInterestRate(newInterestRate);

        TariffResponseDto responseDto = new TariffResponseDto();
        responseDto.setId(tariffId);

        Mockito.when(tariffValidator.validateTariffExists(tariffId)).thenReturn(existingTariff);
        Mockito.when(interestRateService.getInterestRateEntity(2L)).thenReturn(newInterestRate);
        Mockito.when(tariffRepository.save(existingTariff)).thenReturn(updatedTariff);
        Mockito.when(tariffMapper.toDto(updatedTariff)).thenReturn(responseDto);

        TariffResponseDto result = tariffService.update(tariffId, requestDto, 1L);

        assertNotNull(result);
        assertEquals(tariffId, result.getId());
        Mockito.verify(tariffRepository).save(existingTariff);
    }

    @Test
    void get_ShouldReturnTariffResponseDto() {
        Long tariffId = 1L;
        Tariff tariff = new Tariff();
        tariff.setId(tariffId);

        TariffResponseDto responseDto = new TariffResponseDto();
        responseDto.setId(tariffId);

        Mockito.when(tariffValidator.validateTariffExists(tariffId)).thenReturn(tariff);
        Mockito.when(tariffMapper.toDto(tariff)).thenReturn(responseDto);

        TariffResponseDto result = tariffService.get(tariffId);

        assertNotNull(result);
        assertEquals(tariffId, result.getId());
    }

    @Test
    void getAll_ShouldReturnListOfTariffResponseDtos() {
        List<Tariff> tariffs = List.of(new Tariff(), new Tariff());
        List<TariffResponseDto> responseDtos = List.of(new TariffResponseDto(), new TariffResponseDto());

        Mockito.when(tariffRepository.findAll()).thenReturn(tariffs);
        Mockito.when(tariffMapper.toDtos(tariffs)).thenReturn(responseDtos);

        List<TariffResponseDto> result = tariffService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void delete_ShouldCallRepositoryToDelete() {
        Long tariffId = 1L;

        Mockito.doNothing().when(tariffRepository).deleteById(tariffId);
        Mockito.when(tariffValidator.validateTariffExists(tariffId)).thenReturn(new Tariff());

        tariffService.delete(tariffId);

        Mockito.verify(tariffRepository).deleteById(tariffId);
    }

    @Test
    void getTariffChangeRecords_ShouldReturnListOfChangeRecords() throws Exception {
        Long tariffId = 1L;
        Tariff tariff = new Tariff();
        tariff.setChangedByUserHistory("[{\"userId\":1,\"action\":\"CREATE\",\"oldValue\":null,\"newValue\":1}]");

        Mockito.when(tariffValidator.validateTariffExists(tariffId)).thenReturn(tariff);
        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class)))
                .thenReturn(List.of(new TariffChangeRecord()));

        List<TariffChangeRecord> result = tariffService.getTariffChangeRecords(tariffId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}