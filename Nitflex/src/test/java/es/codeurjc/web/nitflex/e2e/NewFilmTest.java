package es.codeurjc.web.nitflex.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import es.codeurjc.web.nitflex.Application;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.UserRepository;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NewFilmTest{

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup(){
        userRepository.save(new User("FAKE USERX", "fakeUserX@gmail.com"));
        driver = new ChromeDriver();
        wait=new WebDriverWait(driver,Duration.ofSeconds(30));
        driver.get("http://localhost:" + this.port + "/");
    }

    @AfterEach
    public void teardown(){
        if (driver!=null){
            driver.quit();
        }
    }

    @Test
    public void new_correct_film() throws InterruptedException{
        //Main page
        WebElement createFilmButton=wait.until(presenceOfElementLocated(By.id("create-film")));
        createFilmButton.click();
        //Film form
        WebElement newFilmTitleInput=wait.until(presenceOfElementLocated(By.name("title")));
        String newTitle="newTitle";
        newFilmTitleInput.sendKeys(newTitle);
        WebElement saveFilmButton=driver.findElement(By.id("Save"));
        saveFilmButton.click();
        //New film page (correct creation check)
        WebElement filmTitle=wait.until(presenceOfElementLocated(By.id("film-title")));
        assertEquals(newTitle,filmTitle.getText());
    }

    @Test
    public void new_film_without_title(){
        //Main page
        WebElement createFilmButton=wait.until(presenceOfElementLocated(By.id("create-film")));
        List<WebElement> allFilms=driver.findElements(By.className("content"));
        int numFilmsBefore=allFilms.size();
        createFilmButton.click();
        //Film form
        WebElement saveFilmButton=wait.until(presenceOfElementLocated(By.id("Save")));
        saveFilmButton.click();
        //Error (no title)
        WebElement saveFilmError=wait.until(presenceOfElementLocated(By.id("error-list")));
        assertEquals("The title is empty",saveFilmError.getText());
        //Main page again (no changes check)
        driver.get("http://localhost:" + this.port + "/");
        allFilms=driver.findElements(By.className("content"));
        int numFilmsAfter=allFilms.size();
        assertEquals(numFilmsBefore, numFilmsAfter);
    }

}