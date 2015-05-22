package jenkins.plugins.livingdoc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import jenkins.plugins.livingdoc.mapping.LivingDocBuildPage;
import jenkins.plugins.livingdoc.mapping.LivingDocProjectConfigPage;
import jenkins.plugins.livingdoc.mapping.LivingDocProjectPage;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.jvnet.hudson.test.JenkinsRule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

public class JenkinsUITestCase extends JenkinsRule {

	private WebDriver webDriver;

	@Before
	protected void setUp() throws Exception {

		
		// http://groups.google.com/group/webdriver/browse_thread/thread/e344d78702d78822
		String xvfbDisplayId = getXvfbDisplayId();
		System.setProperty("selenium.browser", "firefox");
		if (xvfbDisplayId != null) {
			FirefoxBinary firefox = new FirefoxBinary();
			firefox.setEnvironmentProperty("DISPLAY", xvfbDisplayId);
			webDriver = new FirefoxDriver(firefox, null);
		} else {
			FirefoxProfile firefoxProfile = new FirefoxProfile();
			DesiredCapabilities firefoxCap = DesiredCapabilities.firefox();
			firefoxCap.setCapability(FirefoxDriver.PROFILE, firefoxProfile);

			webDriver = new FirefoxDriver(firefoxCap);
		}

		/*
		 * DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		 * capabilities.setJavascriptEnabled(true); webDriver = new
		 * RemoteWebDriver(new URL("http://serpens:3001/wd"), capabilities);
		 */
	}

	@After
	protected void tearDown() throws Exception {
		webDriver.close();
	}

	protected String getLocalHostName() throws UnknownHostException {
		// InetAddress addr = InetAddress.getLocalHost();
		return "localhost";// addr.getHostName();
	}

	protected String getExternalURL() throws IOException {
		String url = getURL().toExternalForm();
		return url.replace("localhost", getLocalHostName());
	}

	protected LivingDocProjectConfigPage newConfigPage(String projectName)
			throws IOException {
		return new LivingDocProjectConfigPage(webDriver, getExternalURL(),
				projectName);
	}

	protected LivingDocProjectPage newProjectPage(String projectName)
			throws IOException {
		return new LivingDocProjectPage(webDriver, getExternalURL(),
				projectName);
	}

	protected LivingDocBuildPage newBuildPage(String projectName,
			int buildNumber) throws IOException {
		return new LivingDocBuildPage(webDriver, getExternalURL(),
				projectName, buildNumber);
	}

	private String getXvfbDisplayId() throws IOException {

		File displayPropertyFile = new File(
				"target/selenium/display.properties");

		if (!displayPropertyFile.exists()) {
			return null;
		}

		Properties displayProperties = new Properties();
		FileInputStream is = null;

		try {
			is = new FileInputStream(displayPropertyFile);

			displayProperties.load(is);
		} finally {
			IOUtils.closeQuietly(is);
		}

		return displayProperties.getProperty("DISPLAY", ":38");
	}
}
