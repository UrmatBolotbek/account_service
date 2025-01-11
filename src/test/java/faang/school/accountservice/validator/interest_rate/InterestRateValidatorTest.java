package faang.school.accountservice.validator.interest_rate;


import faang.school.accountservice.dto.interest_rate.InterestRateDto;
import faang.school.accountservice.exception.interest_rate.InterestRateNotFound;
import faang.school.accountservice.exception.interest_rate.MaximumAllowedInterestRateException;
import faang.school.accountservice.model.interest_rate.InterestRate;
import faang.school.accountservice.repository.InterestRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InterestRateValidatorTest {

    @InjectMocks
    private InterestRateValidator interestRateValidator;

    @Mock
    private InterestRateRepository interestRateRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(interestRateValidator, "maxAllowedInterestRate", new BigDecimal("15.00"));
    }

    @Test
    void validateInterestRateDoesNotExceedMax_ShouldThrowException_WhenRateExceedsMax() {
        InterestRateDto requestDto = new InterestRateDto();
        requestDto.setInterestRate(new BigDecimal("16.00"));

        MaximumAllowedInterestRateException exception = assertThrows(
                MaximumAllowedInterestRateException.class,
                () -> interestRateValidator.validateInterestRateDoesNotExceedMax(requestDto)
        );
        assertEquals("Interest rate cannot exceed the maximum allowed value", exception.getMessage());
    }

    @Test
    void validateInterestRateDoesNotExceedMax_ShouldPass_WhenRateIsWithinLimit() {
        InterestRateDto requestDto = new InterestRateDto();
        requestDto.setInterestRate(new BigDecimal("4.50"));

        assertDoesNotThrow(() -> interestRateValidator.validateInterestRateDoesNotExceedMax(requestDto));
    }

    @Test
    void validateInterestRateExists_ShouldThrowException_WhenInterestRateNotFound() {
        Long interestRateId = 1L;

        Mockito.when(interestRateRepository.findById(interestRateId)).thenReturn(Optional.empty());

        InterestRateNotFound exception = assertThrows(
                InterestRateNotFound.class,
                () -> interestRateValidator.validateInterestRateExists(interestRateId)
        );

        assertEquals("Interest rate with id: " + interestRateId + " does not exist", exception.getMessage());
        Mockito.verify(interestRateRepository).findById(interestRateId);
    }

    @Test
    void validateInterestRateExists_ShouldReturnInterestRate_WhenFound() {
        Long interestRateId = 1L;
        InterestRate interestRate = new InterestRate();
        interestRate.setId(interestRateId);

        Mockito.when(interestRateRepository.findById(interestRateId)).thenReturn(Optional.of(interestRate));

        InterestRate result = interestRateValidator.validateInterestRateExists(interestRateId);

        assertNotNull(result);
        assertEquals(interestRateId, result.getId());
        Mockito.verify(interestRateRepository).findById(interestRateId);
    }
}