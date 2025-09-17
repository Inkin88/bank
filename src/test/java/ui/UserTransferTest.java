package ui;

import api.models.AccountResponse;
import api.models.Type;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.elements.TransactionElement;
import ui.model.Transfer;
import ui.pages.BasePage;
import ui.pages.TransferPage;
import ui.pages.UserDashboardPage;

import java.util.List;

import static api.skelethon.steps.UserSteps.createAccount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ui.pages.BankAlert.*;

public class UserTransferTest extends BaseUiTest {

    @Test
    @UserSession(2)
    public void userTransferToAnotherUserTest() {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        AccountResponse createdAccount2 = createAccount(SessionStorage.getUser(2));
        AccountResponse accountResponse = SessionStorage.getSteps().depositToAccount(createdAccount.getId());
        Transfer transferDetails = new Transfer(createdAccount.getAccountNumber(), createdAccount2.getAccountNumber(), Double.toString(accountResponse.getBalance()), true, "");
        List<TransactionElement> transactionsList = new UserDashboardPage().open()
                .transferMoney()
                .makeTransfer(transferDetails)
                .getAllTransfers();
        TransactionElement transactionOut = transactionsList.stream().filter(tr -> tr.getType().equals(Type.TRANSFER_OUT.toString())).findFirst().orElseThrow();
        assertEquals(accountResponse.getBalance(), Double.parseDouble(transactionOut.getBalance()));
        assertEquals(0.00, SessionStorage.getSteps().getAccountInfo(createdAccount.getId()).getBalance());

        BasePage.authAsUser(SessionStorage.getUser(2));
        TransactionElement transactionIn = new TransferPage().open().getAllTransfers().stream().filter(tr -> tr.getType().equals(Type.TRANSFER_IN.toString())).findFirst().orElseThrow();
        assertEquals(accountResponse.getBalance(), Double.parseDouble(transactionIn.getBalance()));
        assertEquals(accountResponse.getBalance(), SessionStorage.getSteps(2).getAccountInfo(createdAccount2.getId()).getBalance());
    }

    @Test
    @UserSession()
    public void userTransferToNotExistAccountTest() {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        AccountResponse accountResponse = SessionStorage.getSteps().depositToAccount(createdAccount.getId());
        new UserDashboardPage().open()
                .transferMoney()
                .makeTransfer(new Transfer(createdAccount.getAccountNumber(), "ACC99", Double.toString(accountResponse.getBalance()), true, NO_USER_FOUND.getMessage()));
    }

    @Test
    @UserSession()
    public void userDoesNotFillRequiredFieldsTest() {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        AccountResponse createdAccount2 = createAccount(SessionStorage.getUser());
        AccountResponse accountResponse = SessionStorage.getSteps().depositToAccount(createdAccount.getId());
        new UserDashboardPage().open()
                .transferMoney()
                .makeTransfer(new Transfer(createdAccount.getAccountNumber(), createdAccount2.getAccountNumber(), Double.toString(accountResponse.getBalance()), false, FILL_ALL_FIELDS.getMessage()));
    }

    @Test
    @UserSession()
    public void shouldNotAllowUserToTransferMoreMoneyThanHeHasTest() {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        AccountResponse createdAccount2 = createAccount(SessionStorage.getUser());
        AccountResponse accountResponse = SessionStorage.getSteps().depositToAccount(createdAccount.getId());
        new UserDashboardPage().open()
                .transferMoney()
                .makeTransfer(new Transfer(createdAccount.getAccountNumber(), createdAccount2.getAccountNumber(), Double.toString(accountResponse.getBalance() + 1.00), true, INVALID_TRANSFER.getMessage()));
    }
}
