import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.LoginUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mindrot.jbcrypt.BCrypt;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static requests.skelethon.steps.AdminSteps.createUser;
import static requests.skelethon.steps.UserSteps.login;
import static utils.ObjectFieldAssertions.assertFieldsEqual;

public class UserAuthTest {

    @Test
    public void userLoginTest() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        createUser(userRequest);
        var logRequest = LoginUserRequest.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .build();

        var logResponse = login(logRequest);

        assertFieldsEqual(userRequest, logResponse);
    }

    @Test
    public void userTokenFormatTest() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        var createdUser = createUser(userRequest);

        assertTrue(BCrypt.checkpw(userRequest.getPassword(), createdUser.getPassword()), "Password not equals");
    }

    @ParameterizedTest(name = "username={0}, password={1}")
    @MethodSource("testUsers")
    public void userLoginWithWrongCredTest(String userName, String userPassword) {
        new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnBadCredentialsRequest(), Endpoint.LOGIN)
                .post(LoginUserRequest.builder()
                        .username(userName)
                        .password(userPassword)
                        .build());
    }

    private static Stream<Arguments> testUsers() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        CreateUserRequest userRequest2 = RandomModelGenerator.generate(CreateUserRequest.class);
        createUser(userRequest);
        return Stream.of(Arguments.of("", ""),
                Arguments.of("", userRequest.getPassword()),
                Arguments.of(userRequest.getUsername(), ""),
                Arguments.of(userRequest2.getUsername(), userRequest.getPassword()),
                Arguments.of(userRequest.getUsername(), userRequest2.getPassword())
        );
    }
}
