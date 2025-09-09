package generators;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {

    private RandomData() {

    }
    public static String getUserName() {
       return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase() +
                RandomStringUtils.randomAlphabetic(3).toLowerCase() +
                RandomStringUtils.randomAlphanumeric(3) + "$";
    }

}
