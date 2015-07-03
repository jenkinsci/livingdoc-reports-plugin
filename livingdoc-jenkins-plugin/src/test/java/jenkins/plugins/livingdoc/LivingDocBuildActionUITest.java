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
package jenkins.plugins.livingdoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import info.novatec.testit.livingdoc.Statistics;

import java.awt.Color;

import jenkins.plugins.livingdoc.mapping.LivingDocBuildPage;
import jenkins.plugins.livingdoc.mapping.LivingDocBuildResultPage;
import jenkins.plugins.livingdoc.mapping.LivingDocProjectPage;
import jenkins.plugins.livingdoc.util.BankSamplesSCM;
import jenkins.plugins.livingdoc.util.JenkinsUITestCase;

import org.junit.Ignore;


@Ignore
public class LivingDocBuildActionUITest extends JenkinsUITestCase {

    private static final String TEST_PROJECT_NAME = LivingDocBuildActionUITest.class.getName();

    private FreeStyleProject project;
    private LivingDocProjectPage projectPage;

    @Override
    protected void setUp () throws Exception {
        super.setUp();

        project = createFreeStyleProject(TEST_PROJECT_NAME);
        LivingDocPublisher publisher = new LivingDocPublisher();
        project.getPublishersList().add(publisher);

        projectPage = newProjectPage(TEST_PROJECT_NAME);
    }

    @Deprecated
    public void testSpecificationsResultArePresent () throws Exception {

        projectPage.enableLivingDoc();

        project.setScm(new BankSamplesSCM());

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);

        LivingDocBuildPage buildPage = newBuildPage(TEST_PROJECT_NAME, build.getNumber());

        buildPage.showLivingDocBuildResultPage();

        assertThat(buildPage.getSummaryHeader(), containsString("6 specifications"));

        assertThat(buildPage.getSummaryStatistics(), containsString(new Statistics(59, 9, 1, 3).toString()));
        // assertEquals(85, buildPage.getSummaryRate());

        assertBuild(buildPage, 1, "LivingDoc-BNKT-Story 1", new Statistics(8, 4, 0, 0), 66);
        assertBuild(buildPage, 2, "LivingDoc-BNKT-Story 2", new Statistics(6, 0, 0, 0), 100);
        assertBuild(buildPage, 3, "LivingDoc-BNKT-Story 3", new Statistics(12, 0, 0, 0), 100);
        assertBuild(buildPage, 4, "LivingDoc-BNKT-Story 4", new Statistics(25, 5, 0, 0), 83);
        assertBuild(buildPage, 5, "LivingDoc-BNKT-Story 5", new Statistics(3, 0, 1, 0), 75);
        assertBuild(buildPage, 6, "LivingDoc-BNKT-Story 6", new Statistics(5, 0, 0, 3), 100);
    }

    @Deprecated
    public void testVerifyExistenceOfExternalUrl () throws Exception {

        projectPage.enableLivingDoc();

        project.setScm(new BankSamplesSCM());

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);

        LivingDocBuildPage buildPage = newBuildPage(TEST_PROJECT_NAME, build.getNumber());

        buildPage.showLivingDocBuildResultPage();

        assertThat(buildPage.getSummaryHeader(), containsString("6 specifications"));
        assertTrue(buildPage.isExternalUrlPresent(1));
        assertTrue(buildPage.isExternalUrlPresent(2));
        assertTrue(buildPage.isExternalUrlPresent(3));
        assertTrue(buildPage.isExternalUrlPresent(4));
        assertTrue(buildPage.isExternalUrlPresent(5));
        assertFalse(buildPage.isExternalUrlPresent(6));
    }

    @Deprecated
    public void testShouldDisplayExternalUrlOnHeaderOfViewResultAction () throws Exception {

        projectPage.enableLivingDoc();

        project.setScm(new BankSamplesSCM());

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);

        LivingDocBuildPage buildPage = newBuildPage(TEST_PROJECT_NAME, build.getNumber());

        buildPage.showLivingDocBuildResultPage();

        assertThat(buildPage.getSummaryHeader(), containsString("6 specifications"));

        assertTrue(buildPage.isViewResultUrlPresent(1));
        LivingDocBuildResultPage buildResultPage = buildPage.clickViewResultUrl(1);
        assertTrue(buildResultPage.isExternalUrlPresent());

        buildPage.showLivingDocBuildResultPage();
        assertTrue(buildPage.isViewResultUrlPresent(6));
        buildResultPage = buildPage.clickViewResultUrl(6);
        assertFalse(buildResultPage.isExternalUrlPresent());
    }

    @Deprecated
    private void assertBuild (LivingDocBuildPage buildPage, int buildId, String name, Statistics statistics, int rate) {
        assertThat(buildPage.getBuildName(buildId), containsString(name));
        assertEquals(rate == 100 ? Color.green : Color.red, buildPage.getBuildNameColor(buildId));
        assertThat(buildPage.getBuildStatistics(buildId), containsString(statistics.toString()));
        // assertEquals(rate, buildPage.getBuildRate(buildId));
    }
}
