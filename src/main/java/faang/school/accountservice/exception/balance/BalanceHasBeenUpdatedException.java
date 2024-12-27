package faang.school.accountservice.exception.balance;

import lombok.Getter;

@Getter
public class BalanceHasBeenUpdatedException extends RuntimeException {
    public BalanceHasBeenUpdatedException(String message) {
        super(message);
    }
}
