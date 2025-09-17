package ui;

import api.models.AccountResponse;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.elements.AccountElement;
import ui.pages.BankAlert;
import ui.pages.UserDashboardPage;

import java.util.List;

import static api.skelethon.steps.UserSteps.createAccount;
import static api.skelethon.steps.UserSteps.getAccountInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDepositTest extends BaseUiTest {

    @Test
    @UserSession
    public void userDepositToHisAccountTest() {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        List<AccountElement> accountsList = new UserDashboardPage().open()
                .depositMoney()
                .makeDeposit(createdAccount.getAccountNumber(), "100")
                .depositMoney()
                .getAllAccounts();
        AccountElement actualAccount = accountsList.stream().filter(a -> a.getAccountNumber().equals(createdAccount.getAccountNumber())).findFirst().orElseThrow();
        assertEquals("100.00", actualAccount.getBalance());
        AccountResponse accountResponse = getAccountInfo(SessionStorage.getUser(), createdAccount.getId());
        assertEquals(Double.valueOf(actualAccount.getBalance()), accountResponse.getBalance());
    }

    @Test
    @UserSession
    public void userDepositNotCorrectAmountTest() {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        new UserDashboardPage().open()
                .depositMoney()
                .makeIncorrectDeposit(createdAccount.getAccountNumber(), "000000001", BankAlert.INVALID_AMOUNT_MESSAGE.getMessage())
                .checkAccountBalance("0.00", createdAccount.getAccountNumber());
    }

    @Test
    @UserSession
    public void userDepositOverMaxAmountTest() {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        new UserDashboardPage().open()
                .depositMoney()
                .makeIncorrectDeposit(createdAccount.getAccountNumber(), "5001", BankAlert.OVER_MAX_AMOUNT_MESSAGE.getMessage())
                .checkAccountBalance("0.00", createdAccount.getAccountNumber());;
    }

    @Test
    @UserSession
    public void userDoesNotFillRequiredFieldsTest() {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        new UserDashboardPage().open()
                .depositMoney()
                .makeIncorrectDeposit("", "5000", BankAlert.ACCOUNT_NOT_SELECTED_MESSAGE.getMessage())
                .checkAccountBalance("0.00", createdAccount.getAccountNumber())
                .makeIncorrectDeposit(createdAccount.getAccountNumber(), "", BankAlert.INVALID_AMOUNT_MESSAGE.getMessage())
                .checkAccountBalance("0.00", createdAccount.getAccountNumber());
    }
}
