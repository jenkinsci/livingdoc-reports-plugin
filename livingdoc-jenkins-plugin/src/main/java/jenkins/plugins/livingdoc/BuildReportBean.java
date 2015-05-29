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

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import com.greenpepper.Statistics;
import com.greenpepper.TimeStatistics;


public class BuildReportBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Comparator<BuildReportBean> BY_ID = new Comparator<BuildReportBean>() {
        @Override
        public int compare (BuildReportBean o1, BuildReportBean o2) {
            return Integer.valueOf(o1.getBuildId()).compareTo(o2.getBuildId());
        }
    };

    private final int buildId;
    private final int id;
    private String name;
    private String externalUrl;
    private Statistics statistics;
    private TimeStatistics timeStatistics;
    private String result;
    private String resultFile;

    public BuildReportBean (int id, int buildId) {
        this.id = id;
        this.buildId = buildId;
    }

    public int getId () {
        return id;
    }

    public int getBuildId () {
        return buildId;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getExternalUrl () {
        return externalUrl;
    }

    public void setExternalUrl (String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public Statistics getStatistics () {
        return statistics;
    }

    public void setStatistics (Statistics statistics) {
        this.statistics = statistics;
    }

    public TimeStatistics getTimeStatistics () {
        return timeStatistics;
    }

    public void setTimeStatistics (TimeStatistics timeStatistics) {
        this.timeStatistics = timeStatistics;
    }

    public boolean hasResult () {
        return StringUtils.isNotEmpty(result) || isResultFileExist();
    }

    public String getResult () {
        return result;
    }

    public void setResult (String result) {
        this.result = result;
    }

    public String getResultFile () {
        return resultFile;
    }

    public void setResultFile (String resultFile) {
        this.resultFile = resultFile;
    }

    public boolean isResultFileExist () {
        return StringUtils.isNotEmpty(resultFile) && new File(resultFile).exists();
    }
}
