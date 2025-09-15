import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.ProfileRequest;
import org.junit.jupiter.api.Test;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static requests.skelethon.steps.AdminSteps.createUser;
import static requests.skelethon.steps.UserSteps.getUserProfile;
import static requests.skelethon.steps.UserSteps.updateUserProfile;

public class UserProfileTest {

    @Test
    public void changeUserNameTest() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        CreateUserRequest userRequest2 = RandomModelGenerator.generate(CreateUserRequest.class);
        createUser(userRequest);

        updateUserProfile(userRequest, new ProfileRequest(userRequest2.getUsername()));

        assertEquals(userRequest2.getUsername(), getUserProfile(userRequest).getName(), "Name in profile not equals");
    }

    @Test
    public void unAuthUserChangeNameTest() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        CreateUserRequest userRequest2 = RandomModelGenerator.generate(CreateUserRequest.class);
        createUser(userRequest);

        new CrudRequester(RequestSpecs.unAuthSpec(), ResponseSpecs.requestReturnUnAuthRequest(), Endpoint.UPDATE_USER_PROFILE)
                .update(new ProfileRequest(userRequest2.getUsername()));
    }
}
