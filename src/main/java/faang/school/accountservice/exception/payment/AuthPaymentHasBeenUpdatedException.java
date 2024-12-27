package faang.school.accountservice.exception.payment;

import lombok.Getter;

@Getter
public class AuthPaymentHasBeenUpdatedException extends RuntimeException {
    public AuthPaymentHasBeenUpdatedException(String message) {
        super(message);
    }
}