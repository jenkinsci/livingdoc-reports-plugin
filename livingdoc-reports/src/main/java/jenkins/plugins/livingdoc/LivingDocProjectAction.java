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

import hudson.model.ParameterValue;
import hudson.model.ProminentProjectAction;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import hudson.util.Graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import jenkins.plugins.livingdoc.chart.ProjectSummaryChart;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class LivingDocProjectAction extends Actionable implements ProminentProjectAction {

	public static final String LD_CHART_MAX_COUNT_BUILDS = "LD_CHART_MAX_COUNT_BUILDS";

	private final AbstractProject<?, ?> project;

	public LivingDocProjectAction(AbstractProject<?, ?> project) {
		super();

		this.project = project;
	}

	public AbstractProject<?, ?> getProject() {
		return project;
	}

	public String getIconFileName() {
		return ResourceUtils.BIG_ICON_URL;
	}

	public String getSearchUrl() {
		return getUrlName();
    }

	public String getUrlName() {
		return ResourceUtils.PLUGIN_CONTEXT_BASE;
	}

	public String getDisplayName() {
		return "testIT LivingDoc";
	}

	public List<SummaryBuildReportBean> getSummaries() {
		return getAllLivingDocBuildSummaries();
	}

	public boolean hasSummaries() {
		return getAllLivingDocBuildSummaries().size() > 0;
	}

	public void doIndex(StaplerRequest request, StaplerResponse response) throws IOException {

		AbstractBuild<?, ?> build = findLatestLivingDocBuild();

		if (build == null) {
			response.sendRedirect2("nodata");
		} else {
			int buildId = build.getNumber();
			response.sendRedirect2(String.format("../%s/" + ResourceUtils.PLUGIN_CONTEXT_BASE, buildId));
		}
	}

	public Graph getGraph() {
		Calendar timestamp = project.getLastCompletedBuild().getTimestamp();
		return new ProjectSummaryChart(timestamp, getAllLivingDocBuildSummaries());
	}

	private List<SummaryBuildReportBean> getAllLivingDocBuildSummaries() {

		List<SummaryBuildReportBean> summaries = new ArrayList<SummaryBuildReportBean>();
		int maxCountBuilds = getChartMaxCountBuilds();
		int count = 0;
		boolean mayAddEntries = maxCountBuilds == -1 || count <= maxCountBuilds;
		for (AbstractBuild<?, ?> build = project.getLastBuild(); build != null && mayAddEntries; build = build.getPreviousBuild()) {

			LivingDocBuildAction buildAction = findBuildAction(build);

			if (buildAction != null) {
				summaries.add(buildAction.getSummary());
				if (maxCountBuilds > -1) {
					count++;
					mayAddEntries = count < maxCountBuilds;
				}
			}
		}

		Collections.reverse(summaries);

		return summaries;
	}

	@SuppressWarnings("rawtypes")
	private int getChartMaxCountBuilds() {

		int maxCountBuilds = -1;

		AbstractBuild lastBuild = project.getLastBuild();
		if (lastBuild != null) {
			ParametersAction action = lastBuild.getAction(ParametersAction.class);
			if (action != null) {
				ParameterValue paramValue = action.getParameter(LD_CHART_MAX_COUNT_BUILDS);
				if (paramValue != null && paramValue instanceof StringParameterValue) {
					String stringValue = ((StringParameterValue) paramValue).value;
					maxCountBuilds = Integer.parseInt(stringValue);
				}
			}
		}
		return maxCountBuilds;

	}

	private AbstractBuild<?, ?> findLatestLivingDocBuild() {

		for (AbstractBuild<?, ?> build = project.getLastBuild(); build != null; build = build.getPreviousBuild()) {

			if (findBuildAction(build) != null) {
				return build;
			}
		}

		return null;
	}

	private LivingDocBuildAction findBuildAction(AbstractBuild<?, ?> build) {
		return build.getAction(LivingDocBuildAction.class);
	}
}
