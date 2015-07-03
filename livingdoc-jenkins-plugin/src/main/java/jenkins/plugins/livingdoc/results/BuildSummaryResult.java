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
package jenkins.plugins.livingdoc.results;

import hudson.model.AbstractBuild;
import info.novatec.testit.livingdoc.Statistics;
import info.novatec.testit.livingdoc.TimeStatistics;

import java.io.Serializable;
import java.util.List;

import jenkins.plugins.livingdoc.BuildReportBean;
import jenkins.plugins.livingdoc.SummaryBuildReportBean;


public class BuildSummaryResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final AbstractBuild< ? , ? > build;
    private final SummaryBuildReportBean summary;

    public BuildSummaryResult (AbstractBuild< ? , ? > build, SummaryBuildReportBean summary) {
        this.build = build;
        this.summary = summary;
    }

    public AbstractBuild< ? , ? > getBuild () {
        return build;
    }

    public SummaryBuildReportBean getSummary () {
        return summary;
    }

    public Statistics getStatistics () {
        return summary.getStatistics();
    }

    public TimeStatistics getTimeStatistics () {
        return summary.getTimeStatistics();
    }

    public List<BuildReportBean> getReports () {
        return summary.getBuildReports();
    }

    public Object getDynamic (String token) {
        return new BuildResult(build, summary.findBuildById(Integer.parseInt(token)));
    }

    public int getSuccessRate () {
        return getSuccessRate(summary.getStatistics());
    }

    public int getSuccessRate (Statistics stats) {
        return ( int ) ( ( stats.rightCount() / ( double ) ( stats.totalCount() - stats.ignoredCount() ) ) * 100 );
    }
}
