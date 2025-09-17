package api.skelethon.steps;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.skelethon.Endpoint;
import api.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

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
