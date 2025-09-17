package ui.pages;

import api.models.CreateUserRequest;
import api.specs.RequestSpecs;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Alert;
import ui.elements.BaseElement;

import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BasePage<T extends BasePage> {

    protected final SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    protected final SelenideElement profile = $(Selectors.byClassName("profile-header"));
    protected final SelenideElement userName = $(Selectors.byClassName("user-name"));

    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public T checkAlertAndAccept(String message) {
        Alert alert = Selenide.switchTo().alert();
        assertTrue(alert.getText().contains(message), "Actual alert message '%s' does not contains expected alert message '%s'".formatted(alert.getText(), message));
        alert.accept();
        return (T) this;
    }

    public EditProfilePage editProfile() {
        profile.click();
        return new EditProfilePage();
    }

    public String getUserName() {
        return userName.getText();
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/");
        String token = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", token);
    }

    public static void authAsUser(CreateUserRequest user) {
        Selenide.open("/");
        String token = RequestSpecs.getUserAuthHeader(user.getUsername(), user.getPassword());
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", token);
    }

    protected <T extends BaseElement> List<T> generatePageElements(ElementsCollection elementsCollection, Function<SelenideElement, T> constructor) {
        return elementsCollection.asFixedIterable().stream().map(constructor).toList();
    }
}
