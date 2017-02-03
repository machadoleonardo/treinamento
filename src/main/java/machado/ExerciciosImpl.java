package machado;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.BuscaConfig;
import main.Delay;
import main.Exercicio;
import main.IntervaloDeBusca;

public class ExerciciosImpl implements Exercicio{
	public static void main(String[] args) {
		ExerciciosImpl ex = new ExerciciosImpl();
		BuscaConfig buscaConfig = new BuscaConfig();
		buscaConfig.setTermo("Legião Urbana");
		buscaConfig.setPagina(3);
		buscaConfig.setIntervalo(IntervaloDeBusca.EM_QUALQUER_DATA);
//		long result = ex.getNumeroAproximadoDoResultadoDaBuscaPor(buscaConfig.getTermo());
//		System.out.println(result);
//		List<String> linksList = ex.getUrls(buscaConfig.getTermo());
//		for (String url : linksList ){
//			System.out.println(url);
//		}
//		List<String> linksList = ex.getUrls(buscaConfig.getTermo(), buscaConfig.getPagina());
//		for (String url : linksList ){
//			System.out.println(url);
//		}
		List<String> linksList = ex.getUrls(buscaConfig);
		for (String url : linksList ){
			System.out.println(url);
		}

	}
	
	private WebDriver loadWebDrive(String site){
		WebDriver webdriver = null;
		ChromeOptions options = new ChromeOptions();
		System.setProperty("webdriver.chrome.driver", "chromedriverlinux");
		options.addArguments("--start-maximized");
		webdriver = new ChromeDriver(options);
		webdriver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		webdriver.get(site);
		
		return webdriver;
	}
	
	public long getNumeroAproximadoDoResultadoDaBuscaPor(String termo) {
		WebDriver webdriver = loadWebDrive("https://www.google.com.br/");
		WebDriverWait wait = new WebDriverWait(webdriver, 10);
		long result = 0;
		try{
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("lst-ib")));
			webdriver.findElement(By.id("lst-ib")).sendKeys(termo);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultStats")));
			String resultString = webdriver.findElement(By.id("resultStats")).getText();
			Pattern pattern = Pattern.compile("(\\d+\\.?)+");
			Matcher matcher = pattern.matcher(resultString);
			matcher.find();
			String justNumber = matcher.group();

			NumberFormat nf = NumberFormat.getInstance();
			try {
				Number number = nf.parse(justNumber);
				result = number.longValue();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		} catch (Throwable t) {

			t.printStackTrace();

		} finally {
			webdriver.close();
			webdriver.quit();
		}

		return result;
	}

	public List<String> getUrls(String termo) {
		WebDriver webdriver = loadWebDrive("https://www.google.com.br/");
		WebDriverWait wait = new WebDriverWait(webdriver, 10);
		List<String> urlsList = new ArrayList<>();
		
		try{
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("lst-ib")));
			webdriver.findElement(By.id("lst-ib")).sendKeys(termo);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultStats")));
			List<WebElement> results = webdriver.findElement(By.id("search")).findElements(By.className("r"));

			for (WebElement result : results){
				urlsList.add(result.findElement(By.tagName("a")).getAttribute("href").toString());
				
			}
			results.size();
		} catch (Throwable t) {

			t.printStackTrace();

		} finally {
			webdriver.close();
			webdriver.quit();
		}

		return urlsList;
	
	}


	public List<String> getUrls(String termo, int pag) {
		WebDriver webdriver = loadWebDrive("https://www.google.com.br/");
		WebDriverWait wait = new WebDriverWait(webdriver, 10);
		List<String> urlsList = new ArrayList<>();
		
		try{
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("lst-ib")));
			webdriver.findElement(By.id("lst-ib")).sendKeys(termo);
			webdriver.findElement(By.id("sfdiv")).findElement(By.tagName("button")).submit();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav")));
			String pagina = webdriver.getCurrentUrl()+"&start="+((pag-1)*10);
			webdriver.get(pagina);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav")));
			List<WebElement> results = webdriver.findElement(By.id("search")).findElements(By.className("r"));

			for (WebElement result : results){
				urlsList.add(result.findElement(By.tagName("a")).getAttribute("href").toString());
				
			}
			results.size();
		} catch (Throwable t) {

			t.printStackTrace();

		} finally {
			webdriver.close();
			webdriver.quit();
		}

		return urlsList;
	
	}

	public List<String> getUrls(BuscaConfig config) {
		WebDriver webdriver = loadWebDrive("https://www.google.com.br/");
		WebDriverWait wait = new WebDriverWait(webdriver, 10);
		List<String> urlsList = new ArrayList<>();
		
		try{
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("lst-ib")));
			webdriver.findElement(By.id("lst-ib")).sendKeys(config.getTermo());
			webdriver.findElement(By.id("sfdiv")).findElement(By.tagName("button")).submit();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav")));
			String id = null;
			switch (config.getIntervalo()){
				case EM_QUALQUER_DATA: 
					id = "qdr_";
					break;
				case NA_ULTIMA_HORA: 
					id = "qdr_h";
					break;
				case NAS_ULTIMAS_24_HORAS: 
					id = "qdr_d";
					break;
				case NA_ULTIMA_SEMANA: 
					id = "qdr_w";
					break;
				case NO_ULTIMO_MES: 
					id = "qdr_m";
					break;
				case NO_ULTIMO_ANO: 
					id = "qdr_y";
					break;
				case INTERVALO_PERSONALIZADO: 
					id = "qdr_opt";
					break;
				
			}
			webdriver.findElement(By.id("hdtb-tls")).click();
			webdriver.findElement(By.id("hdtbMenus")).findElement(By.className("hdtb-mn-hd")).click();
			webdriver.findElement(By.id("hdtbMenus")).findElement(By.id(id)).click();
			webdriver.findElement(By.id("sfdiv")).findElement(By.tagName("button")).submit();
			String pagina = webdriver.getCurrentUrl()+"&start="+((config.getPagina()-1)*10);
			webdriver.get(pagina);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav")));
			List<WebElement> results = webdriver.findElement(By.id("search")).findElements(By.className("r"));

			for (WebElement result : results){
				urlsList.add(result.findElement(By.tagName("a")).getAttribute("href").toString());
				
			}
			results.size();
		} catch (Throwable t) {

			t.printStackTrace();

		} finally {
			webdriver.close();
			webdriver.quit();
		}

		return urlsList;
	}

	public String getWikiResume(String termo) {
		// TODO Auto-generated method stub
		return null;
	}

}
