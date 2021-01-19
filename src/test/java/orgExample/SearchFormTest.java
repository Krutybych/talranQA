package orgExample;

import com.sun.istack.internal.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

public class SearchFormTest {
    private List<String> errorCities = new ArrayList<>();
    private List<String> foundErrorCities = new ArrayList<>();
    private By itemInLeftDropdown, itemInRightDropdown;
    private int count = 0;
    private String cityLeft, cityRight;
    private ReadingDataFromFile readingDataFromFile = new ReadingDataFromFile();
    private static SearchFormPage searchFormPage;

    private static WebDriver driver;

    @BeforeClass
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", ConfProperties.getProperty("chromeDriver"));
        driver = new ChromeDriver();
        searchFormPage = new SearchFormPage(driver);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(ConfProperties.getProperty("searchPage"));
    }

    @Test
    public void searchFormTest() throws IOException, InterruptedException {
        List<String> cities = readingDataFromFile.data(ConfProperties.getProperty("pathToDatabase"));
        int citiesCount = cities.size();
        errorCities = checkValidationCities(0, cities, citiesCount);
        cities.removeAll(errorCities);
        citiesCount = cities.size();
        for (int iteratorForLeftListCities = 1; iteratorForLeftListCities < citiesCount; iteratorForLeftListCities++) {
            checkValidationCities(iteratorForLeftListCities, cities, citiesCount);
        }
        System.out.println(errorCities);
    }

    private List<String> checkValidationCities(int iteratorForLeftListCities, @NotNull List<String> cities, int citiesCount) throws InterruptedException {
        cityLeft = cities.get(iteratorForLeftListCities);
        itemInLeftDropdown = By.xpath("//*[contains(., '" + cityLeft + "')]");
        searchFormPage.enterCityLeftList(cityLeft);
        Thread.sleep(4000);
        for (int iteratorForRightListCities = 0; iteratorForRightListCities < citiesCount; iteratorForRightListCities++) {
            if (iteratorForLeftListCities == iteratorForRightListCities) {
                iteratorForRightListCities++;
            }
            if (iteratorForRightListCities == citiesCount) {
                break;
            }
            cityRight = cities.get(iteratorForRightListCities); // take out all "get" in searchFormPage
            searchFormPage.enterCityRightList(cityRight);
            itemInRightDropdown = By.xpath("//*[contains(., '" + cityRight + "')]");
            Thread.sleep(4000);
            try {
                searchFormPage.clickFindBtn();
                By elementPrice = By.tagName("mat-chip"); // take out in searchFormPage
                Thread.sleep(4000);
                driver.findElement(By.tagName("mat-chip")).click();
                Screen();
            } catch (Exception e) {
                driver.findElement(By.cssSelector(".Back")).click();
                foundErrorCities.add(cityRight);
                e.printStackTrace();
            }
        }
        return foundErrorCities;
    }

    private void Screen() throws IOException { // take out in separate file
        count++;
        File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String path = "D:\\qa\\screen\\screen" + count + ".png"; // take out in resource file
        FileHandler.copy(screen, new File(path));
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }
}
