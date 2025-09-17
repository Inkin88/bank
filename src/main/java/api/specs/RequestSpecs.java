package api.specs;

import api.configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.models.LoginUserRequest;
import api.skelethon.Endpoint;
import api.skelethon.requesters.CrudRequester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecs {
    private static Map<String, String> authHeaders = new HashMap<>(Map.of("admin", "Basic YWRtaW46YWRtaW4="));

    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("server") + Config.getProperty("apiVersion"));
    }

    public static RequestSpecification unAuthSpec() {
        return defaultRequestSpecBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestSpecBuilder()
                .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                .build();
    }

    public static RequestSpecification authAsUserSpec(String userName, String userPassword) {
        return defaultRequestSpecBuilder().addHeader("Authorization", getUserAuthHeader(userName, userPassword))
                .build();
    }

    public static String getUserAuthHeader(String userName, String password) {
        var userAuthHeader = "";
        if (!authHeaders.containsKey(userName)) {
            userAuthHeader = new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnOk(), Endpoint.LOGIN)
                    .post(LoginUserRequest.builder()
                            .username(userName)
                            .password(password)
                            .build())
                    .extract()
                    .header("Authorization");
            authHeaders.put(userName, userAuthHeader);
        } else {
            userAuthHeader = authHeaders.get(userName);
        }
        return userAuthHeader;
    }
}
