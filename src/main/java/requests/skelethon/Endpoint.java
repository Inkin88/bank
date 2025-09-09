package requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;

@AllArgsConstructor
@Getter
public enum Endpoint {
    ADMIN_USER("/admin/users", CreateUserRequest.class, CreateUserResponse.class),
    LOGIN("auth/login", LoginUserRequest.class, LoginUserResponse.class),
    ACCOUNTS("accounts", AccountRequest.class, AccountResponse.class),
    ACCOUNTS_DEPOSIT("accounts/deposit", DepositAccountRequest.class, AccountResponse.class),
    ACCOUNTS_TRANSFER("accounts/transfer", TransferRequest.class, TransferResponse.class),
    ACCOUNTS_TRANSACTIONS("accounts/%s/transactions", TransactionResponse.class, TransactionResponse.class),
    USER_PROFILE("customer/profile", ProfileResponse.class, CreateUserResponse.class),
    UPDATE_USER_PROFILE("customer/profile", ProfileResponse.class, ProfileResponse.class),
    USER_ACCOUNTS("customer/accounts", AccountRequest.class, AccountResponse.class);

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
