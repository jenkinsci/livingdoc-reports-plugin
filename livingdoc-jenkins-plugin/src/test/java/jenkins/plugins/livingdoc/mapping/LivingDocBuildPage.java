/**
 * Copyright (c) 2009 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */
package jenkins.plugins.livingdoc.mapping;

import java.awt.Color;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LivingDocBuildPage
		extends AbstractPage {

	private final String rootUrl;
	private final String projectName;
	private final int buildNumber;

	public LivingDocBuildPage(WebDriver wd, String rootUrl, String projectName, int buildNumber) {
		super(wd);

		this.rootUrl = rootUrl;
		this.projectName = projectName;
		this.buildNumber = buildNumber;
	}

	public void showLivingDocBuildResultPage() {
		String buildUrl = String.format("%sjob/%s/%s/livingdoc", rootUrl, projectName, buildNumber);
		navigateTo(buildUrl);
	}

	public String getSummaryHeader() {
		return findElement(By.id("ld_build_summary")).getText();
	}

	public String getSummaryStatistics() {
		return findElement(By.id("ld_build_summary_stats")).getText();
	}

	public int getSummaryRate() {
		WebElement element = findElement(By.id("ld_build_summary_rate"));
		return getRate(element.getText());
	}

	public String getBuildName(int buildId) {
		return buildName(buildId).getText();
	}

	public Color getBuildNameColor(int buildId) {
		WebElement element = buildName(buildId);
		String style = element.getAttribute("style");
		return style.indexOf("color: red") == -1 ? Color.green : Color.red;
	}

	public boolean isExternalUrlPresent(int buildId) {
		return findElement(By.id(String.format("ld_build_%s_extlink", buildId))) != null;
	}

	public boolean isViewResultUrlPresent(int buildId) {
		return buildViewResultUrl(buildId) != null;
	}

	public LivingDocBuildResultPage clickViewResultUrl(int buildId) {
//		WebElement button = buildViewResultUrl(buildId);
//		button.click();
//		wd.switchTo().window("gpResult");

		String resultUrl = String.format("%sjob/%s/%s/livingdoc/%s", rootUrl, projectName, buildNumber, buildId);
		navigateTo(resultUrl);

		return new LivingDocBuildResultPage(wd, buildId);
	}

	public String getBuildStatistics(int buildId) {
		return findElement(By.id(String.format("ld_build_%s_stats", buildId))).getText();
	}

	public int getBuildRate(int buildId) {
		WebElement element = findElement(By.id(String.format("ld_build_%s_rate", buildId)));
		return getRate(element.getText());
	}

	private WebElement buildName(int buildId) {
		return findElement(By.id(String.format("ld_build_%s_name", buildId)));
	}

	private WebElement buildViewResultUrl(int buildId) {
		return findElement(By.id(String.format("ld_build_%s_reslink", buildId)));
	}

	private int getRate(String text) {
		return Integer.parseInt(text.replace('%', ' ').trim());
	}
}
