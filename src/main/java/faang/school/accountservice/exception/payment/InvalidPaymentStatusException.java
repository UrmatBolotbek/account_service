package faang.school.accountservice.exception.payment;

public class InvalidPaymentStatusException extends RuntimeException{
    public InvalidPaymentStatusException(String message) {
        super(message);
    }
}
