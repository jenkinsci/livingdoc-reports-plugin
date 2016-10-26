package jenkins.plugins.livingdoc;

import info.novatec.testit.livingdoc.Statistics;
import info.novatec.testit.livingdoc.TimeStatistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SummaryBuildReportBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int buildId;
    private final BuildReportBean summary;
    private List<BuildReportBean> buildReports = new ArrayList<BuildReportBean>();

    public SummaryBuildReportBean (int buildId) {
        this.buildId = buildId;

        summary = new BuildReportBean(0, buildId);
        summary.setStatistics(new Statistics());
        summary.setTimeStatistics(new TimeStatistics());
    }

    public int getBuildId () {
        return buildId;
    }

    public Statistics getStatistics () {
        return summary.getStatistics();
    }

    public TimeStatistics getTimeStatistics () {
        return summary.getTimeStatistics();
    }

    public BuildReportBean getBuildSummary () {
        return summary;
    }

    public boolean hasNoReports () {
        return buildReports == null || buildReports.isEmpty();
    }

    public boolean hasReports () {
        return !hasNoReports();
    }
    public List<BuildReportBean> getBuildReports () {
        Collections.sort(buildReports, BuildReportBean.BY_ID);
        return buildReports;
    }

    public void addBuildReport (BuildReportBean report) {
        buildReports.add(report);

        summary.getStatistics().tally(report.getStatistics());
        summary.getTimeStatistics().tally(report.getTimeStatistics());
    }

    public BuildReportBean findBuildById (int id) {

        for (BuildReportBean buildReport : buildReports) {
            if (buildReport.getId() == id) {
                return buildReport;
            }
        }

        return null;
    }
}
