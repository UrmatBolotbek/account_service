package faang.school.accountservice.controller.savings_account;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.savings_account.SavingsAccountRequestDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.service.savings_account.SavingsAccountService;
import faang.school.accountservice.validator.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/savings_accounts")
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;
    private final UserValidator userValidator;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountResponseDto create(@Valid @RequestBody SavingsAccountRequestDto savingsAccountRequestDto) {
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);
        log.info("Received request to create savings account {} by user {}",
                savingsAccountRequestDto, userId);
        return savingsAccountService.create(savingsAccountRequestDto);
    }

    @GetMapping("/{accountId}")
    public SavingsAccountResponseDto getById(@PathVariable long accountId) {
        log.info("Received request to get savings account with id {}", accountId);
        return savingsAccountService.getById(accountId);
    }

    @GetMapping("/{accountOwnerId}")
    public SavingsAccountResponseDto getByOwnerId(@PathVariable long accountOwnerId) {
        log.info("Received request to get savings account, that belong to owner with id: {}", accountOwnerId);
        return savingsAccountService.getByOwnerId(accountOwnerId);
    }
}
