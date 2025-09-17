package ui.elements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$x;

public class Select {

    private SelenideElement element;

    public Select(SelenideElement element) {
        this.element = element;
    }

    public static Select byXpath(String xpath) {
        return new Select($x(xpath));
    }

    public void selectByContainingText(String text) {
        element.$$("option").findBy(com.codeborne.selenide.Condition.text(text)).click();
    }

    public ElementsCollection getOptions() {
        return element.$$("option")
                .filterBy(not(attribute("value", "")))
                .filterBy(not(cssClass("input-field")));
    }
}
