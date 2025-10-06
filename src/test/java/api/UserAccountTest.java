package api;

import api.models.AccountResponse;
import api.models.CreateUserRequest;
import api.models.DepositAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

import static api.requests.skelethon.steps.UserSteps.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static api.requests.skelethon.steps.AdminSteps.createUser;

public class UserAccountTest extends BaseTest {
    private CreateUserRequest user;
    private AccountResponse account;

    @BeforeEach
    public void generateTestData() {
        user = createUser();
        account = createAccount(user);
    }

    @Test
    public void userGetAccountsInfoTest() {
        List<AccountResponse> accountsInfo = getAccountsInfo(user);
        accountsInfo.stream().filter(a -> a.equals(account)).findFirst().orElseThrow();
    }

    @Test
    public void userDepositToAccountTest() {
        var depositInfo = new DepositAccountRequest(account.getId(), 0.01);
        depositToAccount(user, depositInfo);
        AccountResponse accountResponse = getAccountsInfo(user).stream().filter(a -> a.getId() == account.getId()).findFirst().orElseThrow();

        assertEquals(depositInfo.getBalance(), accountResponse.getBalance(), "User account(id=%s) balance not equals".formatted(accountResponse.getId()));
    }

    @ParameterizedTest(name = "balance={0}")
    @ValueSource(doubles = {0, -1})
    public void userDepositToAccountInvalidAmountTest(double balance) {
        var depositInfo = new DepositAccountRequest(account.getId(), balance);
        new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnBadRequest(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(depositInfo);
    }

    @Test
    public void userDepositToNotExistAccountTest() {
        var depositInfo = new DepositAccountRequest(999999, 300);
        new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenRequest(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(depositInfo);
    }

    @Test
    public void userDepositToAnotherUserAccountTest() {
        var anotherUser = createUser();
        var anotherAccount = createAccount(anotherUser);
        var depositInfo = new DepositAccountRequest(anotherAccount.getId(), 300);
        new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenRequest(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(depositInfo);
    }


    @Test
    @Disabled
    public void unAuthUserDepositToAccountTest() {
        var depositInfo = new DepositAccountRequest(account.getId(), 300);
        new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnUnAuthRequest(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(depositInfo);
    }

    @Test
    @Disabled
    public void unAuthUserGetAccountsInfoTest() {
        new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnUnAuthRequest(), Endpoint.USER_ACCOUNTS)
                .getList();
    }
}
