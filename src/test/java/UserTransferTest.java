import models.AccountResponse;
import models.CreateUserRequest;
import models.DepositAccountRequest;
import models.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static requests.skelethon.steps.AdminSteps.createUser;
import static requests.skelethon.steps.UserSteps.*;
import static utils.ObjectFieldAssertions.assertFieldsEqual;

public class UserTransferTest {
    private CreateUserRequest user;
    private AccountResponse account;

    @BeforeEach
    public void generateTestData() {
        user = createUser();
        account = createAccount(user);
    }

    @Test
    public void userTransferToAnotherUserAccountTest() {
        double amount = 20;
        var anotherUser = createUser();
        var anotherUserAccount = createAccount(anotherUser);
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), anotherUserAccount.getId(), amount);
        var transferResponse = transferToAccount(user, transferRequest);
        assertFieldsEqual(transferRequest, transferResponse);

        var accInfo = getAccountInfo(anotherUser, anotherUserAccount.getId());

        assertEquals(amount, accInfo.getBalance(), "Expected balance not equals actual");
    }

    @Test
    public void userTransferToAnotherHisAccountTest() {
        double amount = 20;
        var anotherAccount = createAccount(user);
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), anotherAccount.getId(), amount);
        var transferResponse = transferToAccount(user, transferRequest);
        assertFieldsEqual(transferRequest, transferResponse);

        var accInfo = getAccountInfo(user, anotherAccount.getId());

        assertEquals(amount, accInfo.getBalance(), "Expected balance not equals actual");
    }

    @ParameterizedTest(name = "transferred balance ={0}")
    @ValueSource(doubles = {400, 0, -0.1})
    public void userTransferToAnotherHisAccountInvalidAmountTest(double amount) {
        var anotherAccount = createAccount(user);
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), anotherAccount.getId(), amount);
        var errorMessage = new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnBadRequest(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest)
                .extract()
                .body()
                .asString();
        assertEquals("Invalid transfer: insufficient funds or invalid accounts", errorMessage);
    }

    @Test
    public void userTransferToSameAccountTest() {
        double amount = 20;
        double balance = 100;
        depositToAccount(user, new DepositAccountRequest(account.getId(), balance));
        var transferRequest = new TransferRequest(account.getId(), account.getId(), amount);
        var transferResponse = transferToAccount(user, transferRequest);
        assertFieldsEqual(transferRequest, transferResponse);

        var accInfo = getAccountInfo(user, account.getId());

        assertEquals(balance, accInfo.getBalance(), "Expected balance not equals actual");
    }

    @Test
    public void userTransferToNotExistAccountTest() {
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), 99999, 30);
        var errorMessage = new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnBadRequest(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest)
                .extract()
                .body()
                .asString();
        assertEquals("Invalid transfer: insufficient funds or invalid accounts", errorMessage);
    }

    @Test
    public void userTransferFromNotExistAccountTest() {
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(99999, account.getId(), 30);
        var errorMessage = new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenRequest(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest)
                .extract()
                .body()
                .asString();
        assertEquals("Unauthorized access to account", errorMessage);
    }

    @Test
    public void userTransferFromAnotherUserAccountTest() {
        var anotherUser = createUser();
        var anotherUserAccount = createAccount(anotherUser);
        depositToAccount(anotherUser, new DepositAccountRequest(anotherUserAccount.getId(), 100));
        var transferRequest = new TransferRequest(anotherUserAccount.getId(), account.getId(), 30);
        var errorMessage = new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenRequest(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest)
                .extract()
                .body()
                .asString();
        assertEquals("Unauthorized access to account", errorMessage);
    }

    @Test
    public void unAuthUserTransferToAnotherHisAccountTest() {
        double amount = 20;
        var anotherAccount = createAccount(user);
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), anotherAccount.getId(), amount);
        var errorMessage = new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnUnAuthRequest(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest)
                .extract()
                .body()
                .asString();

        assertEquals("Unauthorized access to account", errorMessage);
    }
}
