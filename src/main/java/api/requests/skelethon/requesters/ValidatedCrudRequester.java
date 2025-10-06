package api.requests.skelethon.requesters;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.restassured.common.mapper.TypeRef;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.Getter;
import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.CrudEndPointInterface;
import api.requests.skelethon.interfaces.ListEndPointInterface;

import java.util.List;

@Getter
public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndPointInterface, ListEndPointInterface {

    private CrudRequester crudRequester;

    public ValidatedCrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
        this.crudRequester = new CrudRequester(requestSpecification, responseSpecification, endpoint);
    }

    @Override
    public T post(BaseModel model) {
        return (T) crudRequester.post(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T get(long id) {
        return (T) crudRequester.get(id).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T get() {
        return (T) crudRequester.get().extract().as(endpoint.getResponseModel());
    }

    @Override
    public T update(BaseModel model) {
        return (T) crudRequester.update(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public Object delete(long id) {
        return null;
    }

    @Override
    public List<T> getList() {
        return crudRequester.getList().extract().as(new TypeRef<>() {
            @Override
            public java.lang.reflect.Type getType() {
                return TypeFactory.defaultInstance()
                        .constructCollectionType(List.class, endpoint.getResponseModel());
            }
        });
    }

    @Override
    public List<T> getList(long id) {
        return crudRequester.getList(id).extract().as(new TypeRef<>() {
            @Override
            public java.lang.reflect.Type getType() {
                return TypeFactory.defaultInstance()
                        .constructCollectionType(List.class, endpoint.getResponseModel());
            }
        });
    }
}
