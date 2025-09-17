package ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class AccountElement extends BaseElement {

    private String accountNumber;
    private String balance;

    public AccountElement(SelenideElement element) {
        super(element);
        var array = element.getText().split(" ");
        accountNumber = array[0];
        balance = array[2].substring(1, array[2].length() - 1);
    }
}
