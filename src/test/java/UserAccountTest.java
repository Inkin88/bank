import models.AccountResponse;
import models.CreateUserRequest;
import models.DepositAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static requests.skelethon.steps.AdminSteps.createUser;
import static requests.skelethon.steps.UserSteps.*;

public class UserAccountTest {
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
        double balance = 0.01;
        var depositInfo = new DepositAccountRequest(account.getId(), balance);
        depositToAccount(user, depositInfo);
        AccountResponse accountResponse = getAccountsInfo(user).stream().filter(a -> a.getId() == account.getId()).findFirst().orElseThrow();

        assertEquals(balance, accountResponse.getBalance(), "User account(id=%s) balance not equals".formatted(accountResponse.getId()));
    }

    @ParameterizedTest(name = "balance={0}")
    @ValueSource(doubles = {0, -1})
    public void userDepositToAccountInvalidAmountTest(double balance) {
        var depositInfo = new DepositAccountRequest(account.getId(), balance);
        var errorMessage = new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnBadRequest(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(depositInfo)
                .extract()
                .body()
                .asString();

        assertEquals("Invalid account or amount", errorMessage);
    }

    @Test
    public void userDepositToNotExistAccountTest() {
        var depositInfo = new DepositAccountRequest(999999, 300);
        var errorMessage = new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenRequest(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(depositInfo)
                .extract()
                .body()
                .asString();

        assertEquals("Unauthorized access to account", errorMessage);
    }

    @Test
    public void userDepositToAnotherUserAccountTest() {
        var anotherUser = createUser();
        var anotherAccount = createAccount(anotherUser);
        var depositInfo = new DepositAccountRequest(anotherAccount.getId(), 300);
        var errorMessage = new CrudRequester(RequestSpecs.authAsUserSpec(user.getUsername(), user.getPassword()), ResponseSpecs.requestReturnForbiddenRequest(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(depositInfo)
                .extract()
                .body()
                .asString();

        assertEquals("Unauthorized access to account", errorMessage, "Error message not equals");
    }

    @Test
    public void unAuthUserDepositToAccountTest() {
        var depositInfo = new DepositAccountRequest(account.getId(), 300);
        var errorMessage = new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnUnAuthRequest(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(depositInfo)
                .extract()
                .body()
                .asString();

        assertEquals("Unauthorized access to account", errorMessage, "Error message not equals");
    }

    @Test
    public void unAuthUserGetAccountsInfoTest() {
        var errorMessage = new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnUnAuthRequest(), Endpoint.USER_ACCOUNTS)
                .getList()
                .extract()
                .jsonPath()
                .get("error");
        assertEquals(errorMessage, "Not authorized", "Error message not equals");
    }
}
