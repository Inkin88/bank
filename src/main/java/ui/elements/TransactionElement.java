package ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class TransactionElement extends BaseElement {

    private String type;
    private String balance;

    public TransactionElement(SelenideElement element) {
        super(element);
        var array = element.getText().split("\n");
        var array2 = array[0].split(" - ");
        type = array2[0];
        balance = array2[1].substring(1);
    }
}
