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

import info.novatec.testit.Statistics;
import info.novatec.testit.TimeStatistics;

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
        return buildReports.isEmpty();
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
