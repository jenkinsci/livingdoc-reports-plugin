/**
 * Copyright(c) 2009 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or(at your option) any later
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

import info.novatec.testit.livingdoc.Statistics;
import info.novatec.testit.livingdoc.TimeStatistics;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;


public class BuildReportBean implements Serializable {

    public static final Comparator<BuildReportBean> BY_ID = new Comparator<BuildReportBean>() {
        public int compare(BuildReportBean o1, BuildReportBean o2) {
            return Integer.compare(o1.getBuildId(), o2.getBuildId());
        }
    };

    private static final long serialVersionUID = 1L;

    private final int buildId;
    private String externalUrl;
    private final int id;
    private String name;
    private String result;
    private String resultFile;
    private Statistics statistics;
    private TimeStatistics timeStatistics;
    private String xmlReport;

    public BuildReportBean(int id, int buildId) {
        this.id = id;
        this.buildId = buildId;
    }

    public int getBuildId() {
        return buildId;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getResult() {
        return result;
    }

    public String getResultFile() {
        return resultFile;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public TimeStatistics getTimeStatistics() {
        return timeStatistics;
    }

    public String getXmlReport() {
        return xmlReport;
    }

    public boolean hasResult() {
        return StringUtils.isNotEmpty(result) || isResultFileExist();
    }

    public boolean isResultFileExist() {
        return StringUtils.isNotEmpty(resultFile) && new File(resultFile).exists();
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public void setTimeStatistics(TimeStatistics timeStatistics) {
        this.timeStatistics = timeStatistics;
    }

    public void setXmlReport(String xmlReport) {
        this.xmlReport = xmlReport;
    }
}
