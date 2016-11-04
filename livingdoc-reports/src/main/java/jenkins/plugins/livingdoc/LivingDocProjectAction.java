package jenkins.plugins.livingdoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.Actionable;
import hudson.model.Job;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ProminentProjectAction;
import hudson.model.Run;
import hudson.model.StringParameterValue;
import hudson.util.Graph;
import jenkins.plugins.livingdoc.chart.ProjectSummaryChart;


public class LivingDocProjectAction extends Actionable implements ProminentProjectAction {

    public static final String LD_CHART_MAX_COUNT_BUILDS = "LD_CHART_MAX_COUNT_BUILDS";

    private final Job< ? , ? > project;

    public LivingDocProjectAction(Job< ? , ? > job) {
        super();

        this.project = job;
    }

    public void doIndex(StaplerRequest request, StaplerResponse response) throws IOException {

        Run< ? , ? > build = findLatestLivingDocRun();

        if (build == null) {
            response.sendRedirect2("nodata");
        } else {
            int buildId = build.getNumber();
            response.sendRedirect2(String.format("../%s/" + ResourceUtils.PLUGIN_CONTEXT_BASE, buildId));
        }
    }

    private LivingDocBuildAction findBuildAction(Run< ? , ? > run) {
        return run.getAction(LivingDocBuildAction.class);
    }

    private Run< ? , ? > findLatestLivingDocRun() {

        for (Run< ? , ? > run = project.getLastBuild(); run != null; run = run.getPreviousBuild()) {

            if (findBuildAction(run) != null) {
                return run;
            }
        }

        return null;
    }

    private List<SummaryBuildReportBean> getAllLivingDocBuildSummaries() {

        List<SummaryBuildReportBean> summaries = new ArrayList<SummaryBuildReportBean>();
        int maxCountBuilds = getChartMaxCountBuilds();
        int count = 0;
        boolean mayAddEntries = maxCountBuilds == - 1 || count <= maxCountBuilds;
        for (Run< ? , ? > run = project.getLastBuild(); run != null && mayAddEntries; run = run.getPreviousBuild()) {

            LivingDocBuildAction buildAction = findBuildAction(run);

            if (buildAction != null) {
                summaries.add(buildAction.getSummary());
                if (maxCountBuilds > - 1) {
                    count ++ ;
                    mayAddEntries = count < maxCountBuilds;
                }
            }
        }

        Collections.reverse(summaries);

        return summaries;
    }

    private int getChartMaxCountBuilds() {

        int maxCountBuilds = - 1;

        Run< ? , ? > lastRun = project.getLastBuild();
        if (lastRun != null) {
            ParametersAction action = lastRun.getAction(ParametersAction.class);
            if (action != null) {
                ParameterValue paramValue = action.getParameter(LD_CHART_MAX_COUNT_BUILDS);
                if (paramValue != null && paramValue instanceof StringParameterValue) {
                    String stringValue = ( ( StringParameterValue ) paramValue ).value;
                    maxCountBuilds = Integer.parseInt(stringValue);
                }
            }
        }

        return maxCountBuilds;

    }

    public String getDisplayName() {
        return ResourceUtils.LIVINGDOC_NAME;
    }

    public Graph getGraph() {
        Calendar timestamp = project.getLastCompletedBuild().getTimestamp();
        return new ProjectSummaryChart(timestamp, getAllLivingDocBuildSummaries());
    }

    public String getIconFileName() {
        return ResourceUtils.BIG_ICON_URL;
    }

    public Job< ? , ? > getProject() {
        return project;
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    public List<SummaryBuildReportBean> getSummaries() {
        return getAllLivingDocBuildSummaries();
    }

    public String getUrlName() {
        return ResourceUtils.PLUGIN_CONTEXT_BASE;
    }

    public boolean hasSummaries() {
        return getAllLivingDocBuildSummaries().size() > 0;
    }
}
