package faang.school.accountservice.exception;

public class MaximumAllowedInterestRateException extends RuntimeException {
    public MaximumAllowedInterestRateException(String message) {
        super(message);
    }
}