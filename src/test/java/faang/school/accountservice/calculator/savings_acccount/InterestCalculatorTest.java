package faang.school.accountservice.calculator.savings_acccount;

import faang.school.accountservice.model.interest_rate.InterestRate;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import faang.school.accountservice.model.tariff.Tariff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InterestCalculatorTest {

    private InterestCalculator interestCalculator;

    @BeforeEach
    void setUp() {
        interestCalculator = new InterestCalculator();
    }

    @Test
    void calculate_ShouldUpdateSavingsAccountBalancesCorrectly() {
        SavingsAccount account1 = new SavingsAccount();
        account1.setId(1L);
        account1.setBalance(BigDecimal.valueOf(1000));
        Tariff tariff1 = new Tariff();
        InterestRate rate1 = new InterestRate();
        rate1.setInterestRate(BigDecimal.valueOf(5));
        tariff1.setInterestRate(rate1);
        account1.setTariff(tariff1);

        SavingsAccount account2 = new SavingsAccount();
        account2.setId(2L);
        account2.setBalance(BigDecimal.valueOf(2000));
        Tariff tariff2 = new Tariff();
        InterestRate rate2 = new InterestRate();
        rate2.setInterestRate(BigDecimal.valueOf(10));
        tariff2.setInterestRate(rate2);
        account2.setTariff(tariff2);

        List<SavingsAccount> savingsAccounts = List.of(account1, account2);

        interestCalculator.calculate(savingsAccounts);

        assertEquals(BigDecimal.valueOf(50.0).setScale(2), account1.getBalance());
        assertEquals(BigDecimal.valueOf(200.0).setScale(2), account2.getBalance());
    }

    @Test
    void calculate_ShouldHandleZeroInterestRate() {
        SavingsAccount account = new SavingsAccount();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(1000));
        Tariff tariff = new Tariff();
        InterestRate rate = new InterestRate();
        rate.setInterestRate(BigDecimal.ZERO);
        tariff.setInterestRate(rate);
        account.setTariff(tariff);

        List<SavingsAccount> savingsAccounts = List.of(account);

        interestCalculator.calculate(savingsAccounts);

        assertEquals(BigDecimal.valueOf(0.0).setScale(2), account.getBalance());
    }

    @Test
    void calculate_ShouldHandleNullBalance() {
        SavingsAccount account = new SavingsAccount();
        account.setId(1L);
        account.setBalance(null);
        Tariff tariff = new Tariff();
        InterestRate rate = new InterestRate();
        rate.setInterestRate(BigDecimal.valueOf(5));
        tariff.setInterestRate(rate);
        account.setTariff(tariff);

        List<SavingsAccount> savingsAccounts = List.of(account);

        assertThrows(NullPointerException.class, () -> interestCalculator.calculate(savingsAccounts));
    }

    @Test
    void calculate_ShouldHandleNullTariff() {
        SavingsAccount account = new SavingsAccount();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(1000));
        account.setTariff(null);

        List<SavingsAccount> savingsAccounts = List.of(account);

        assertThrows(NullPointerException.class, () -> interestCalculator.calculate(savingsAccounts));
    }

    @Test
    void calculate_ShouldHandleEmptySavingsAccountsList() {
        List<SavingsAccount> savingsAccounts = new ArrayList<>();

        interestCalculator.calculate(savingsAccounts);

        assertTrue(savingsAccounts.isEmpty());
    }
}