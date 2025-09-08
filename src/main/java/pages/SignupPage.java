package pages;

import org.openqa.selenium.By;

public class SignupPage {
    public static final By FIRSTNAME_INPUT = By.name("firstname");
    public static final By LASTNAME_INPUT = By.name("lastname");
    public static final By EMAIL_INPUT = By.name("reg_email__");
    public static final By REEMAIL_INPUT = By.name("reg_email_confirmation__");
    public static final By PASSWORD_INPUT = By.name("reg_passwd__");
    public static final By DOB_DAY = By.id("day");
    public static final By DOB_MONTH = By.id("month");
    public static final By DOB_YEAR = By.id("year");
    public static final By GENDER_MALE = By.xpath("//input[@value='2']");
    public static final By GENDER_FEMALE = By.xpath("//input[@value='1']");
    public static final By SIGNUP_BUTTON = By.name("websubmit");
}
