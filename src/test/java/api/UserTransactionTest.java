package api;

import api.models.*;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static api.requests.skelethon.steps.AdminSteps.createUser;
import static api.requests.skelethon.steps.UserSteps.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserTransactionTest extends BaseTest {

    private CreateUserRequest user;
    private AccountResponse account;

    @BeforeEach
    public void generateTestData() {
        user = createUser();
        account = createAccount(user);
    }

    @Test
    public void userGetAccessTransactionsInfoTest() {
        DepositAccountRequest depositInfo = new DepositAccountRequest(account.getId(), 233.0);
        depositToAccount(user, depositInfo);
        var transList = UserSteps.getTransactionHistory(user, account.getId());
        var transaction = transList.stream().filter(t -> t.getType().equals(Type.DEPOSIT)).findFirst().orElseThrow();

        assertAll(
                () -> assertEquals(depositInfo.getBalance(), transaction.getAmount(), "Expected amount not equals actual"),
                () -> assertEquals(account.getId(), transaction.getRelatedAccountId(), "Expected account id not equals actual")
        );
    }

    @Test
    public void unAuthUserGetAccessTransactionsInfoTest() {
        DepositAccountRequest depositInfo = new DepositAccountRequest(account.getId(), 233.0);
        depositToAccount(user, depositInfo);
        new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnUnAuthRequest(), Endpoint.ACCOUNTS_TRANSACTIONS)
                .get(account.getId());
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

        new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenAccessRequest(), Endpoint.ACCOUNTS_TRANSACTIONS)
                .get(anotherUserAccount.getId());
    }

    @ParameterizedTest
    @ValueSource(ints = 999)
    public void userGetTransactionsInfoNotExistAccountTest(int accountId) {
        new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenAccessRequest(), Endpoint.ACCOUNTS_TRANSACTIONS)
                .get(accountId);
    }

    @Test
    public void checkUserTransactionsAttributesTest() {
        var anotherUser = createUser();
        var anotherUserAccount = createAccount(anotherUser);
        DepositAccountRequest depositInfo = new DepositAccountRequest(account.getId(), 100);
        depositToAccount(user, depositInfo);
        var transferRequest = new TransferRequest(account.getId(), anotherUserAccount.getId(), 20);
        var transferRequest2 = new TransferRequest(anotherUserAccount.getId(), account.getId(), 1.0);
        transferToAccount(user, transferRequest);
        transferToAccount(anotherUser, transferRequest2);

        var actualTransactionsList = UserSteps.getTransactionHistory(user, account.getId());
        assertEquals(3, actualTransactionsList.size(), "Transaction list size not equals");
        var depositTransaction = actualTransactionsList.stream().filter(t -> t.getType().equals(Type.DEPOSIT)).findFirst().orElseThrow();
        var transferInTransaction = actualTransactionsList.stream().filter(t -> t.getType().equals(Type.TRANSFER_IN)).findFirst().orElseThrow();
        var transferOutTransaction = actualTransactionsList.stream().filter(t -> t.getType().equals(Type.TRANSFER_OUT)).findFirst().orElseThrow();

        checkTransactions(depositInfo.getBalance(), account.getId(), depositTransaction);
        checkTransactions(transferRequest2.getAmount(), anotherUserAccount.getId(), transferInTransaction);
        checkTransactions(transferRequest.getAmount(), anotherUserAccount.getId(), transferOutTransaction);
    }

    private void checkTransactions(double amount, long relatedAccountId, TransactionResponse trans) {
        assertAll(
                () -> assertDoesNotThrow(() -> ZonedDateTime.parse(trans.getTimestamp(), DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH))),
                () -> assertEquals(amount, trans.getAmount()),
                () -> assertEquals(relatedAccountId, trans.getRelatedAccountId())
        );
    }
}
