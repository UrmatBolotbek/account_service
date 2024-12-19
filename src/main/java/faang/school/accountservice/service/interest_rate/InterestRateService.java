package faang.school.accountservice.service.interest_rate;

import faang.school.accountservice.dto.interest_rate.InterestRateRequestDto;
import faang.school.accountservice.dto.interest_rate.InterestRateResponseDto;
import faang.school.accountservice.mapper.interest_rate.InterestRateMapper;
import faang.school.accountservice.model.interest_rate.InterestRate;
import faang.school.accountservice.repository.InterestRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestRateService {

    private final InterestRateRepository interestRateRepository;
    private final InterestRateMapper interestRateMapper;

    public InterestRateResponseDto create(InterestRateRequestDto requestDto, long userId) {
        InterestRate interestRate = interestRateMapper.toEntity(requestDto);
        interestRate.setCreatorOrChangerUserId(userId);
        interestRate = interestRateRepository.save(interestRate);
        log.info("Created interest rate {}", interestRate);
        return interestRateMapper.toDto(interestRate);
    }

    public InterestRateResponseDto update(Long interestRateId, InterestRateRequestDto requestDto, long userId) {
        InterestRate interestRate = interestRateRepository.getReferenceById(interestRateId);
        interestRate.setInterestRate(requestDto.getInterestRate());
        interestRate.setCreatorOrChangerUserId(userId);
        interestRate = interestRateRepository.save(interestRate);
        log.info("Updated interest rate {}", interestRate);
        return interestRateMapper.toDto(interestRate);
    }

    public InterestRateResponseDto get(Long interestRateId) {
        log.info("Retrieving interest rate {}", interestRateId);
        InterestRate interestRate = interestRateRepository.getReferenceById(interestRateId);
        return interestRateMapper.toDto(interestRate);
    }

    public InterestRate getInterestRateEntity(Long interestRateId) {
        log.info("Retrieving InterestRateEntity by id {}", interestRateId);
        return interestRateRepository.getReferenceById(interestRateId);
    }

    public void delete(Long interestRateId) {
        log.info("Deleting interest rate {}", interestRateId);
        interestRateRepository.deleteById(interestRateId);
    }
}