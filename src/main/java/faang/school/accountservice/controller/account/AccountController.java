package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.RequestAccountDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.Currency;
import faang.school.accountservice.service.free_account.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/accounts")
@RestController
@RequiredArgsConstructor
public class AccountController {
    private final FreeAccountNumbersService service;

    @GetMapping()
    public ResponseAccountDto getNewAccount(@RequestBody RequestAccountDto requestAccountDto) {
        return service.getNewAccount(requestAccountDto);
    }
}
