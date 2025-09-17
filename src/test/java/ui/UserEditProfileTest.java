package ui;

import common.annotation.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.UserDashboardPage;

import static org.junit.jupiter.api.Assertions.*;

public class UserEditProfileTest extends BaseUiTest {

    @Test
    @UserSession()
    public void userChangeNameTest() {
        String expectedName = "@user?ы:; name!";
        String actualName = new UserDashboardPage().open()
                .editProfile()
                .setName(expectedName)
                .getUserName();
        assertAll(
                () -> assertEquals(expectedName, actualName),
                () -> assertEquals(expectedName, SessionStorage.getSteps().getUserProfile().getName()));
    }

    @Test
    @UserSession()
    public void userChangeNameToEmptyTest() {
        String expectedName = "         ";
        String actualName = new UserDashboardPage().open()
                .editProfile()
                .setName(expectedName)
                .getUserName();
        assertAll(
                () -> assertEquals("Noname", actualName),
                () -> assertNull(SessionStorage.getSteps().getUserProfile().getName()));
    }
}

