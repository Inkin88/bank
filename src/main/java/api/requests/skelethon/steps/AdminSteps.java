package api.requests.skelethon.steps;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;

public class AdminSteps {

    public static CreateUserRequest createUser() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        return StepLogger.log("Admin create user %s".formatted(userRequest.getUsername()), () -> {
            new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreatedSpec(), Endpoint.ADMIN_USER)
                    .post(userRequest);
            return userRequest;
        });
    }

    public static CreateUserResponse createUser(CreateUserRequest userRequest) {
        return StepLogger.log("Admin create user %s".formatted(userRequest.getUsername()),
                () -> new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreatedSpec(), Endpoint.ADMIN_USER)
                        .post(userRequest));
    }
}
