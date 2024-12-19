package faang.school.accountservice.controller.interest_rate;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.interest_rate.InterestRateRequestDto;
import faang.school.accountservice.dto.interest_rate.InterestRateResponseDto;
import faang.school.accountservice.service.interest_rate.InterestRateService;
import faang.school.accountservice.validator.InterestRateValidator;
import faang.school.accountservice.validator.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/interest_rates")
public class InterestRateController {

    private final UserValidator userValidator;
    private final InterestRateValidator interestRateValidator;
    private final InterestRateService interestRateService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterestRateResponseDto create(@Valid @RequestBody InterestRateRequestDto interestRateDto) {
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);
        interestRateValidator.validateInterestRateDoesNotExceedMax(interestRateDto);
        log.info("Received request to create a new interest rate {} by user with id {}",
                interestRateDto.getInterestRate(), userId);
        return interestRateService.create(interestRateDto, userId);
    }

    @PutMapping("/{interestRateId}")
    public InterestRateResponseDto update(@PathVariable("interestRateId") Long interestRateId,
                                          @Valid @RequestBody InterestRateRequestDto interestRateDto) {
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);
        interestRateValidator.validateInterestRateDoesNotExceedMax(interestRateDto);
        log.info("Received a request to update an interestRate with id {} to {} by user with id {}",
                interestRateId, interestRateDto.getInterestRate(), userId);
        return interestRateService.update(interestRateId, interestRateDto, userId);
    }

    @GetMapping("/{interestRateId}")
    public InterestRateResponseDto get(@PathVariable("interestRateId") Long interestRateId) {
        log.info("Received a request to get an interestRate with id {}", interestRateId);
        return interestRateService.get(interestRateId);
    }

    @DeleteMapping("/{interestRateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("interestRateId") Long interestRateId) {
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);
        log.info("Received a request to delete an interestRate with id {} by user with id {} ",
                interestRateId, userId);
        interestRateService.delete(interestRateId);
    }
}