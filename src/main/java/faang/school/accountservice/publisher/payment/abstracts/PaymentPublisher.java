package faang.school.accountservice.publisher.payment.abstracts;

public interface PaymentPublisher<T> {
    Class<T> getInstance();

    <I> void makeResponse(I input);

    <R, E extends Exception> void makeErrorResponse(R request, E exception);

    String getTopicName();

    void publish();
}