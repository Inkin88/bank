package requests.skelethon.steps;

import models.*;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

public class UserSteps {

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

    public static AccountResponse depositToAccount(CreateUserRequest userRequest, DepositAccountRequest depositInfo) {
        return new ValidatedCrudRequester<AccountResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.ACCOUNTS_DEPOSIT)
                .post(depositInfo);
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

//    public static ProfileResponse updateUserProfile(CreateUserRequest userRequest, ProfileResponse profileResponse) {
//        return new ValidatedCrudRequester<ProfileResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
//                ResponseSpecs.requestReturnOk(), Endpoint.USER_PROFILE)
//                .update(profileResponse);
//    }

    public static ProfileResponse updateUserProfile(CreateUserRequest userRequest, ProfileRequest profileRequest) {
        return new ValidatedCrudRequester<ProfileResponse>(RequestSpecs.authAsUserSpec(userRequest.getUsername(), userRequest.getPassword()),
                ResponseSpecs.requestReturnOk(), Endpoint.UPDATE_USER_PROFILE)
                .update(profileRequest);
    }
}
