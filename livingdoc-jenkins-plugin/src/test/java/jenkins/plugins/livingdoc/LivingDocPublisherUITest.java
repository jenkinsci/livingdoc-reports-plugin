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
package jenkins.plugins.livingdoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import jenkins.plugins.livingdoc.LivingDocPublisher;
import jenkins.plugins.livingdoc.mapping.LivingDocProjectConfigPage;
import jenkins.plugins.livingdoc.util.JenkinsUITestCase;
import hudson.model.FreeStyleProject;

public class LivingDocPublisherUITest
		extends JenkinsUITestCase {

	private static final String TEST_PROJECT_NAME = LivingDocPublisherUITest.class.getName();

	private FreeStyleProject project;
	private LivingDocProjectConfigPage configPage;

	@Override
	protected void setUp()
			throws Exception {
		super.setUp();

		project = createFreeStyleProject(TEST_PROJECT_NAME);
		LivingDocPublisher publisher = new LivingDocPublisher();
		project.getPublishersList().add(publisher);

		configPage = newConfigPage(TEST_PROJECT_NAME);
	}

	public void testVerifyStateForNewProject()
			throws Exception {

		configPage.showConfiguration();

		assertNull(configPage.getTestResultsPattern());
	}

	public void testCheckParametersPersistence()
			throws Exception {

		configPage.showConfiguration();

		
		configPage.setTestResultsPattern("**/junit/**.xml");

		configPage.save();

		configPage.showConfiguration();

		assertEquals("**/junit/**.xml", configPage.getTestResultsPattern());
	}
}
