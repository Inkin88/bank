package ui;

import api.models.AccountResponse;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ui.elements.AccountElement;
import ui.pages.BankAlert;
import ui.pages.UserDashboardPage;

import java.util.List;
import java.util.stream.Stream;

import static api.skelethon.steps.UserSteps.createAccount;
import static api.skelethon.steps.UserSteps.getAccountInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDepositTest extends BaseUiTest {

    @ParameterizedTest
    @ValueSource(strings = {"0.01", "4999.99", "5000"})
    @UserSession
    public void userDepositToHisAccountTest(String amount) {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        List<AccountElement> accountsList = new UserDashboardPage().open()
                .depositMoney()
                .makeDeposit(createdAccount.getAccountNumber(), amount)
                .depositMoney()
                .getAllAccounts();
        AccountElement actualAccount = accountsList.stream().filter(a -> a.getAccountNumber().equals(createdAccount.getAccountNumber())).findFirst().orElseThrow();
        assertEquals(amount, actualAccount.getBalance());
        AccountResponse accountResponse = getAccountInfo(SessionStorage.getUser(), createdAccount.getId());
        assertEquals(Double.valueOf(actualAccount.getBalance()), accountResponse.getBalance());
    }

    @ParameterizedTest
    @MethodSource("incorrectDeposit")
    @UserSession
    public void userDepositNotCorrectAmountTest(String amount, String expectedBalance, String message) {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        new UserDashboardPage().open()
                .depositMoney()
                .makeIncorrectDeposit(createdAccount.getAccountNumber(), amount, message)
                .checkAccountBalance(expectedBalance, createdAccount.getAccountNumber());
        assertEquals(Double.parseDouble(expectedBalance), SessionStorage.getSteps().getAccountInfo(createdAccount.getId()).getBalance());
    }

    private static Stream<Arguments> incorrectDeposit() {
        return Stream.of(
                Arguments.of("000000001", "0.00", BankAlert.INVALID_AMOUNT_MESSAGE.getMessage()),
                Arguments.of("5001", "0.00", BankAlert.OVER_MAX_AMOUNT_MESSAGE.getMessage())
        );
    }

    @Test
    @UserSession
    public void userDoesNotFillRequiredFieldsTest() {
        String expectedBalance = "0.00";
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        new UserDashboardPage().open()
                .depositMoney()
                .makeIncorrectDeposit("", "5000", BankAlert.ACCOUNT_NOT_SELECTED_MESSAGE.getMessage())
                .checkAccountBalance(expectedBalance, createdAccount.getAccountNumber())
                .makeIncorrectDeposit(createdAccount.getAccountNumber(), "", BankAlert.INVALID_AMOUNT_MESSAGE.getMessage())
                .checkAccountBalance(expectedBalance, createdAccount.getAccountNumber());
        assertEquals(Double.parseDouble(expectedBalance), SessionStorage.getSteps().getAccountInfo(createdAccount.getId()).getBalance());
    }
}
