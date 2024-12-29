package faang.school.accountservice.exception.payment;

import lombok.Getter;

@Getter
public class PaymentHasBeenUpdatedException extends RuntimeException {
    public PaymentHasBeenUpdatedException(String message) {
        super(message);
    }
}