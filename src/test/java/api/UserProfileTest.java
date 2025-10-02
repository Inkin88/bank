package api;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.ProfileRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static api.requests.skelethon.steps.AdminSteps.createUser;
import static api.requests.skelethon.steps.UserSteps.getUserProfile;
import static api.requests.skelethon.steps.UserSteps.updateUserProfile;

public class UserProfileTest extends BaseTest {

    @Test
    public void changeUserNameTest() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        CreateUserRequest userRequest2 = RandomModelGenerator.generate(CreateUserRequest.class);
        createUser(userRequest);

        updateUserProfile(userRequest, new ProfileRequest(userRequest2.getUsername()));

        assertEquals(userRequest2.getUsername(), getUserProfile(userRequest).getName(), "Name in profile not equals");
    }

    @Test
    @Disabled
    public void unAuthUserChangeNameTest() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        CreateUserRequest userRequest2 = RandomModelGenerator.generate(CreateUserRequest.class);
        createUser(userRequest);

        new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnUnAuthRequest(), Endpoint.UPDATE_USER_PROFILE)
                .update(new ProfileRequest(userRequest2.getUsername()));
    }
}
