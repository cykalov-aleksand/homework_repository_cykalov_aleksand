import lombok.extern.slf4j.Slf4j;
import org.example.CashMoneyATM;
import org.example.CashMoneyPerson;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class CashMoneyTest {
    @Test
    public void acceptDeposit_ShouldIncreaseATMBalance() {
        logger.info("Тест на проверку приёма денег банкоматом");

        CashMoneyATM atm = new CashMoneyATM(2, 0, 0, 0, 10, 0, 0, 0, 0, 0);
        int initialBalance = atm.amountOfCash();

        CashMoneyPerson person = new CashMoneyPerson(2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
        int depositAmount = person.amountOfCash();

        int deposited = atm.acceptDeposit(person);

        assertThat(deposited).isEqualTo(depositAmount);
        assertThat(atm.amountOfCash()).isEqualTo(initialBalance + depositAmount);
    }

    @Test
    public void amountOfCash_ShouldCalculateTotalCorrectly() {
        logger.info("Тест на проверку вывода корректной суммы по количеству банкнот в ATM");
        CashMoneyATM cash = new CashMoneyATM(1, 0, 1, 1, 1, 1, 1, 1, 1, 1);
        int total = 5000 + 1000 + 500 + 100 + 50 + 10 + 5 + 2 + 1;

        assertThat(cash.amountOfCash()).isEqualTo(total);
    }

    @Test
    public void withdrawAndUpdateState_ShouldDeductWithdrawnAmount() {
        logger.info("Тест на проверку выдачи денег и обновления состояния");
        CashMoneyATM cash = new CashMoneyATM(2, 0, 0, 0, 10, 0, 0, 0, 0, 0); // 2 по 5000 и 5 по 100 = 10500
        int initialAmount = cash.amountOfCash();
        Map<String, Integer> issued = cash.withdrawAndUpdateAfterIssue(6000);

        assertThat(issued).containsEntry("5000", 1).containsEntry("100", 10);
        assertThat(cash.amountOfCash()).isEqualTo(initialAmount - 6000);
    }

    @Test
    public void throwingExceptionIfValueNegative() {
        logger.info("Тест на выброс исключения при вводе отрицательного значения.");
        CashMoneyATM cash = new CashMoneyATM(2, 0, 0, 0, 10, 0, 0, 0, 0, 0); // 2 по 5000 и 5 по 100 = 10500
        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                cash.withdrawAndUpdateAfterIssue(-6000));
        assertEquals("Сумма не может быть отрицательной", exception.getMessage());
    }

    @Test
    public void issuingExceptionForSmallAmount() {
        logger.info("Тест на проверку недостаточной суммы денег в банкомате");
        CashMoneyATM cash = new CashMoneyATM(2, 0, 0, 0, 10, 0, 0, 0, 0, 0);

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                cash.withdrawAndUpdateAfterIssue(11001));
        assertEquals("В банкомате недостаточно денег", exception.getMessage());
    }

    @Test
    public void issuingExceptionIfRequiredAmountMissing() {
        logger.info("Тест на проверку отсутствия нужной суммы в банкомате");
        CashMoneyATM cash = new CashMoneyATM(2, 0, 0, 0, 10, 0, 0, 0, 0, 0);

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                cash.withdrawAndUpdateAfterIssue(7000));
        assertEquals("Невозможно выдать точную сумму имеющимися купюрами", exception.getMessage());
    }
}
