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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import hudson.FilePath;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class LivingDocReportCollectorTest {

    private static FilePath buildDir;
    private static File baseDir;

    @BeforeClass
    public static void initBaseDir () throws Exception {

        URL dirUrl = LivingDocReportCollectorTest.class.getResource("LivingDocReportCollector");
        baseDir = new File(dirUrl.toURI());

        File localBuildDir = File.createTempFile("junit", "builddir");
        FileUtils.deleteQuietly(localBuildDir);
        FileUtils.forceMkdir(localBuildDir);
        buildDir = new FilePath(localBuildDir);
    }

    @AfterClass
    public static void deleteBuildDir () throws Exception {
        buildDir.deleteRecursive();
    }

    @Test ( expected = BuildException.class )
    public void readingXmlReportsFromUnknowDirectoryShouldThrowException () throws Exception {
        LivingDocReportCollector collector = newReportCollector();
        collector.invoke(new File("unknow-dir/unknow-subdir"), null);
    }

    @Test ( expected = IOException.class )
    public void readingBadXmlReportFileShouldThrowException () throws Exception {

        LivingDocReportCollector collector = newReportCollector("**/livingdoc-reports/**/report*.bad");

        collector.invoke(baseDir, null);
    }

    @Test
    public void canReadXmlReports () throws Exception {

        LivingDocReportCollector collector = newReportCollector();
        SummaryBuildReportBean summary = collector.invoke(baseDir, null);
        assertFalse(summary.hasNoReports());
        assertEquals(4, summary.getBuildReports().size());
    }

    @Test
    public void getLocation () throws IOException, InterruptedException {
        String[][] values =
            new String[][] {
                    { "prefix-Confluence Site Title-KEY-LL - My Executable Spec.html", "prefix-", "Confluence Site Title",
                            "KEY", "Confluence Site Title-KEY/LL - My Executable Spec" },
                    { "prefix-LL - My Executable Spec.html", "prefix-", "Confluence Site Title", "KEY",
                            "Confluence Site Title-KEY/LL - My Executable Spec" },
                    { "prefix-LL - My Executable Spec.html.xml", "prefix-", "Confluence Site Title", "KEY",
                            "Confluence Site Title-KEY/LL - My Executable Spec" },
                    { "prefix-LL - My Executable Spec.htm", "prefix-", "Confluence Site Title", "KEY",
                            "Confluence Site Title-KEY/LL - My Executable Spec" },
                    { "prefix-LL - My Executable Spec.xml", "prefix-", "Confluence Site Title", "KEY",
                            "Confluence Site Title-KEY/LL - My Executable Spec" },
                    { "LL - My Executable Spec.xml", null, "Confluence Site Title", "KEY",
                            "Confluence Site Title-KEY/LL - My Executable Spec" } };

        FilePath path = new FilePath(File.createTempFile("temp", "temp").getParentFile());
        for (int i = 0; i < values.length; i ++ ) {
            LivingDocPublisherConfig config = new LivingDocPublisherConfig();
            config.setFilenamePrefix(values[i][1]);
            config.setConfluenceSiteTitle(values[i][2]);
            config.setConfluenceSpaceKey(values[i][3]);
            LivingDocReportCollector collector = new LivingDocReportCollector(path, i, config);
            String expectedLocation = values[i][4];
            String currLocation = collector.getLocation(values[i][0]);
            assertEquals(expectedLocation, currLocation);

        }
    }

    private LivingDocReportCollector newReportCollector () throws Exception {
        return newReportCollector("**/livingdoc-reports/**/report*.xml");
    }

    private LivingDocReportCollector newReportCollector (String testResultsPattern) throws Exception {
        LivingDocPublisherConfig config = new LivingDocPublisherConfig();
        config.setTestResultsPattern(testResultsPattern);
        return new LivingDocReportCollector(buildDir, 1, config);
    }
}
