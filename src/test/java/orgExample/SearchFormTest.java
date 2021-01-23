package orgExample;

import com.sun.istack.internal.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private List<WebElement> prices, allTime;
    private List<WebElement> elements;
    private WebElement parent;

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

    private List<String> checkValidationCities(int iteratorForLeftListCities, @NotNull List<String> cities, int citiesCount) throws InterruptedException, IOException {
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
                prices = driver.findElements(By.tagName("mat-chip"));
                for (int i = 0; i < prices.size(); i++) {
                    parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                            "return arguments[0].parentNode;", prices.get(i));
                    System.out.println("общее время маршрута " + (parent.findElement(By.tagName("span"))).getText());
                    String totalRouteTime = (parent.findElement(By.tagName("span"))).getText();
                    System.out.println("общее количество минут из общего времени маршрута " + parseStringToTime(totalRouteTime));
                    int timeRoute = parseStringToTime(totalRouteTime);
                    System.out.println("общий прайс маршрута " + prices.get(i).getText());
                    String totalPriceOfRoute = prices.get(i).getText().replaceAll("[^\\d.]", "");
                    double priceRoute = Double.parseDouble(totalPriceOfRoute);
                    parent.click();
                    Thread.sleep(1000);
                    List<WebElement> listRoute = driver.findElements(By.className("mat-expansion-panel-body"));
                    elements = listRoute.get(i).findElements(By.cssSelector("div.details.ng-star-inserted"));
                    double sumPrice = 0;
                    int sumMinutes = 0;
                    WebElement tt = elements.get(0).findElements(By.tagName("span")).get(2);
                    for (WebElement element : elements) {
                        System.out.println("время пересадки " + (element.findElements(By.tagName("span"))).get(2).getText());
                        totalRouteTime = (element.findElements(By.tagName("span"))).get(2).getText();
                        System.out.println("время пересадки в минутах " + parseStringToTime(totalRouteTime));
                        sumMinutes += parseStringToTime(totalRouteTime);
                        System.out.println("прайс пересадки " + (element.findElements(By.tagName("span"))).get(4).getText());
                        totalPriceOfRoute = (element.findElements(By.tagName("span"))).get(4).getText().replaceAll("[^\\d.]", "");
                        double priceTransfer = Double.parseDouble(totalPriceOfRoute);
                        sumPrice += priceTransfer;
                    }
//                  round the number for the second decimal place
                    sumPrice = round(sumPrice, 2);
                    System.out.println("общая сумма маршрута " + sumPrice);
                    System.out.println("общее суммарное время маршрута " + sumMinutes);

                    if (priceRoute != sumPrice) {
                        Screen();
//                  problem routes can be written to an array or a list
                    } else {
                        System.out.println("все прайсы равны");
                    }
                    if (timeRoute < sumMinutes) {
                        Screen();
//                  problem routes can be written to an array or a list
                    } else {
                        System.out.println("суммарное время пересадок меньше или равно общему");
                    }
                }
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

    private static int parseStringToTime(@NotNull String z) {
        String[] arrayStringToTime = z.split(" ");
        int coefficient = 1;
        int flagMinutesOrDaysOrHours = 1;
        int intStringToTime, sumTime = 0;
        String stringTimeFromArray;
        for (int j = arrayStringToTime.length - 1; j >= 0; j--) {
            stringTimeFromArray = arrayStringToTime[j].replaceAll("[^\\d.]", "");
            intStringToTime = Integer.parseInt(stringTimeFromArray);
            if (flagMinutesOrDaysOrHours == 2) {
                coefficient = 60;
            }
            if (flagMinutesOrDaysOrHours == 3) {
                coefficient = 24 * 60;
            }
            intStringToTime *= coefficient;
            sumTime += intStringToTime;
            flagMinutesOrDaysOrHours++;
        }
        return sumTime;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }
}
