package requests.skelethon.steps;

import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.CreateUserResponse;
import requests.skelethon.Endpoint;
import requests.skelethon.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AdminSteps {
    public static CreateUserRequest createUser() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreatedSpec(), Endpoint.ADMIN_USER)
                .post(userRequest);
        return userRequest;
    }

    public static CreateUserResponse createUser(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreatedSpec(), Endpoint.ADMIN_USER)
                .post(userRequest);
    }
}
