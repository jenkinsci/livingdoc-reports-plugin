/**
 * Copyright(c) 2009 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 *(at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */
package jenkins.plugins.livingdoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import hudson.FilePath;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReportCollectorTest {

	private static FilePath buildDir;
	private static File baseDir;

	@BeforeClass
	public static void initBaseDir() throws IOException, URISyntaxException {

		URL dirUrl = ReportCollectorTest.class
				.getResource("ReportCollector");
		baseDir = new File(dirUrl.toURI());

		File localBuildDir = File.createTempFile("junit", "builddir");
		FileUtils.deleteQuietly(localBuildDir);
		FileUtils.forceMkdir(localBuildDir);
		buildDir = new FilePath(localBuildDir);
	}

	@AfterClass
	public static void deleteBuildDir() throws IOException, InterruptedException {
		buildDir.deleteRecursive();
	}

	@Test(expected = BuildException.class)
	public void readingXmlReportsFromUnknowDirectoryShouldThrowException() throws IOException, InterruptedException  {
		ReportCollector collector = newReportCollector();
		collector.invoke(new File("unknow-dir/unknow-subdir"), null);
	}

	@Test(expected = IOException.class)
	public void readingBadXmlReportFileShouldThrowException() throws IOException, InterruptedException  {

		ReportCollector collector = newReportCollector("**/livingdoc-reports/**/report*.bad");

		collector.invoke(baseDir, null);
	}

	@Test
	public void canReadXmlReports() throws IOException, InterruptedException {

		ReportCollector collector = newReportCollector();
		SummaryBuildReportBean summary = collector.invoke(baseDir, null);
		assertFalse(summary.hasNoReports());
		assertEquals(4, summary.getBuildReports().size());
	}

	

	private ReportCollector newReportCollector() throws IOException, InterruptedException {
		return newReportCollector("**/livingdoc-reports/**/report*.xml");
	}

	private ReportCollector newReportCollector(
			String testResultsPattern) throws IOException, InterruptedException  {
		return new ReportCollector(buildDir, 1, testResultsPattern);
	}
}