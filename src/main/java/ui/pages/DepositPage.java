package ui.pages;

import com.codeborne.selenide.*;
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
        accountSelect.selectByContainingText(accountNumber);
        amountInput.setValue(amount);
        depositButton.click();
        checkAlertAndAccept("✅ Successfully deposited $%s to account %s!".formatted(amount, accountNumber));
        return new UserDashboardPage();
    }

    public DepositPage makeIncorrectDeposit(String accountNumber, String amount, String message) {
        if (!accountNumber.isEmpty()) {
            accountSelect.selectByContainingText(accountNumber);
        }
        if (!accountNumber.isEmpty()) {
            amountInput.setValue(amount);
        }
        depositButton.click();
        checkAlertAndAccept(message);
        return this;
    }

    public DepositPage checkAccountBalance(String expectedBalance, String accountNumber) {
        Selenide.refresh();
        assertEquals(expectedBalance, getAccount(accountNumber).getBalance());
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
