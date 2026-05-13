package es.udc.paproject.e2etests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private WebDriver driver;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void teardown() {
        driver.quit();
    }

    private void login(String userName, String password) {
        driver.get("http://localhost:5173");

        WebElement loginLink = driver.findElement(By.id("loginLink"));
        loginLink.click();

        WebElement userNameInput = driver.findElement(By.id("userName"));
        userNameInput.sendKeys(userName);

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys(password);

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        WebElement userDropdown = driver.findElement(By.id("user-dropdown"));
        assertTrue(userDropdown.getText().contains(userName));
    }

    @Test
    public void testLogin() {
        login("testviewer", "pa2526");
    }
    @Test
    public void testSessionDetails() {

        //login
        login("testviewer", "pa2526");

        //En el desplegable del día, seleccionar la segunda opción (mañana)
        Select dateSelect = new Select(driver.findElement(By.id("billboardDate")));
        dateSelect.selectByIndex(1);

        //Localizar el enlace de la primera película y guardar su texto
        List<WebElement> movieLinks = driver.findElements(By.cssSelector(".card-title a"));
        WebElement firstMovieLink = movieLinks.get(0);
        String movieName = firstMovieLink.getText();

        //Localizar el enlace de la primera sesión de esa película y guardar su texto
        List<WebElement> sessionLinks = driver.findElements(By.cssSelector(".sessions a"));
        WebElement firstSessionLink = sessionLinks.get(0);
        String sessionTime = firstSessionLink.getText();

        //Hacer clic en el enlace de la primera sesión
        firstSessionLink.click();

        //Comprobar que existen todos los campos esperados (por id)
        driver.findElement(By.id("movieTitle"));
        driver.findElement(By.id("duration"));
        driver.findElement(By.id("price"));
        driver.findElement(By.id("date"));
        driver.findElement(By.id("time"));
        driver.findElement(By.id("room"));
        driver.findElement(By.id("availableTickets"));

        //Comprobar que el nombre de la película coincide
        WebElement movieTitleElement = driver.findElement(By.id("movieTitle"));
        assertEquals(movieName, movieTitleElement.getText());

        //Comprobar que la hora de la sesión coincide
        WebElement timeElement = driver.findElement(By.id("time"));
        assertTrue(timeElement.getText().contains(sessionTime));

        //Comprobar que incluye el formulario para comprar entradas.
        driver.findElement(By.id("buyForm"));
    }
}