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
import hudson.util.FormValidation;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;


public class LivingDocReportsPublisher extends Recorder {

    // Needed to succeed field check
    public String testResultsPattern = null;
    public int failureThreshold;

    public ConfluenceConfig confluenceConfig;
    public boolean publishToConfluence = false;
    @Extension
    public static final LivingDocDescriptor DESCRIPTOR = new LivingDocDescriptor();

    public static class LivingDocDescriptor extends BuildStepDescriptor<Publisher> {

        private static final int DEFAULT_FAILURE_THRESHOLD = 20;

        public LivingDocDescriptor () {
            super(LivingDocReportsPublisher.class);
            load();
        }

        @Override
        public String getDisplayName () {
            return Messages.LivingDocPublisher_livingdoc_publisher_displayname();
        }

        @Override
        public boolean isApplicable (Class< ? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public Publisher newInstance (StaplerRequest staplerRequest, JSONObject formData) throws FormException {
            LivingDocReportsPublisher publisher = staplerRequest.bindJSON(LivingDocReportsPublisher.class, formData);
            return publisher;
        }

        public FormValidation doCheckTestResultsPattern (@QueryParameter final String testResultsPattern) {
            FormValidation validationResult;

            if (StringUtils.isNotEmpty(testResultsPattern)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error("Please provide a valid test results pattern");
            }

            return validationResult;
        }

        public int getDefaultFailureThreshold () {
            return DEFAULT_FAILURE_THRESHOLD;
        }

    }

    @DataBoundConstructor
    public LivingDocReportsPublisher (String testResultsPattern, int failureThreshold, ConfluenceConfig confluenceConfig) {
        super();
        this.testResultsPattern = StringUtils.trimToNull(testResultsPattern);
        this.failureThreshold = failureThreshold;
        this.confluenceConfig = confluenceConfig;
        this.publishToConfluence = ( confluenceConfig != null );

    }

    @Override
    public boolean perform (AbstractBuild< ? , ? > build, Launcher launcher, BuildListener listener)
        throws InterruptedException, IOException {
        BuildLogger.intialize(listener);
        FilePath workspaceDir = build.getWorkspace();
        FilePath buildDir = new FilePath(build.getRootDir());

        SummaryBuildReportBean summary =
            workspaceDir
                .act(new ReportCollector(buildDir, build.getNumber(), build.getBuildVariables(), testResultsPattern));

        if (summary.hasNoReports()) {
            throw new AbortException("No LivingDoc test report files were found. Configuration error?");
        }

        if (confluenceConfig != null) {
            ConfluencePublisher confluencePublisher = new ConfluencePublisher(confluenceConfig);
            confluencePublisher.publishToConfluence(summary);
        }
        build.addAction(new LivingDocBuildAction(build, summary));
        BuildLogger.info("Test results :" + summary.getStatistics());
        computeBuildResult(summary, build);
        return true;
    }

    @Override
    public LivingDocDescriptor getDescriptor () {
        return DESCRIPTOR;
    }

    @Override
    public Action getProjectAction (AbstractProject< ? , ? > project) {
        return new LivingDocProjectAction(project);
    }

    public BuildStepMonitor getRequiredMonitorService () {
        return BuildStepMonitor.NONE;
    }

    private void computeBuildResult (SummaryBuildReportBean summary, AbstractBuild< ? , ? > build) {
        int totalWithoutIgnored = summary.getStatistics().totalCount() - summary.getStatistics().ignoredCount();
        int successRate = ( 100 * summary.getStatistics().rightCount() ) / totalWithoutIgnored;
        int failureRate = 100 - successRate;
        if (failureRate > failureThreshold) {
            BuildLogger.severe("Too much tests failed !!!!  (" + failureRate + "% [Threshold: " + failureThreshold + "%])");
            build.setResult(Result.FAILURE);
        } else if (successRate < 100) {
            BuildLogger.warn("Some Tests failed  (" + failureRate + "% [Threshold: " + failureThreshold + "%])");
            build.setResult(Result.UNSTABLE);
        }

    }

}
