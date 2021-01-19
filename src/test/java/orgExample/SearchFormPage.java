package orgExample;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SearchFormPage {
    private WebDriver driver;

    public SearchFormPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    @FindBy(id = "mat-input-0")
    private WebElement cityLeftField;

    @FindBy(id = "mat-input-1")
    private WebElement cityRightField;

    @FindBy(xpath = "/html/body/app-root/app-trip-direction/app-select-direction/div/form/div[2]/button[2]")
    private WebElement findBtn;

    public void enterCityLeftList(String cityLeftList) {
        cityLeftField.clear();
        cityLeftField.sendKeys(cityLeftList);
    }

    public void enterCityRightList(String cityRightList) {
        cityRightField.clear();
        cityRightField.sendKeys(cityRightList);
    }

    public void clickFindBtn() {
        findBtn.click();
    }
}
