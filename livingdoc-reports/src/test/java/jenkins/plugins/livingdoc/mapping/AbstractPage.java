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

import static org.junit.Assert.assertNotNull;
import info.novatec.testit.livingdoc.util.ExceptionImposter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AbstractPage {

	protected final WebDriver wd;

	public AbstractPage(WebDriver wd) {
		this.wd = wd;
	}

	protected void navigateTo(String url) {
		try {
			wd.navigate().to(url);
		}
		catch (Exception ex) {
			throw ExceptionImposter.imposterize(ex);
		}
	}

	protected void clickOn(WebElement element) {
		assertNotNull(element);
		element.click();
	}

	protected WebElement findElement(By by) {
		try {
			return wd.findElement(by);
		}
		catch (Exception e) {
			return null;
		}
	}

	protected String escapeSpaces(String text) {
		return text.replaceAll(" ", "%20");
	}
}