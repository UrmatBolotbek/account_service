package faang.school.accountservice.service.tariff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.mapper.tariff.TariffMapper;
import faang.school.accountservice.model.tariff.Tariff;
import faang.school.accountservice.repository.TariffRepository;
import faang.school.accountservice.service.interest_rate.InterestRateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TariffService {

    private final InterestRateService interestRateService;
    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public TariffResponseDto create(TariffRequestDto tariffRequestDto, long userId) {
        Tariff tariff = tariffMapper.toEntity(tariffRequestDto);
        setInterestRateHistoryAndUser(tariffRequestDto, userId, tariff);
        Tariff createdTariff = tariffRepository.save(tariff);
        log.info("Tariff created: {}", createdTariff);
        return tariffMapper.toDto(createdTariff);
    }

    @Transactional
    public TariffResponseDto update(Long TariffId, TariffRequestDto tariffRequestDto, long userId) {
        Tariff tariff = tariffRepository.getReferenceById(TariffId);
        setInterestRateHistoryAndUser(tariffRequestDto, userId, tariff);
        Tariff updatedTariff = tariffRepository.save(tariff);
        log.info("Tariff updated: {}", updatedTariff);
        return tariffMapper.toDto(updatedTariff);
    }

    public TariffResponseDto get(Long TariffId) {
        log.info("Retrieving Tariff: {}", TariffId);
        Tariff tariff = tariffRepository.getReferenceById(TariffId);
        return tariffMapper.toDto(tariff);
    }

    public Tariff getTariffEntity(Long TariffId) {
        log.info("Retrieving TariffEntity: {}", TariffId);
        return tariffRepository.getReferenceById(TariffId);
    }

    public void delete(Long TariffId) {
        log.info("Deleting Tariff: {}", TariffId);
        tariffRepository.deleteById(TariffId);
    }

    private void setInterestRateHistoryAndUser(TariffRequestDto tariffRequestDto, long userId, Tariff tariff) {
        tariff.setInterestRate(interestRateService.getInterestRateEntity(tariffRequestDto.getInterestRateId()));
        tariff.setCreatorOrChangerUserId(userId);
        addRateToHistory(tariff, tariffRequestDto);
    }

    private void addRateToHistory(Tariff tariff, TariffRequestDto tariffRequestDto) {
        try {
            List<Double> rates = new ArrayList<>();
            if (tariff.getRateHistory() != null) {
                rates = objectMapper.readValue(tariff.getRateHistory(), new TypeReference<>() {
                });
            }
            rates.add(interestRateService.getInterestRateEntity(tariffRequestDto.getInterestRateId()).getInterestRate());
            tariff.setRateHistory(objectMapper.writeValueAsString(rates));
        } catch (JsonProcessingException e) {
            log.error("Json processing error", e);
            throw new RuntimeException("Json processing error", e);
        }
    }
}
