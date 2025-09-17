package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    private final SelenideElement userNameInput = $(Selectors.byAttribute("placeholder", "Username"));
    private final SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));
    private final SelenideElement loginButton = $("button");

    @Override
    public String url() {
        return "/login";
    }

    public UserDashboardPage login(String userName, String password) {
        userNameInput.setValue(userName);
        passwordInput.setValue(password);
        loginButton.click();
        return new UserDashboardPage();
    }
}
