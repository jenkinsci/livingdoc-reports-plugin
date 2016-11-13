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
    private List<BuildReportBean> buildReports = new ArrayList<BuildReportBean>();
    private final BuildReportBean summary;

    public SummaryBuildReportBean(int buildId) {
        this.buildId = buildId;

        summary = new BuildReportBean(0, buildId);
        summary.setStatistics(new Statistics());
        summary.setTimeStatistics(new TimeStatistics());
    }

    public void addBuildReport(BuildReportBean report) {
        buildReports.add(report);

        summary.getStatistics().tally(report.getStatistics());
        summary.getTimeStatistics().tally(report.getTimeStatistics());
    }

    public BuildReportBean findBuildById(int id) {

        for (BuildReportBean buildReport : buildReports) {
            if (buildReport.getId() == id) {
                return buildReport;
            }
        }

        return null;
    }

    public int getBuildId() {
        return buildId;
    }

    public List<BuildReportBean> getBuildReports() {
        Collections.sort(buildReports, BuildReportBean.BY_ID);
        return buildReports;
    }

    public BuildReportBean getBuildSummary() {
        return summary;
    }

    public Statistics getStatistics() {
        return summary.getStatistics();
    }

    public TimeStatistics getTimeStatistics() {
        return summary.getTimeStatistics();
    }

    public boolean hasNoReports() {
        return buildReports == null || buildReports.isEmpty();
    }

    public boolean hasReports() {
        return ! hasNoReports();
    }
    
    public SummaryBuildReportBean withoutXmlReports(){
        for(BuildReportBean report : buildReports){
            report.setXmlReport(null);
        }
        return this;
    }
}
