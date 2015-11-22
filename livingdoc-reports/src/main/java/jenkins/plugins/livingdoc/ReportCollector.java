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

import static org.apache.commons.lang3.StringUtils.trimToNull;
import hudson.FilePath;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.jenkinsci.remoting.RoleChecker;


public class ReportCollector implements FilePath.FileCallable<SummaryBuildReportBean> {

    private static Logger LOGGER = Logger.getLogger(ReportCollector.class.getCanonicalName());

    private static final long serialVersionUID = 1L;
    private final FilePath livingDocReportDir;
    private final SummaryBuildReportBean summary;
    private String testResultsPattern = null;

    private int idGenerator = 1;

    public ReportCollector (FilePath buildDir, int buildId, Map<String, String> buildVariables, String testResultsPattern)
        throws IOException, InterruptedException {
        livingDocReportDir = livingDocReportDir(buildDir);
        summary = new SummaryBuildReportBean(buildId);
        this.testResultsPattern = testResultsPattern;
    }

    public SummaryBuildReportBean invoke (File workspaceDir, VirtualChannel channel) throws IOException {

        LOGGER.entering(getClass().getCanonicalName(), "invoke");
        try {
            for (FilePath fileReport : list(workspaceDir)) {
                XmlReportReader xmlReport = new XmlReportReader().parse(fileReport);

                BuildReportBean buildReportBean = new BuildReportBean(nextId(), summary.getBuildId());

                buildReportBean.setName(trimToNull(xmlReport.getName()));
                buildReportBean.setExternalUrl(trimToNull(xmlReport.getExternalLink()));
                buildReportBean.setStatistics(xmlReport.getStatistics());
                buildReportBean.setTimeStatistics(xmlReport.getTimeStatistics());

                buildReportBean.setResultFile(persistResult(buildReportBean.getId(), xmlReport));
                summary.addBuildReport(buildReportBean);
            }

            LOGGER.fine("Global statistics : " + summary.getStatistics().toString());
            LOGGER.exiting(getClass().getCanonicalName(), "invoke", summary);
            return summary;
        } catch (InterruptedException ex) {
            throw new IOException("Collecting LivingDoc reports fail", ex);
        }

    }

    private FilePath[] list (File workspaceDir) throws IOException {

        try {
            return new FilePath(workspaceDir).list(testResultsPattern);
        } catch (InterruptedException ex) {
            throw new IOException(String.format("Listing xml report files from directory : %s", workspaceDir), ex);
        }
    }

    private int nextId () {
        return idGenerator ++ ;
    }

    private String persistResult (int id, XmlReportReader xmlReport) throws IOException, InterruptedException {
        FilePath reportFile = new FilePath(livingDocReportDir, String.format("%d.result", id));
        String results = xmlReport.getResults();
        reportFile.write(results, "UTF-8");

        return reportFile.getRemote();
    }

    private static FilePath livingDocReportDir (FilePath buildDir) throws IOException, InterruptedException {
        FilePath path = new FilePath(buildDir, "livingdoc-reports");
        path.mkdirs();
        return path;
    }

    public void checkRoles (RoleChecker arg0) throws SecurityException {
    }

}
