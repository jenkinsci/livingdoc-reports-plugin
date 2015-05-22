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

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.kohsuke.stapler.StaplerRequest;

public class LivingDocPublisher extends Recorder implements Serializable {

	private static final long serialVersionUID = 1L;

	LivingDocPublisherConfig config = new LivingDocPublisherConfig();
	private String systemProperties = null;
	// Needed to succeed field check
	private String testResultsPattern = null;
	private boolean publishToConfluence = false;
	private String confluenceUrl = null;
	private String filenamePrefix = null;
	private String confluenceSpaceKey = null;
	private String confluenceSiteTitle = null;
	private int failureThreshold = 60;

	@Extension
	public static final LivingDocDescriptor DESCRIPTOR = new LivingDocDescriptor();

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		BuildLogger.intialize(listener);
		FilePath workspaceDir = build.getWorkspace();
		FilePath buildDir = new FilePath(build.getRootDir());
		if (StringUtils.isNotEmpty(systemProperties)) {
			setSystemProperties();
		}
		SummaryBuildReportBean summary = workspaceDir
				.act(new LivingDocReportCollector(buildDir,
						build.getNumber(), config, build.getBuildVariables()));

		if (summary.hasNoReports()) {
			throw new AbortException(
					"No LivingDoc test report files were found. Configuration error?");
		}

		build.getActions().add(new LivingDocBuildAction(build, summary));
		BuildLogger.info("Test results :" + summary.getStatistics());
		int totalWithoutIgnored = summary.getStatistics().totalCount()
				- summary.getStatistics().ignoredCount();
		int successRate = (100 * summary.getStatistics().rightCount())
				/ totalWithoutIgnored;
		int failureRate = 100 - successRate;
		if (failureRate > config.getFailureThreshold()) {
			BuildLogger.severe("Too much tests failed !!!!  (" + failureRate
					+ "% [Threshold: " + config.getFailureThreshold() + "%])");
			build.setResult(Result.FAILURE);
		} else if (successRate < 100) {
			BuildLogger.warn("Some Tests failed  (" + failureRate
					+ "% [Threshold: " + config.getFailureThreshold() + "%])");
			build.setResult(Result.UNSTABLE);
		}
		return true;
	}

	@Override
	public LivingDocDescriptor getDescriptor() {
		return DESCRIPTOR;
	}

	/**
	 * @return
	 * @see jenkins.plugins.livingdoc.LivingDocPublisherConfig#isPublishToConfluence()
	 */
	public boolean isPublishToConfluence() {
		return config.isPublishToConfluence();
	}

	/**
	 * @return the systemProperties
	 */
	public String getSystemProperties() {
		return systemProperties;
	}

	/**
	 * @param systemProperties
	 *            the systemProperties to set
	 */
	public void setSystemProperties(String systemProperties) {
		this.systemProperties = systemProperties;
	}

	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		return new LivingDocProjectAction(project);
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	/**
	 * @return
	 * @see jenkins.plugins.livingdoc.LivingDocPublisherConfig#getFilenamePrefix()
	 */
	public String getFilenamePrefix() {
		return config.getFilenamePrefix();
	}

	public String getConfluenceSpaceKey() {
		return config.getConfluenceSpaceKey();
	}

	public void setConfluenceSpaceKey(String confluenceSpaceKey) {
		config.setConfluenceSpaceKey(confluenceSpaceKey);
	}

	public String getConfluenceSiteTitle() {
		return config.getConfluenceSiteTitle();
	}

	public void setConfluenceSiteTitle(String confluenceSiteTitle) {
		config.setConfluenceSiteTitle(confluenceSiteTitle);
	}

	/**
	 * @param filenamePrefix
	 * @see jenkins.plugins.livingdoc.LivingDocPublisherConfig#setFilenamePrefix(java.lang.String)
	 */
	public void setFilenamePrefix(String filenamePrefix) {
		config.setFilenamePrefix(filenamePrefix);
	}

	/**
	 * @return
	 * @see jenkins.plugins.livingdoc.LivingDocPublisherConfig#getTestResultsPattern()
	 */
	public String getTestResultsPattern() {
		return config.getTestResultsPattern();
	}

	/**
	 * @param testResultsPattern
	 * @see jenkins.plugins.livingdoc.LivingDocPublisherConfig#setTestResultsPattern(java.lang.String)
	 */
	public void setTestResultsPattern(String testResultsPattern) {
		config.setTestResultsPattern(testResultsPattern);
	}

	/**
	 * @param publishToConfluence
	 * @see jenkins.plugins.livingdoc.LivingDocPublisherConfig#setPublishToConfluence(boolean)
	 */
	public void setPublishToConfluence(boolean publishToConfluence) {
		config.setPublishToConfluence(publishToConfluence);
	}

	/**
	 * @return
	 * @see jenkins.plugins.livingdoc.LivingDocPublisherConfig#getConfluenceUrl()
	 */
	public String getConfluenceUrl() {
		return config.getConfluenceUrl();
	}

	/**
	 * @param confluenceUrl
	 * @see jenkins.plugins.livingdoc.LivingDocPublisherConfig#setConfluenceUrl(java.lang.String)
	 */
	public void setConfluenceUrl(String confluenceUrl) {
		config.setConfluenceUrl(confluenceUrl);
	}

	/**
	 * @return
	 * @see jenkins.plugins.livingdoc.LivingDocPublisherConfig#getFailureThreshold()
	 */
	public int getFailureThreshold() {
		return config.getFailureThreshold();
	}

	/**
	 * @param failureThreshold
	 * @see jenkins.plugins.livingdoc.LivingDocPublisherConfig#setFailureThreshold(int)
	 */
	public void setFailureThreshold(int failureThreshold) {
		config.setFailureThreshold(failureThreshold);
	}

	private void setSystemProperties() {
		Properties sysProps = System.getProperties();
		StringInputStream sis = new StringInputStream(systemProperties);

		try {
			sysProps.load(sis);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				sis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static final class LivingDocDescriptor extends
			BuildStepDescriptor<Publisher> {

		public LivingDocDescriptor() {
			super(LivingDocPublisher.class);
			load();
		}

		@Override
		public String getDisplayName() {
			return Messages
					.LivingDocPublisher_livingdoc_publisher_displayname();
		}

		@Override
		public boolean isApplicable(Class type) {
			return true;
		}

		@Override
		public Publisher newInstance(StaplerRequest req, JSONObject formData)
				throws FormException {
			LivingDocPublisher pub = new LivingDocPublisher();
			req.bindParameters(pub, "livingdoc_reports.");
			return pub;
		}
	}
}
