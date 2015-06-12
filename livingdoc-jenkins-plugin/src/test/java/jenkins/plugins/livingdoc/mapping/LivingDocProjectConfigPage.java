/**
 * Copyright (c) 2009 Pyxis Technologies inc.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package jenkins.plugins.livingdoc.mapping;

import static org.junit.Assert.assertNotNull;
import info.novatec.testit.util.CollectionUtil;
import info.novatec.testit.util.ExceptionImposter;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class LivingDocProjectConfigPage extends AbstractPage {

    private final String projectName;
    private final String rootUrl;

    public LivingDocProjectConfigPage (WebDriver webDriver, String rootUrl, String projectName) {
        super(webDriver);

        this.rootUrl = rootUrl;
        this.projectName = projectName;
    }

    public void showConfiguration () {
        String configureUrl = String.format("%sjob/%s/configure", rootUrl, projectName);
        navigateTo(configureUrl);
        assertPublisherPresent();
    }

    public void assertPublisherPresent () {
        assertNotNull(testResultsPattern());
    }

    public String getTestResultsPattern () {
        return StringUtils.trimToNull(testResultsPattern().getText());
    }

    public void setTestResultsPattern (String pattern) {
        testResultsPattern().sendKeys(StringUtils.trimToEmpty(pattern));
    }

    public void setDefaultTextResultsPattern () {
        setTestResultsPattern("**/livingdoc-reports/**/*.xml");
    }

    public void save () {

        try {
            WebElement submitButton = CollectionUtil.last(wd.findElements(By.tagName("button")));
            clickOn(submitButton);
        } catch (Exception ex) {
            throw ExceptionImposter.imposterize(ex);
        }
    }

    private WebElement testResultsPattern () {
        return findElement(By.name("livingdoc_reports.testResultsPattern"));
    }
}
