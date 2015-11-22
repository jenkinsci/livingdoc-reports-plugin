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

import hudson.model.FreeStyleBuild;
import hudson.model.Result;
import hudson.model.FreeStyleProject;

import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;

public class LivingDocPublisherTest {

	private static final String TEST_PROJECT_NAME = LivingDocPublisherTest.class.getName();

	@Rule
	private JenkinsRule jr = new JenkinsRule();

	public void testShouldThrowExceptionWhenNoSummaryFound() throws Exception {

		FreeStyleProject project = jr.createFreeStyleProject(TEST_PROJECT_NAME);

		LivingDocReportsPublisher publisher = new LivingDocReportsPublisher("**nothing**", 0, null);
		project.getPublishersList().add(publisher);

		FreeStyleBuild build = project.scheduleBuild2(0).get();
		jr.assertBuildStatus(Result.FAILURE, build);

		jr.assertLogContains("hudson.AbortException: No LivingDoc test report files were found. Configuration error?", build);
	}
}
