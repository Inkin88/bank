import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import requests.skelethon.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static requests.skelethon.steps.AdminSteps.createUser;
import static requests.skelethon.steps.UserSteps.*;

public class UserTransactionTest {

    private CreateUserRequest user;
    private AccountResponse account;

    @BeforeEach
    public void generateTestData() {
        user = createUser();
        account = createAccount(user);
    }

    @Test
    public void userGetAccessTransactionsInfoTest() {
        double amount = 233.0;
        depositToAccount(user, new DepositAccountRequest(account.getId(), amount));
        var transList = UserSteps.getTransactionHistory(user, account.getId());
        var transaction = transList.stream().filter(t -> t.getType().equals(Type.DEPOSIT)).findFirst().orElseThrow();

        assertAll(
                () -> assertEquals(amount, transaction.getAmount(), "Expected amount not equals actual"),
                () -> assertEquals(account.getId(), transaction.getRelatedAccountId(), "Expected account id not equals actual")
        );
    }

    @Test
    public void unAuthUserGetAccessTransactionsInfoTest() {
        double amount = 233.0;
        depositToAccount(user, new DepositAccountRequest(account.getId(), amount));
        var errorMessage = new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnUnAuthRequest(), Endpoint.ACCOUNTS_TRANSACTIONS)
                .get(account.getId())
                .extract()
                .body()
                .asString();

        assertEquals("Unauthorized access to account", errorMessage);
    }

    @Test
    public void userGetTransactionsInfoOnlyHisAccountTest() {
        var anotherUser = createUser();
        var anotherUserAccount = createAccount(anotherUser);
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), anotherUserAccount.getId(), 20);
        transferToAccount(user, transferRequest);

        var expectedTransactionsList = getAccountInfo(user, account.getId()).getTransactions();
        var actualTransactionsList = UserSteps.getTransactionHistory(user, account.getId());

        assertTrue(actualTransactionsList.containsAll(expectedTransactionsList) && expectedTransactionsList.containsAll(actualTransactionsList), "Transactions lists not equals");
    }

    @Test
    public void userGetTransactionsInfoAnotherUserAccountTest() {
        var anotherUser = createUser();
        var anotherUserAccount = createAccount(anotherUser);
        depositToAccount(anotherUser, new DepositAccountRequest(anotherUserAccount.getId(), 100));

        var errorMessage = new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenRequest(), Endpoint.ACCOUNTS_TRANSACTIONS)
                .get(anotherUserAccount.getId())
                .extract()
                .body()
                .asString();
        assertEquals("You do not have permission to access this account", errorMessage, "Error messages not equals");
    }

    @Test
    public void userGetTransactionsInfoNotExistAccountTest() {
        var errorMessage = new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenRequest(), Endpoint.ACCOUNTS_TRANSACTIONS)
                .get(999)
                .extract()
                .body()
                .asString();

        assertEquals("You do not have permission to access this account", errorMessage, "Error messages not equals");
    }

    @Test
    public void checkUserTransactionsAttributesTest() {
        double amount1 = 100;
        double amount2 = 20;
        double amount3 = 1.0;
        var anotherUser = createUser();
        var anotherUserAccount = createAccount(anotherUser);
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), anotherUserAccount.getId(), 20);
        var transferRequest2 = new TransferRequest(anotherUserAccount.getId(), account.getId(), 1.0);
        transferToAccount(user, transferRequest);
        transferToAccount(anotherUser, transferRequest2);

        var actualTransactionsList = UserSteps.getTransactionHistory(user, account.getId());
        assertEquals(3, actualTransactionsList.size(), "Transaction list size not equals");
        var depositTransaction = actualTransactionsList.stream().filter(t -> t.getType().equals(Type.DEPOSIT)).findFirst().orElseThrow();
        var transferInTransaction = actualTransactionsList.stream().filter(t -> t.getType().equals(Type.TRANSFER_IN)).findFirst().orElseThrow();
        var transferOutTransaction = actualTransactionsList.stream().filter(t -> t.getType().equals(Type.TRANSFER_OUT)).findFirst().orElseThrow();

        checkTransactions(amount1, account.getId(), depositTransaction);
        checkTransactions(amount3, anotherUserAccount.getId(), transferInTransaction);
        checkTransactions(amount2, anotherUserAccount.getId(), transferOutTransaction);
    }

    private void checkTransactions(double amount, long relatedAccountId, TransactionResponse trans) {
        assertAll(
                () -> assertDoesNotThrow(() -> ZonedDateTime.parse(trans.getTimestamp(), DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH))),
                () -> assertEquals(amount, trans.getAmount()),
                () -> assertEquals(relatedAccountId, trans.getRelatedAccountId())
        );
    }
}
