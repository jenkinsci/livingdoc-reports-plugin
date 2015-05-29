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


@SuppressWarnings ( "unused" )
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

    @Deprecated
    @Override
    public boolean perform (AbstractBuild< ? , ? > build, Launcher launcher, BuildListener listener)
        throws InterruptedException, IOException {
        BuildLogger.intialize(listener);
        FilePath workspaceDir = build.getWorkspace();
        FilePath buildDir = new FilePath(build.getRootDir());
        if (StringUtils.isNotEmpty(systemProperties)) {
            setSystemProperties();
        }
        SummaryBuildReportBean summary = workspaceDir.act(new LivingDocReportCollector(buildDir, build.getNumber(), config));

        if (summary.hasNoReports()) {
            throw new AbortException("No LivingDoc test report files were found. Configuration error?");
        }

        build.getActions().add(new LivingDocBuildAction(build, summary));
        BuildLogger.info("Test results :" + summary.getStatistics());
        int totalWithoutIgnored = summary.getStatistics().totalCount() - summary.getStatistics().ignoredCount();
        int successRate = ( 100 * summary.getStatistics().rightCount() ) / totalWithoutIgnored;
        int failureRate = 100 - successRate;
        if (failureRate > config.getFailureThreshold()) {
            BuildLogger.severe("Too much tests failed !!!!  (" + failureRate + "% [Threshold: "
                + config.getFailureThreshold() + "%])");
            build.setResult(Result.FAILURE);
        } else if (successRate < 100) {
            BuildLogger.warn("Some Tests failed  (" + failureRate + "% [Threshold: " + config.getFailureThreshold() + "%])");
            build.setResult(Result.UNSTABLE);
        }
        return true;
    }

    @Override
    public LivingDocDescriptor getDescriptor () {
        return DESCRIPTOR;
    }

    public boolean isPublishToConfluence () {
        return config.isPublishToConfluence();
    }

    public String getSystemProperties () {
        return systemProperties;
    }

    public void setSystemProperties (String systemProperties) {
        this.systemProperties = systemProperties;
    }

    @Deprecated
    @Override
    public Action getProjectAction (AbstractProject< ? , ? > project) {
        return new LivingDocProjectAction(project);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService () {
        return BuildStepMonitor.NONE;
    }

    public String getFilenamePrefix () {
        return config.getFilenamePrefix();
    }

    public String getConfluenceSpaceKey () {
        return config.getConfluenceSpaceKey();
    }

    public void setConfluenceSpaceKey (String confluenceSpaceKey) {
        config.setConfluenceSpaceKey(confluenceSpaceKey);
    }

    public String getConfluenceSiteTitle () {
        return config.getConfluenceSiteTitle();
    }

    public void setConfluenceSiteTitle (String confluenceSiteTitle) {
        config.setConfluenceSiteTitle(confluenceSiteTitle);
    }

    public void setFilenamePrefix (String filenamePrefix) {
        config.setFilenamePrefix(filenamePrefix);
    }

    public String getTestResultsPattern () {
        return config.getTestResultsPattern();
    }

    public void setTestResultsPattern (String testResultsPattern) {
        config.setTestResultsPattern(testResultsPattern);
    }

    public void setPublishToConfluence (boolean publishToConfluence) {
        config.setPublishToConfluence(publishToConfluence);
    }

    public String getConfluenceUrl () {
        return config.getConfluenceUrl();
    }

    public void setConfluenceUrl (String confluenceUrl) {
        config.setConfluenceUrl(confluenceUrl);
    }

    public int getFailureThreshold () {
        return config.getFailureThreshold();
    }

    public void setFailureThreshold (int failureThreshold) {
        config.setFailureThreshold(failureThreshold);
    }

    private void setSystemProperties () {
        Properties sysProps = System.getProperties();

        try (StringInputStream sis = new StringInputStream(systemProperties)) {
            sysProps.load(sis);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static final class LivingDocDescriptor extends BuildStepDescriptor<Publisher> {

        public LivingDocDescriptor () {
            super(LivingDocPublisher.class);
            load();
        }

        @Override
        public String getDisplayName () {
            return Messages.LivingDocPublisher_livingdoc_publisher_displayname();
        }

        @SuppressWarnings ( "rawtypes" )
        @Override
        public boolean isApplicable (Class type) {
            return true;
        }

        @Override
        public Publisher newInstance (StaplerRequest req, JSONObject formData) throws FormException {
            LivingDocPublisher pub = new LivingDocPublisher();
            req.bindParameters(pub, "livingdoc_reports.");
            return pub;
        }
    }
}
