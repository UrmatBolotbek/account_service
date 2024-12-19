package faang.school.accountservice.validator;

import faang.school.accountservice.dto.interest_rate.InterestRateRequestDto;
import faang.school.accountservice.exception.MaximumAllowedInterestRateException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
@Slf4j
public class InterestRateValidator {

    @Value("${maximum_allowed_interest_rate}")
    private Double maxAllowedInterestRate;

    public void validateInterestRateDoesNotExceedMax(InterestRateRequestDto requestDto) {
        if(requestDto.getInterestRate() > maxAllowedInterestRate) {
            log.error("Maximum allowed interest rate exceeded");
            throw new MaximumAllowedInterestRateException("Interest rate cannot exceed maximum allowed value");
        }
    }
}