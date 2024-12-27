package faang.school.accountservice.model.account_number;

import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class FreeAccountId implements Serializable {

    @Column(name = "account_number", nullable = false)
    private BigInteger accountNumber;

    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(value = EnumType.STRING)
    private AccountType type;

    @Column(name = "currency", nullable = false, length = 16)
    @Enumerated(value = EnumType.STRING)
    private Currency currency;
}
