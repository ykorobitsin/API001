package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 29.06.13
 * Time: 12:12
 */
public class ApplicationContext {

    private static Properties appConfig;

    public static Properties getAppConfig() throws IOException {
        if (appConfig == null) {
            appConfig = new Properties();
            appConfig.load(new FileInputStream("src\\application.properties"));
        }
        return appConfig;
    }

}
