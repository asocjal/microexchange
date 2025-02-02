package net.satopay.satoexchange;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import bittech.lib.utils.Config;
import net.satopay.satoexchange.banks.BanksModule;
import net.satopay.satoexchange.core.CoreModule;
import net.satopay.satoexchange.hub.HubModule;
import net.satopay.satoexchange.ln.LnModule;
import net.satopay.satoexchange.web.WebModule;

public class App {

	public static void main(final String[] args) throws Exception {
		Config.loadFromFile(new File(".satoex/config.json"));
//		satoClick();
		startServer();
	}

	private static void startServer() {
		try {

			BanksModule banksModule = new BanksModule();
			LnModule lnModule = new LnModule();
			CoreModule coreModule = new CoreModule(banksModule, lnModule);
			HubModule hubModule = new HubModule(coreModule, banksModule);
			WebModule webModule = new WebModule(coreModule, lnModule, banksModule);

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				webModule.close();
				hubModule.close();
				coreModule.close();
				lnModule.close();
				banksModule.close();

				Runtime.getRuntime().halt(0); // TODO: Needed because webModule do not want to stop properly
			}));
		} catch (Exception ex) {
			Runtime.getRuntime().halt(0);
		}
	}

	private static void satoClick() throws Exception {
		System.setProperty("webdriver.gecko.driver", "geckodriver");

		FirefoxOptions firefoxOptions = new FirefoxOptions();
		FirefoxBinary fb = firefoxOptions.getBinary();
		fb.addCommandLineOptions("--headless");
		firefoxOptions.setBinary(fb);

		WebDriver driver = new FirefoxDriver(firefoxOptions);
		try {
			driver.get("https://tbtc.bitaps.com/");
			WebElement input = driver.findElement(By.id("faucet-address"));
			input.sendKeys("2NBMEXnU3untFp1gJd4xLvvUhUdr5gv1qfp");

			WebElement button = driver.findElement(By.id("receive"));
			for (;;) {
				Thread.sleep(10000);
				button.click();
			}
		} finally {
			driver.close();
		}
	}

}
