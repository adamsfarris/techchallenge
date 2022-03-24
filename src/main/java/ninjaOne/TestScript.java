package ninjaOne;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

public class TestScript {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("test started");
		
		RestAssured.baseURI = "http://localhost:3000/devices/";
		RequestSpecification httpRequest = RestAssured.given(); 
		Response response = httpRequest.request(Method.GET, "");

		JSONArray nodes =  deviceResponse(response.getBody().asString().replace("[","").replace("]","").split("},"));
		
		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		
		driver.get("http://localhost:3001/");

//		driver.findElement(By.xpath("//*[@id='device_type']")).sendKeys("WINDOWS WORKSTATION");
		
		System.out.println("number of nodes: " + nodes.length());
		Thread.sleep(500);
		for(int x = 0;  x < nodes.length(); x++) {
			JSONObject singleNode = nodes.getJSONObject(x);
			
			Assert.assertTrue(driver.findElement(By.xpath("//span[@class='device-name' and text()='" + singleNode.getString("system_name") + "']")).isDisplayed());
			WebElement deviceMainBox = driver.findElement(By.xpath("//div[@class='device-main-box' and (.//*[text()='"+ singleNode.getString("system_name") + "'])]"));

			Assert.assertTrue(deviceMainBox.findElement(By.xpath(".//span[@class='device-type' and text()='" + singleNode.getString("type") + "']")).isDisplayed());
			Assert.assertTrue(deviceMainBox.findElement(By.xpath(".//span[@class='device-capacity' and text()='" + singleNode.getString("hdd_capacity") + "']")).isDisplayed());
			
			Assert.assertTrue(deviceMainBox.findElement(By.xpath(".//a[@class='device-edit']")).isDisplayed());
			Assert.assertTrue(deviceMainBox.findElement(By.xpath(".//button[@class='device-remove']")).isDisplayed());
			
			System.out.println("validated row for device name " + singleNode.getString("system_name"));
		}
	}
	
	public static JSONArray deviceResponse(String[] fulljson) {
		System.out.println("in method - array size: " + fulljson.length);
		JSONArray responses = new JSONArray();
		for (String singleNode : fulljson)
		{
			responses.put(new JSONObject(singleNode.trim()+"}+"));
		}
		return responses;
	}
	
	public static void highLighterMethod(WebDriver driver, WebElement element){
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element);
		}
}
