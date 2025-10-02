package ui;

import api.configs.Config;
import com.codeborne.selenide.Configuration;
import common.extension.AdminSessionExtension;
import common.extension.BrowserMatchExtension;
import common.extension.TimingExtension;
import common.extension.UserSessionExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith(AdminSessionExtension.class)
@ExtendWith(UserSessionExtension.class)
@ExtendWith(BrowserMatchExtension.class)
@ExtendWith(TimingExtension.class)
public class BaseUiTest {

    protected static final String NONAME = "Noname";

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browser.size");
        Configuration.headless = false;

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
    }
}
