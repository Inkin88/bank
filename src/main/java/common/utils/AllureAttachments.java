package common.utils;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

public class AllureAttachments {
    @Attachment(value = "Screenshot", type = "image/png")
    public static void screenshot(String name) {
        try {
            byte[] screenshotBytes = Selenide.screenshot(OutputType.BYTES);
            Allure.addAttachment(name, new ByteArrayInputStream(screenshotBytes));
        } catch (Exception e) {
            System.err.println("Не удалось сделать скриншот: " + e.getMessage());
        }
    }
}
