package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboardPage extends BasePage<UserDashboardPage> {

    private final SelenideElement userDashBoardText = $(Selectors.byText("User Dashboard"));
    private final SelenideElement depositButton = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private final SelenideElement transferButton = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public DepositPage depositMoney() {
        StepLogger.log("Переходим в раздел депозит", () -> {
          //  AllureAttachments.screenshot("Меню дашборда");
            depositButton.click();
        });
        return new DepositPage();
    }

    public TransferPage transferMoney() {
        transferButton.click();
        return new TransferPage();
    }
}
