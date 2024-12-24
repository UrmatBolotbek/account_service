package faang.school.accountservice.model.account;

import lombok.Getter;

@Getter
public enum AccountType {
    FL(40817),
    UL(40701),
    FL_DEPOSIT(42301),
    UL_DEPOSIT(42001);

    private final int accountTypeTypeNumber;

    AccountType(int accountTypeTypeNumber) {
        this.accountTypeTypeNumber = accountTypeTypeNumber;
    }
}
