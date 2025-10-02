package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import common.utils.AllureAttachments;
import ui.elements.AccountElement;
import ui.elements.Select;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositPage extends BasePage<DepositPage> {
    private final Select accountSelect = Select.byXpath("//label[text() = 'Select Account:']/../select");
    private final SelenideElement depositButton = $(Selectors.byText("\uD83D\uDCB5 Deposit"));

    @Override
    public String url() {
        return "/deposit";
    }

    public UserDashboardPage makeDeposit(String accountNumber, String amount) {
        StepLogger.log("Делаем депозит на %s на сумму %s".formatted(accountNumber, amount), () -> {
            accountSelect.selectByContainingText(accountNumber);
            amountInput.setValue(amount);
            AllureAttachments.screenshot("Скриншот перед депозитом");
            depositButton.click();
            checkAlertAndAccept("✅ Successfully deposited $%s to account %s!".formatted(amount, accountNumber));
        });
        return new UserDashboardPage();
    }

    public DepositPage makeIncorrectDeposit(String accountNumber, String amount, String message) {

        StepLogger.log("Делаем депозит на %s на сумму %s".formatted(accountNumber, amount), () -> {
            if (!accountNumber.isEmpty()) {
                accountSelect.selectByContainingText(accountNumber);
            }
            if (!accountNumber.isEmpty()) {
                amountInput.setValue(amount);
            }
            AllureAttachments.screenshot("Скриншот перед депозитом");
            depositButton.click();
            checkAlertAndAccept(message);
        });
        return this;
    }

    public DepositPage checkAccountBalance(String expectedBalance, String accountNumber) {
        StepLogger.log("Проверяем баланс(%s) аккаунта %s".formatted(expectedBalance, accountNumber), () -> {
            Selenide.refresh();
            AllureAttachments.screenshot("Скриншот баланса аккаунта");
            assertEquals(expectedBalance, getAccount(accountNumber).getBalance());
        });
        return this;
    }

    public AccountElement getAccount(String accountNumber) {
        List<AccountElement> elements = getAllAccounts();
        return elements.stream().filter(a -> a.getAccountNumber().equals(accountNumber)).findFirst().orElseThrow();
    }

    public List<AccountElement> getAllAccounts() {
        Selenide.sleep(1000);
        ElementsCollection options = accountSelect.getOptions();
        return generatePageElements(options, AccountElement::new);
    }
}
