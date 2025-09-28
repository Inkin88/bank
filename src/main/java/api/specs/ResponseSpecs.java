package api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.*;

public class ResponseSpecs {

    public static final String INVALID_ACCOUNT_OR_AMOUNT = "Invalid account or amount";
    public static final String UNAUTHORIZED_ACCESS_TO_ACCOUNT = "Unauthorized access to account";
    public static final String UNAUTHORIZED = "Not authorized";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";
    public static final String HAVE_NOT_PERMISSION_TO_ACCESS_ACCOUNT = "You do not have permission to access this account";
    public static final String INVALID_TRANSFER = "Invalid transfer: insufficient funds or invalid accounts";

    private ResponseSpecs() {
    }

    private static ResponseSpecBuilder defaultResponseSpec() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification entityWasCreatedSpec() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification requestReturnOk() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification requestReturnUnAuthRequest() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_UNAUTHORIZED)
                .expectBody(containsString(UNAUTHORIZED))
                .build();
    }

    public static ResponseSpecification requestReturnBadCredentialsRequest() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_UNAUTHORIZED)
                .expectBody("error", equalTo(INVALID_USERNAME_OR_PASSWORD))
                .build();
    }

    public static ResponseSpecification requestReturnBadRequest() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(equalTo(INVALID_ACCOUNT_OR_AMOUNT))
                .build();
    }

    public static ResponseSpecification requestReturnInvalidTransferRequest() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(equalTo(INVALID_TRANSFER))
                .build();
    }

    public static ResponseSpecification requestReturnForbiddenRequest() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(equalTo(UNAUTHORIZED_ACCESS_TO_ACCOUNT))
                .build();
    }

    public static ResponseSpecification requestReturnForbiddenAccessRequest() {
        return defaultResponseSpec()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(equalTo(HAVE_NOT_PERMISSION_TO_ACCESS_ACCOUNT))
                .build();
    }
}
