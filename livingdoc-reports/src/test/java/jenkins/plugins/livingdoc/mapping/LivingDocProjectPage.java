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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LivingDocProjectPage
		extends AbstractPage {

	private final String projectName;
	private final String rootUrl;

	public LivingDocProjectPage(WebDriver webDriver, String rootUrl, String projectName) {
		super(webDriver);

		this.rootUrl = rootUrl;
		this.projectName = projectName;
	}

	public void enableLivingDoc() {

		LivingDocProjectConfigPage configPage =
				new LivingDocProjectConfigPage(wd, rootUrl, projectName);

		configPage.showConfiguration();
		configPage.setDefaultTextResultsPattern();
		configPage.save();
	}

	public void showProjectPage() {
		String configureUrl = String.format("%sjob/%s", rootUrl, projectName);
		navigateTo(configureUrl);
	}

	public boolean isLivingDocSidePanelMenuPresent() {
		return livingDocSidePanelMenu() != null;
	}

	public void clickLivingDocSidePanelMenu() {
		clickOn(livingDocSidePanelMenu());
	}

	public boolean isLivingDocContentMenuPresent() {
		return livingDocContentMenu() != null;
	}

	public boolean isLivingDocSummaryChartPresent() {
		return livingDocSummaryChart() != null;
	}

	public boolean isNoDataPresent() {
		return findElement(By.id("ld_nodata")) != null;
	}

	public boolean isBuildResultPresent(int buildNumber) {
		return findElement(By.id(String.format("ld_build_%s", buildNumber))) != null;
	}

	private WebElement livingDocSidePanelMenu() {
		String xpath = String.format("//a[@href='/job/%s/livingdoc']", projectName);
		return findElement(By.xpath(escapeSpaces(xpath)));
	}

	private WebElement livingDocContentMenu() {
		return findElement(By.xpath("//a[@href='greenpepper']"));
	}

	private WebElement livingDocSummaryChart() {
		return findElement(By.id("ld_project_graph"));
	}
}
