package com.example.integration.test

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.bridge.SLF4JBridgeHandler
import org.testng.annotations.*
import java.util.concurrent.TimeUnit

// https://medium.com/kotlin-lang-notes/selenium-kotlintest-4db1da9811cc
class ProfilePage(private val driver: WebDriver) {

    private val pageUrl = "http://site.local:8080/chat/profile"

    init {
        PageFactory.initElements(driver, this)
    }

    fun open() = driver.get(pageUrl)

    fun verifyUrl() {
        WebDriverWait(driver, 10).until { it.currentUrl == pageUrl }
    }

    fun verifyContent() {
        driver.pageSource.contains("Hello Nikita Konev", false)
    }
}

class LoginPage(private val driver: WebDriver) {

    init {
        PageFactory.initElements(driver, this)
    }

    @FindBy(css = "div#kc-form #kc-form-login input#username")
    lateinit var login: WebElement

    @FindBy(css = "div#kc-form #kc-form-login input#password")
    lateinit var password: WebElement

    @FindBy(css = "div#kc-form #kc-form-login input#kc-login")
    lateinit var loginButton: WebElement

    fun verifyUrl() {
        WebDriverWait(driver, 10).until { it.currentUrl.startsWith("http://auth.site.local:8080/auth") }
    }
}

class KeycloakTest {

    lateinit var driver: WebDriver

    @BeforeSuite
    fun beforeSuite() {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        // https://sites.google.com/a/chromium.org/chromedriver/
        WebDriverManager.chromedriver().version("73.0.3683.68").setup();

        driver = ChromeDriver()

        driver.manage()?.timeouts()?.implicitlyWait(30, TimeUnit.SECONDS)
        driver.manage()?.window()?.maximize()


        println("find jar")
        println("start jar /home/nkonev/go_1_11/social-sender/chat/target/chat-app-0.0.0-jar-with-dependencies.jar")

        println("find go binary")
        println("start go binary /home/nkonev/go_1_11/social-sender/user-service/user-service")
    }

    @BeforeMethod
    fun before() {
        driver.manage().deleteAllCookies()
    }

    @Test(priority = 1)
    fun `Direct calling microservice requires authentication - positive`() {

        val profilePage = ProfilePage(driver)
        val loginPage = LoginPage(driver)

        profilePage.run {
            open()
        }

        loginPage.verifyUrl()

        loginPage.run {
            login.sendKeys("tester")
            password.sendKeys("tester")
            loginButton.click()
        }

        profilePage.verifyUrl()
        profilePage.verifyContent()
    }

    @Test(priority = 2)
    fun `Direct calling microservice requires authentication - negative`() {

        val profilePage = ProfilePage(driver)
        val loginPage = LoginPage(driver)


        profilePage.run {
            open()
        }

        loginPage.verifyUrl()

        loginPage.run {
            login.sendKeys("tester")
            password.sendKeys("tester2")
            loginButton.click()
        }

        loginPage.verifyUrl()
    }



    @AfterSuite
    fun afterSuite() {
        driver.close()
    }

}
