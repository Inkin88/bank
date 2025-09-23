package api;

import api.models.AccountResponse;
import api.models.CreateUserRequest;
import api.models.DepositAccountRequest;
import api.models.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.skelethon.Endpoint;
import api.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import static api.skelethon.steps.UserSteps.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static api.skelethon.steps.AdminSteps.createUser;
import static api.utils.ObjectFieldAssertions.assertFieldsEqual;

public class UserTransferTest extends BaseTest {
    private CreateUserRequest user;
    private AccountResponse account;

    @BeforeEach
    public void generateTestData() {
        user = createUser();
        account = createAccount(user);
    }

    @Test
    public void userTransferToAnotherUserAccountTest() {
        var anotherUser = createUser();
        var anotherUserAccount = createAccount(anotherUser);
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), anotherUserAccount.getId(), 20);
        var transferResponse = transferToAccount(user, transferRequest);
        assertFieldsEqual(transferRequest, transferResponse);

        var accInfo = getAccountInfo(anotherUser, anotherUserAccount.getId());

        assertEquals(transferRequest.getAmount(), accInfo.getBalance(), "Expected balance not equals actual");
    }

    @Test
    public void userTransferToAnotherHisAccountTest() {
        var anotherAccount = createAccount(user);
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), anotherAccount.getId(), 20);
        var transferResponse = transferToAccount(user, transferRequest);
        assertFieldsEqual(transferRequest, transferResponse);

        var accInfo = getAccountInfo(user, anotherAccount.getId());

        assertEquals(transferRequest.getAmount(), accInfo.getBalance(), "Expected balance not equals actual");
    }

    @ParameterizedTest(name = "transferred balance ={0}")
    @ValueSource(doubles = {400, 0, -0.1})
    public void userTransferToAnotherHisAccountInvalidAmountTest(double amount) {
        var anotherAccount = createAccount(user);
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), anotherAccount.getId(), amount);
        new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnInvalidTransferRequest(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest);
    }

    @Test
    public void userTransferToSameAccountTest() {
        DepositAccountRequest depositInfo = new DepositAccountRequest(account.getId(), 100);
        depositToAccount(user, depositInfo);
        var transferRequest = new TransferRequest(account.getId(), account.getId(), 20);
        var transferResponse = transferToAccount(user, transferRequest);
        assertFieldsEqual(transferRequest, transferResponse);

        var accInfo = getAccountInfo(user, account.getId());

        assertEquals(depositInfo.getBalance(), accInfo.getBalance(), "Expected balance not equals actual");
    }

    @Test
    public void userTransferToNotExistAccountTest() {
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), 99999, 30);
        new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnInvalidTransferRequest(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest);
    }

    @Test
    public void userTransferFromNotExistAccountTest() {
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(99999, account.getId(), 30);
        new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenRequest(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest);
    }

    @Test
    public void userTransferFromAnotherUserAccountTest() {
        var anotherUser = createUser();
        var anotherUserAccount = createAccount(anotherUser);
        depositToAccount(anotherUser, new DepositAccountRequest(anotherUserAccount.getId(), 100));
        var transferRequest = new TransferRequest(anotherUserAccount.getId(), account.getId(), 30);
        new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenRequest(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest);
    }

    @Test
    public void unAuthUserTransferToAnotherHisAccountTest() {
        var anotherAccount = createAccount(user);
        depositToAccount(user, new DepositAccountRequest(account.getId(), 100));
        var transferRequest = new TransferRequest(account.getId(), anotherAccount.getId(), 20);
        new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnUnAuthRequest(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest);
    }
}
