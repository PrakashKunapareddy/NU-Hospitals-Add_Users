package stepdefinitions;


import com.vassarlabs.projectname.driver.WebdriverInitializer;
import com.vassarlabs.projectname.page.AddUser;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class add_user_sd {
    AddUser add_user = new AddUser(WebdriverInitializer.getDriver());

    @Given("Entered a valid {string} {string}")
    public void entered_a_valid(String username, String password) throws Throwable {
        add_user.enterUsername(username);
        add_user.enterPassword(password);
    }


    @When("Click on the sign in button")
    public void clickOnTheSignInButton() throws Throwable {
        add_user.clickSignInButton();
    }

    @And("Navigate to Users Module")
    public void navigateToUsersModule() throws Throwable {
        add_user.clickOnHamburgerIcon();
        add_user.clickOnUserMGMTPanel();
        add_user.clickOnUsersPanel();
    }


    @Then("ADD USERS {string} {string}")
    public void addUSERSFile_pathSheet_name(String filePath, String sheetName) throws Throwable {
        add_user.writeDataToUsers(filePath,sheetName);
    }
}
