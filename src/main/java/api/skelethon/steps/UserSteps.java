package api.skelethon.steps;

import api.models.*;
import api.skelethon.Endpoint;
import api.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserSteps {

    private String userName;
    private String password;

    public static LoginUserResponse login(LoginUserRequest loginCred) {
        return new ValidatedCrudRequester<LoginUserResponse>(RequestSpecs.authAsUserSpec(loginCred.getUsername(), loginCred.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.LOGIN)
                .post(loginCred);
    }

    public static LoginUserResponse login(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<LoginUserResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.LOGIN)
                .post(LoginUserRequest.builder()
                        .username(userRequest.getUsername())
                        .password(userRequest.getPassword())
                        .build());
    }

    public static AccountResponse createAccount(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<AccountResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.entityWasCreatedSpec(), Endpoint.ACCOUNTS)
                .post(null);
    }

    public static List<AccountResponse> getAccountsInfo(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<AccountResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.USER_ACCOUNTS)
                .getList();
    }

    public static AccountResponse getAccountInfo(CreateUserRequest userRequest, long accountId) {
        return new ValidatedCrudRequester<AccountResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.USER_ACCOUNTS)
                .getList().stream().filter(a -> a.getId() == accountId).findFirst().orElseThrow();
    }

    public AccountResponse getAccountInfo(long accountId) {
        return new ValidatedCrudRequester<AccountResponse>(RequestSpecs.authAsUserSpec(userName, password),
                ResponseSpecs.requestReturnOk(), Endpoint.USER_ACCOUNTS)
                .getList().stream().filter(a -> a.getId() == accountId).findFirst().orElseThrow();
    }

    public static AccountResponse depositToAccount(CreateUserRequest userRequest, DepositAccountRequest depositInfo) {
        return new ValidatedCrudRequester<AccountResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(depositInfo);
    }

    public AccountResponse depositToAccount(long id) {
        return new ValidatedCrudRequester<AccountResponse>(RequestSpecs.authAsUserSpec(userName, password),
                ResponseSpecs.requestReturnOk(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(new DepositAccountRequest(id));
    }

    public static TransferResponse transferToAccount(CreateUserRequest userRequest, TransferRequest transferRequest) {
        return new ValidatedCrudRequester<TransferResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.ACCOUNTS_TRANSFER)
                .post(transferRequest);
    }

    public static List<TransactionResponse> getTransactionHistory(CreateUserRequest userRequest, long accountId) {
        return new ValidatedCrudRequester<TransactionResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.ACCOUNTS_TRANSACTIONS)
                .getList(accountId);
    }

    public static CreateUserResponse getUserProfile(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.USER_PROFILE)
                .get();
    }

    public CreateUserResponse getUserProfile() {
        return new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.authAsUserSpec(userName, password),
                ResponseSpecs.requestReturnOk(), Endpoint.USER_PROFILE)
                .get();
    }

    public static ProfileResponse updateUserProfile(CreateUserRequest userRequest, ProfileRequest profileRequest) {
        return new ValidatedCrudRequester<ProfileResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.UPDATE_USER_PROFILE)
                .update(profileRequest);
    }
}
