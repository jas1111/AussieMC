package ag.eztv.qa.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import ag.eztv.qa.util.GlobalProperties;



public class BasePage {

    public static WebDriver driver;
    public static Properties prop;

    
    public BasePage(){
        try {
                prop = new Properties();
                FileInputStream ip = new FileInputStream(System.getProperty("user.dir")+ "/src/main/java/ag/eztv/qa/config/config.properties");
                prop.load(ip);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
}


public static void initialization(){
        String browserName = prop.getProperty("browser");
        String url = prop.getProperty("url");
 
        
        
        
        if(browserName.equals("chrome")){
                System.setProperty("webdriver.chrome.driver", "/Users/Jas/Downloads/chromedriver");  
                driver = new ChromeDriver(); 
        }
        else if(browserName.equals("FF")){
                System.setProperty("webdriver.gecko.driver", "/Users/Jas/Downloads/chromedriver");     
                driver = new FirefoxDriver(); 
        }
        
 //       driver.manage().window().maximize();

        driver.manage().timeouts().pageLoadTimeout(GlobalProperties.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(GlobalProperties.IMPLICIT_WAIT, TimeUnit.SECONDS);
		driver.get(url);
        
}
}
