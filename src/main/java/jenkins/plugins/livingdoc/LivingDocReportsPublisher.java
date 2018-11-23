package jenkins.plugins.livingdoc;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;


@SuppressWarnings({"unchecked" ,"PMD.GuardLogStatementJavaUtil"})
public class LivingDocReportsPublisher extends Recorder implements SimpleBuildStep {

    @Extension
    @Symbol("livingdoc")
    public static class LivingDocDescriptor extends BuildStepDescriptor<Publisher> {

        private static final int DEFAULT_FAILURE_THRESHOLD = 20;

        public LivingDocDescriptor() {
            super(LivingDocReportsPublisher.class);
            LOGGER.info("Loading LivingDoc-Descriptor...");
            load();
        }

        public FormValidation doCheckSut(@QueryParameter final String sut) {
            FormValidation validationResult;

            if (StringUtils.isNotEmpty(sut)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error("Please provide a valid SUT(System Under Test)");
            }
            return validationResult;
        }

        public FormValidation doCheckTestResultsPattern(@QueryParameter final String testResultsPattern) {
            FormValidation validationResult;

            if (StringUtils.isNotEmpty(testResultsPattern)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error("Please provide a valid test results pattern");
            }

            return validationResult;
        }

        public int getDefaultFailureThreshold() {
            return DEFAULT_FAILURE_THRESHOLD;
        }

        @Override
        public String getDisplayName() {
            return Messages.LivingDocPublisher_livingdoc_publisher_displayname();
        }

        @Override
        public boolean isApplicable(Class< ? extends AbstractProject> jobType) {
            LOGGER.fine(String.format("Checking support for job type %s", jobType.getName()));
            return true;
        }

        @Override
        public Publisher newInstance(StaplerRequest staplerRequest, JSONObject formData) throws FormException {
            if (staplerRequest != null) {
                LivingDocReportsPublisher publisher = staplerRequest.bindJSON(LivingDocReportsPublisher.class, formData);
                return publisher;
            } else {
                throw new FormException("Could not intialize publisher", "testResultsPattern");
            }

        }

    }

    private static final Logger LOGGER = Logger.getLogger(LivingDocReportsPublisher.class.getCanonicalName());
    public ConfluenceConfig confluenceConfig;

    public int failureThreshold;
    public boolean publishToConfluence ;

    // Needed to succeed field check
    public String testResultsPattern ;

    @DataBoundConstructor
    public LivingDocReportsPublisher(String testResultsPattern, int failureThreshold, ConfluenceConfig confluenceConfig) {
        super();
        this.testResultsPattern = StringUtils.trimToNull(testResultsPattern);
        this.failureThreshold = failureThreshold;
        this.confluenceConfig = confluenceConfig;
        this.publishToConfluence = ( confluenceConfig != null );

    }

    private void computeBuildResult(SummaryBuildReportBean summary, Run< ? , ? > run) {
        int totalWithoutIgnored = summary.getStatistics().totalCount() - summary.getStatistics().ignoredCount();
        int successRate = totalWithoutIgnored > 0 ? ( 100 * summary.getStatistics().rightCount() ) / totalWithoutIgnored : 0;
        int failureRate = 100 - successRate;
        if (failureRate > failureThreshold) {
            BuildLogger.severe("Too much tests failed !!!! (" + failureRate + "% [Threshold: " + failureThreshold + "%])");
            run.setResult(Result.FAILURE);
        } else if (successRate < 100) {
            BuildLogger.warn("Some Tests failed (" + failureRate + "% [Threshold: " + failureThreshold + "%])");
            run.setResult(Result.UNSTABLE);
        }

    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public void perform(Run< ? , ? > run, FilePath filePath, Launcher launcher, TaskListener taskListener)
        throws InterruptedException, IOException {
        BuildLogger.intialize(taskListener);
        if (filePath != null) {
            FilePath runDir = new FilePath(run.getRootDir());

            SummaryBuildReportBean summary = filePath.act(new ReportCollector(runDir, run.getNumber(), testResultsPattern));

            if (summary != null && summary.hasReports()) {

                if (confluenceConfig != null) {
                    ConfluencePublisher confluencePublisher = new ConfluencePublisher(confluenceConfig);
                    confluencePublisher.publishToConfluence(summary);
                }
                LivingDocBuildAction action = run.getAction(LivingDocBuildAction.class);
                if (action == null) {
                    action = new LivingDocBuildAction(run, summary.withoutXmlReports());

                }

                run.addAction(action);
                BuildLogger.info("Test results :" + summary.getStatistics());
                computeBuildResult(summary, run);
            } else {
                BuildLogger.warn("No test reports found.");
            }
        } else {
            throw new AbortException("Please configure workspace folder(missing)");
        }
    }

}
