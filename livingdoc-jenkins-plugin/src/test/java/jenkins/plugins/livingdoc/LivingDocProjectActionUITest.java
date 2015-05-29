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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import jenkins.plugins.livingdoc.mapping.LivingDocProjectPage;
import jenkins.plugins.livingdoc.util.BankSamplesSCM;
import jenkins.plugins.livingdoc.util.JenkinsUITestCase;


public class LivingDocProjectActionUITest extends JenkinsUITestCase {

    private static final String TEST_PROJECT_NAME = LivingDocProjectActionUITest.class.getName();

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

    public void testCheckThatLivingDocLinksArePresentWhenNoBuilds () throws Exception {

        projectPage.enableLivingDoc();

        projectPage.showProjectPage();

        assertTrue(projectPage.isLivingDocSidePanelMenuPresent());
        assertTrue(projectPage.isLivingDocContentMenuPresent());
        assertFalse(projectPage.isLivingDocSummaryChartPresent());
    }

    public void testCheckThatNoDataMessageIsPresentWhenNoBuilds () throws Exception {

        projectPage.enableLivingDoc();

        projectPage.showProjectPage();

        projectPage.clickLivingDocSidePanelMenu();

        assertTrue(projectPage.isNoDataPresent());
    }

    public void testCheckThatSummaryChartIsPresentAfterTheBuild () throws Exception {

        projectPage.enableLivingDoc();

        project.setScm(new BankSamplesSCM());

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);

        projectPage.showProjectPage();

        assertTrue(projectPage.isLivingDocSidePanelMenuPresent());
        assertTrue(projectPage.isLivingDocContentMenuPresent());
        assertTrue(projectPage.isLivingDocSummaryChartPresent());
    }

    public void testCheckThatProjectMenuLinkRedirectToLatestBuild () throws Exception {

        projectPage.enableLivingDoc();

        project.setScm(new BankSamplesSCM());

        FreeStyleBuild build1 = project.scheduleBuild2(0).get();
        assertBuildStatusSuccess(build1);

        FreeStyleBuild build2 = project.scheduleBuild2(0).get();
        assertBuildStatusSuccess(build2);

        projectPage.showProjectPage();

        projectPage.clickLivingDocSidePanelMenu();

        assertTrue(projectPage.isBuildResultPresent(build2.getNumber()));
    }
}
