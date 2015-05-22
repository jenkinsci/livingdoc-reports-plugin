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


import java.io.IOException;

import jenkins.plugins.livingdoc.LivingDocProjectAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import static org.mockito.Mockito.*;

public class LivingDocProjectActionTest {

	private LivingDocProjectAction action;

	private AbstractProject<?, ?> project;

	private StaplerRequest request;
	private StaplerResponse response;

	@Before
	public void setUp() {

		project = mock(AbstractProject.class);

		action = new LivingDocProjectAction(project);

		request = mock(StaplerRequest.class);
		response = mock(StaplerResponse.class);
	}

	@After
	public void tearDown() {
		//paranoia mode !
		verifyNoMoreInteractions(project);
		verifyNoMoreInteractions(request);
		verifyNoMoreInteractions(response);
	}
	
	@Test
	public void projectWithNoBuildsShouldRedirectToNoDataPage()
			throws IOException {

		when(project.getLastBuild()).thenReturn(null);

		action.doIndex(request, response);

		verify(project).getLastBuild();
		verify(response).sendRedirect2("nodata");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldRedirectToFirstBuildFound()
			throws IOException {

/*		AbstractBuild build = mock(AbstractBuild.class);
		
		when(build.getNumber()).thenReturn(999);
		when(build.getAction(LivingDocBuildAction.class)).thenReturn(new LivingDocBuildAction(null, null));

		when(project.getLastBuild()).thenReturn(build);

		action.doIndex(request, response);

		verify(project).getLastBuild();
		verify(build).getAction(LivingDocBuildAction.class);
		verify(response).sendRedirect2("../999/greenpepper");
*/	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldRedirectToFirstBuildFoundWhenMultipleBuilds()
			throws IOException {

/*		AbstractBuild build1 = mock(AbstractBuild.class);
		when(build1.getAction(LivingDocBuildAction.class)).thenReturn(null);

		AbstractBuild build2 = mock(AbstractBuild.class);

		when(build2.getNumber()).thenReturn(999);
		when(build2.getAction(LivingDocBuildAction.class)).thenReturn(new LivingDocBuildAction(null, null));

		when(project.getLastBuild()).thenReturn(build1);
		when(build1.getPreviousBuild()).thenReturn(build2);

		action.doIndex(request, response);

		verify(project).getLastBuild();
		verify(build1).getAction(LivingDocBuildAction.class);
		verify(build2).getAction(LivingDocBuildAction.class);
		verify(response).sendRedirect2("../999/greenpepper");
*/	}
}
