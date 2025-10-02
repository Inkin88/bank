package api.requests.skelethon.requesters;

import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.CrudEndPointInterface;
import api.requests.skelethon.interfaces.ListEndPointInterface;
import common.helpers.StepLogger;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndPointInterface, ListEndPointInterface {
    public CrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
    }

    @Override
    @Step("POST запрос на {endpoint} с телом {model}")
    public ValidatableResponse post(BaseModel model) {
        return StepLogger.log("POST запрос на %s".formatted(endpoint.getUrl()), () -> {
            var body = model == null ? "" : model;
            return given()
                    .spec(requestSpecification)
                    .body(body)
                    .post(endpoint.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    @Override
    @Step("GET запрос на {endpoint} с id {id}")
    public ValidatableResponse get(long id) {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getUrl().formatted(id))
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    @Step("GET запрос на {endpoint}")
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    @Step("GET запрос на {endpoint}")
    public ValidatableResponse getList() {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    @Step("GET запрос на {endpoint} с id {id}")
    public ValidatableResponse getList(long id) {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getUrl().formatted(id))
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    @Step("PUT запрос на {endpoint} с телом {model}")
    public ValidatableResponse update(BaseModel model) {
        var body = model == null ? "" : model;
        return given()
                .spec(requestSpecification)
                .body(body)
                .put(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    @Step("DELETE запрос на {endpoint} с id {id}")
    public Object delete(long id) {
        return null;
    }
}
