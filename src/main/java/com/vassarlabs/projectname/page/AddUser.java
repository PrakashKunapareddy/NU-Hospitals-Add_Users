package com.vassarlabs.projectname.page;

import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.io.FileInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddUser {
    WebDriver driver;

    private By userName = By.xpath("//input[@placeholder='Username']");
    private By passWord = By.xpath("//input[@placeholder='Password']");
    private By signInButton = By.xpath("//span[text()=' Sign In ']/..");
    private By hamburgerPath = By.xpath("//button[contains(@class,'example-icon')]");
    private By userManagementPath = By.xpath("//mat-panel-title[text()=' User Management ']");
    private By usersModulePath = By.xpath("//span[text()='Users']");
    private By addUser_button = By.xpath("//span[contains(text(),'Add')]/parent::button");
    private By firstNameField = By.xpath("//input[@formcontrolname='firstName']");
    private By lastNameField = By.xpath("//input[@formcontrolname='lastName']");
    private By userNameField = By.xpath("//input[@formcontrolname='username']");
    private By emailField = By.xpath("//input[@formcontrolname='email']");
    private By phoneNumberField = By.xpath("//input[@type='tel']");
    private By submitButtonAddUser = By.xpath("//span[text()='Add']/..");
    private By roleDropDownField = By.xpath("//span[@class='ng-arrow-wrapper']");
    private By branchDropDown = By.xpath("//mat-label[text()='Branch']/parent::label/parent::div/parent::div/following::div/mat-select/div/div/following-sibling::div/../div[contains(@class,'mat-mdc-select-arrow-wrapper')]");
    private By DepartmentDropDown = By.xpath("//mat-label[text()='Department']/parent::label/parent::div/parent::div/following::div/mat-select/div/div/following-sibling::div/../div[contains(@class,'mat-mdc-select-arrow-wrapper')]");
    private By SubDepartmentDropDown = By.xpath("//mat-label[text()='Sub Departments']/parent::label/parent::div/parent::div/following::div/mat-select/div/div/following-sibling::div/../div[contains(@class,'mat-mdc-select-arrow-wrapper')]");
    private By ReportingToDropDown = By.xpath("//mat-label[text()='Reporting To']/parent::label/parent::div/parent::div/following::div/mat-select/div/div/following-sibling::div/../div[contains(@class,'mat-mdc-select-arrow-wrapper')]");


    public AddUser(WebDriver driver) {
        this.driver = driver;
    }

    public void enterUsername(String username) throws InterruptedException {
        Thread.sleep(1000);
        WebElement un = driver.findElement(userName);
        un.sendKeys(username);
    }

    public void enterPassword(String password) throws InterruptedException {
        Thread.sleep(1000);
        WebElement pw = driver.findElement(passWord);
        pw.sendKeys(password);
    }

    public void clickSignInButton() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        Thread.sleep(3000);
        driver.findElement(signInButton).click();
    }

    public void clickOnHamburgerIcon() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.findElement(hamburgerPath).click();
    }

    public void clickOnUserMGMTPanel() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        int size = driver.findElements(userManagementPath).size();
        if (size > 0) {
            Thread.sleep(2000);
            driver.findElement(userManagementPath).click();
        }
    }

    public void clickOnUsersPanel() throws Throwable {
        Thread.sleep(2000);
        driver.findElement(usersModulePath).click();
    }

    public void clickOnAddUserButton() throws Throwable {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.findElement(addUser_button).isDisplayed();
        Thread.sleep(4000);
        driver.findElement(addUser_button).click();

    }

    public List<String[]> readExcelData(String filePath, String sheetName) throws Throwable {
        ArrayList<String[]> data = new ArrayList<>();
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = WorkbookFactory.create(fis);

        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            System.out.println("Sheet with name " + sheetName + " does not exist.");
            return data;
        }

        boolean isFirstRow = true;

        for (Row row : sheet) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            List<String> rowData = new ArrayList<>();
            for (Cell cell : row) {
                String cellValue = "";

                switch (cell.getCellType()) {
                    case STRING:
                        cellValue = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        cellValue = String.valueOf(cell.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        cellValue = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        cellValue = cell.getCellFormula();  // Formula expression
                        break;
                    case BLANK:
                    case _NONE:
                    default:
                        cellValue = "";
                        break;
                }

                rowData.add(cellValue);
            }

            data.add(rowData.toArray(new String[0]));
        }

        workbook.close();
        fis.close();
        return data;
    }

    public void writeDataToUsers(String filePath, String sheetName) throws Throwable {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        List<String[]> formData = readExcelData(filePath, sheetName);
        String firstName = "";
        String lastName = "";
        for (String[] row : formData) {
            clickOnAddUserButton();
            String displayName = row[1].trim();
            String[] nameParts = displayName.split(" ");
            if (displayName.split(" ").length > 1) {
                firstName = displayName.split(" ")[0];
                lastName = String.join("_", Arrays.copyOfRange(nameParts, 1, nameParts.length));
            } else {
                firstName = displayName;
                lastName = "";
            }
            System.out.println(row);
            driver.findElement(firstNameField).sendKeys(firstName);
            driver.findElement(lastNameField).sendKeys(lastName);
            String usernameDisplayName = firstName.toLowerCase() + "_" + lastName.toLowerCase();
            driver.findElement(userNameField).sendKeys(usernameDisplayName);
            driver.findElement(emailField).sendKeys(row[2]);
            driver.findElement(phoneNumberField).sendKeys(row[3].split("-")[1]);
            driver.findElement(branchDropDown).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-option/span[text()=' "+row[4].trim()+" ']")));
            driver.findElement(By.xpath("//mat-option/span[text()=' "+row[4].trim()+" ']")).click();
            driver.findElement(DepartmentDropDown).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-option/span[text()=' Nursing ']")));
            driver.findElement(By.xpath("//mat-option/span[text()=' Nursing ']")).click();
            driver.findElement(SubDepartmentDropDown).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-option/span[text()=' "+row[5].trim()+" ']")));
            driver.findElement(By.xpath("//mat-option/span[text()=' "+row[5].trim()+" ']")).click();
            driver.findElement(ReportingToDropDown).click();
            driver.findElement(By.xpath("//mat-option/span[text()=' "+row[7].trim()+" ']")).click();
            wait.until(ExpectedConditions.elementToBeClickable(submitButtonAddUser));
            driver.findElement(submitButtonAddUser).click();
            Thread.sleep(4000);
            driver.findElement(roleDropDownField).click();
            Thread.sleep(3000);
            driver.findElement(By.xpath("//div[@class='ng-option-disabled ng-optgroup ng-star-inserted'][@role='group']/following-sibling::div[contains(text(),'" + row[6].trim() + "')]")).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Add Role']/..")));
            driver.findElement(By.xpath("//span[text()='Add Role']/..")).click();
            driver.findElement(By.xpath("//span[text()='Continue']/..")).click();
            Thread.sleep(1000);
        }
    }

}
