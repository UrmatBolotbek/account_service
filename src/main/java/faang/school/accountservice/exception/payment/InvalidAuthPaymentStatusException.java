package faang.school.accountservice.exception.payment;

public class InvalidAuthPaymentStatusException extends RuntimeException{
    public InvalidAuthPaymentStatusException(String message) {
        super(message);
    }
}
