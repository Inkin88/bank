package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static ui.pages.BankAlert.ENTER_A_VALID_NAME;
import static ui.pages.BankAlert.NAME_UPDATED;

public class EditProfilePage extends BasePage<EditProfilePage> {

    private SelenideElement nameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement saveChangesButton = $(Selectors.byText("\uD83D\uDCBE Save Changes"));

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage setName(String name) {
        String message = ENTER_A_VALID_NAME.getMessage();
        if (!name.isBlank()) {
            nameInput.setValue(name);
            message = NAME_UPDATED.getMessage();
        }
        saveChangesButton.click();
        checkAlertAndAccept(message);
        Selenide.refresh();
        return this;
    }
}
