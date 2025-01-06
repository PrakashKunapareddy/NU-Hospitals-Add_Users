package apphooks;

import com.vassarlabs.projectname.driver.WebdriverInitializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vassarlabs.projectname.utils.ConfigReader;
import com.vassarlabs.projectname.utils.Constants;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

public class ApplicationHooks {

    private WebdriverInitializer driverFactory;
    private WebDriver driver;
    private ConfigReader configReader;
    Properties prop;


    @Before(order = 0)
    public void getProperty() {
        configReader = new ConfigReader();
        prop = configReader.init_prop();
    }

    @Before(order = 1)
    public void launchBrowser() {
        String browserName = prop.getProperty("browser");
        driverFactory = new WebdriverInitializer();
        driver = driverFactory.init_driver(browserName);
        driver.manage().timeouts().implicitlyWait(Duration.ofMinutes(2));
        driver.get(Constants.URL);
    }


    @After(order = 0)
    public void quitBrowser() throws InterruptedException {
//        driver.quit();
    }

    @After(order = 1)
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            // take screenshot:
            String screenshotName = scenario.getName().replaceAll(" ", "_");
            byte[] sourcePath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(sourcePath, "image/png", screenshotName);

        }
    }

//    @After(order = 2)
    public void afterClass() throws IOException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object str = js.executeScript("return window.__coverage__;");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String coverage = gson.toJson(str);

        // Read existing coverage file, if it exists
        String filePath = "";
        String existingCoverage = null;
        try {
            existingCoverage = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (NoSuchFileException e) {
            // File doesn't exist yet, ignore and create new file below
        }

        // Merge coverage with existing coverage, if it exists
        if (existingCoverage != null && !existingCoverage.isEmpty()) {
            coverage = mergeCoverage(coverage, existingCoverage);
        }

        // Write merged coverage to file
        Files.write(Paths.get(filePath), coverage.getBytes());
    }

    private String mergeCoverage(String currentCoverage, String existingCoverage) {
        // Parse coverage strings into JSON objects
        Gson gson = new Gson();
        JsonObject currentJson = gson.fromJson(currentCoverage, JsonObject.class);
        JsonObject existingJson = gson.fromJson(existingCoverage, JsonObject.class);

        // Merge coverage data
        for (Map.Entry<String, JsonElement> entry : currentJson.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (existingJson.has(key)) {
                JsonElement existingValue = existingJson.get(key);
                if (value.isJsonObject()) {
                    mergeCoverage(value.toString(), existingValue.toString());
                } else {
                    existingJson.add(key, value);
                }
            } else {
                existingJson.add(key, value);
            }
        }

        // Convert merged coverage back to string
        return gson.toJson(existingJson);
    }
}

