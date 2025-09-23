package ui;

import api.models.AccountResponse;
import api.models.Type;
import common.annotation.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.elements.TransactionElement;
import ui.model.Transfer;
import ui.pages.BasePage;
import ui.pages.TransferPage;
import ui.pages.UserDashboardPage;

import java.util.List;
import java.util.stream.Stream;

import static api.skelethon.steps.UserSteps.createAccount;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ui.pages.BankAlert.*;

public class UserTransferTest extends BaseUiTest {

    @Test
    @UserSession(2)
    public void userTransferToAnotherUserTest() {
        AccountResponse createdAccount = step("Создать аккаунт для первого пользователя", () -> createAccount(SessionStorage.getUser()));
        AccountResponse createdAccount2 = step("Создать аккаунт для второго пользователя", () -> createAccount(SessionStorage.getUser(2)));
        AccountResponse accountResponse = step("Пополнить счёт первого пользователя", () -> SessionStorage.getSteps().depositToAccount(createdAccount.getId()));

        Transfer transferDetails = new Transfer(createdAccount.getAccountNumber(), createdAccount2.getAccountNumber(), Double.toString(accountResponse.getBalance()), true, "");
        List<TransactionElement> transactionsList = step("Совершить перевод денег от первого пользователя ко второму", () -> new UserDashboardPage().open()
                .transferMoney()
                .makeTransfer(transferDetails)
                .getAllTransfers());

        TransactionElement transactionOut = step("Проверить исходящий перевод у первого пользователя", () -> transactionsList
                .stream()
                .filter(tr -> tr.getType().equals(Type.TRANSFER_OUT.toString())).findFirst().orElseThrow());

        step("Проверить, что баланс первого пользователя стал 0", () -> {
            assertEquals(accountResponse.getBalance(), Double.parseDouble(transactionOut.getBalance()));
            assertEquals(0.00, SessionStorage.getSteps().getAccountInfo(createdAccount.getId()).getBalance());
        });

        step("Авторизоваться под вторым пользователем и проверить входящий перевод", () -> {
            BasePage.authAsUser(SessionStorage.getUser(2));
            TransactionElement transactionIn = new TransferPage().open().getAllTransfers().stream().filter(tr -> tr.getType().equals(Type.TRANSFER_IN.toString())).findFirst().orElseThrow();
            assertEquals(accountResponse.getBalance(), Double.parseDouble(transactionIn.getBalance()));
            assertEquals(accountResponse.getBalance(), SessionStorage.getSteps(2).getAccountInfo(createdAccount2.getId()).getBalance());
        });
    }

    @ParameterizedTest
    @UserSession()
    @MethodSource("incorrectTransfer")
    public void userDoesIncorrectTransferTest(String toAcc, boolean isConfirm, String message) {
        AccountResponse createdAccount = createAccount(SessionStorage.getUser());
        if (toAcc == null) {
            toAcc = createAccount(SessionStorage.getUser()).getAccountNumber();
        }
        AccountResponse accountResponse = SessionStorage.getSteps().depositToAccount(createdAccount.getId());
        Transfer transfer = Transfer.builder()
                .fromACC(createdAccount.getAccountNumber())
                .toAcc(toAcc)
                .amount(Double.toString(accountResponse.getBalance()))
                .isConfirm(isConfirm)
                .message(message)
                .build();
        new UserDashboardPage().open()
                .transferMoney()
                .makeTransfer(transfer);
        assertEquals(accountResponse.getBalance(), SessionStorage.getSteps().getAccountInfo(accountResponse.getId()).getBalance());
    }

    private static Stream<Arguments> incorrectTransfer() {
        return Stream.of(
                Arguments.of("ACC20000", true, NO_USER_FOUND.getMessage()),
                Arguments.of(null, false, FILL_ALL_FIELDS.getMessage())
        );
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
        assertEquals(accountResponse.getBalance(), SessionStorage.getSteps().getAccountInfo(createdAccount.getId()).getBalance());
    }
}
