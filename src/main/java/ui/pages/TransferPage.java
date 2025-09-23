package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import ui.elements.Select;
import ui.elements.TransactionElement;
import ui.model.Transfer;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TransferPage extends BasePage<TransferPage> {

    private final Select accountSelect = Select.byXpath("//label[text() = 'Select Your Account:']/../select");
    private final SelenideElement confirmDetailCheckBox = $(Selectors.byId("confirmCheck"));
    private final SelenideElement recipientAccNumberInput = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private final SelenideElement sendTransferButton = $(Selectors.byText("\uD83D\uDE80 Send Transfer"));
    private final SelenideElement transferAgainButton = $(Selectors.byText("\uD83D\uDD01 Transfer Again"));
    private final ElementsCollection transfersList = $$(Selectors.byXpath("//ul/li"));

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage makeTransfer(Transfer transfer) {
        Selenide.sleep(2000);
        if (!transfer.getFromACC().isEmpty()) {
            accountSelect.selectByContainingText(transfer.getFromACC());
        }
        if (!transfer.getToAcc().isEmpty()) {
            recipientAccNumberInput.setValue(transfer.getToAcc());
        }
        if (!transfer.getAmount().isEmpty()) {
            amountInput.setValue(transfer.getAmount());
        }
        if (transfer.isConfirm()) {
            confirmDetailCheckBox.setSelected(true);
        }
        sendTransferButton.click();
        if (!transfer.getMessage().isEmpty()) {
            checkAlertAndAccept(transfer.getMessage());
        } else {
            checkAlertAndAccept("✅ Successfully transferred $%s to account %s!".formatted(transfer.getAmount(), transfer.getToAcc()));
        }
        Selenide.refresh();
        return this;
    }

    public List<TransactionElement> getAllTransfers() {
        transferAgainButton.click();
        Selenide.sleep(1000);
        return generatePageElements(transfersList, TransactionElement::new);
    }
}
