package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResponseAccountDto {

    private String number;
    private OwnerType ownerType;
    private long ownerId;
    private AccountType accountType;
    private Currency currency;
    private AccountStatus status;
    private LocalDateTime updatedAt;

}
