package jenkins.plugins.livingdoc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.kohsuke.stapler.StaplerProxy;

import hudson.model.Action;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.Run;
import jenkins.plugins.livingdoc.results.BuildSummaryResult;
import jenkins.tasks.SimpleBuildStep;


public class LivingDocBuildAction implements HealthReportingAction, StaplerProxy, SimpleBuildStep.LastBuildAction {

    private static Logger LOGGER = Logger.getLogger(LivingDocBuildAction.class.getCanonicalName());

    private final Run< ? , ? > run;
    private final SummaryBuildReportBean summary;

    List<LivingDocProjectAction> projectActions;

    public LivingDocBuildAction(Run< ? , ? > run, SummaryBuildReportBean summary) {
        this.run = run;
        this.summary = summary;

        projectActions = new ArrayList<LivingDocProjectAction>();
        if (run != null && run.getParent() != null) {
            projectActions.add(new LivingDocProjectAction(run.getParent()));
        }else{
            LOGGER.warning("Run or parent not definied !");
        }
    }

    public SummaryBuildReportBean getSummary() {
        return summary;
    }

    public HealthReport getBuildHealth() {

        BuildSummaryResult result = getResult();

        return new HealthReport(result.getSuccessRate(), Messages._LivingDocBuildAction_description(result.getStatistics()));
    }

    public String getIconFileName() {
        return ResourceUtils.BIG_ICON_URL;
    }

    public String getUrlName() {
        return ResourceUtils.PLUGIN_CONTEXT_BASE;
    }

    public String getDisplayName() {
        return ResourceUtils.LIVINGDOC_NAME;
    }

    public Object getTarget() {
        return getResult();
    }

    private BuildSummaryResult getResult() {
        return new BuildSummaryResult(run, summary);
    }

    @Override
    public Collection< ? extends Action> getProjectActions() {
        return projectActions;
    }
}
