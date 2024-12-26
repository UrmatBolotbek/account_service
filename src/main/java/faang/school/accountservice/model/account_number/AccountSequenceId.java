package faang.school.accountservice.model.account_number;

import faang.school.accountservice.model.account.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AccountSequenceId implements Serializable {

    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(value = EnumType.STRING)
    private AccountType type;

    @Column(name = "current")
    private long current;

}
