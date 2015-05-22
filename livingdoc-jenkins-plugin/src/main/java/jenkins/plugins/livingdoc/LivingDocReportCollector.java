/**
 * Copyright (c) 2009 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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

import static org.apache.commons.lang3.StringUtils.trimToNull;
import hudson.FilePath;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.remoting.RoleChecker;

public class LivingDocReportCollector implements FilePath.FileCallable<SummaryBuildReportBean> {

	private static Logger LOGGER = Logger.getLogger(LivingDocReportCollector.class.getCanonicalName());
	private static final String[] REMOVEABLE_POSTFIXES = { ".html.xml", ".xml", ".html", ".htm" };

	private static final long serialVersionUID = 1L;
	private final FilePath livingDocReportDir;
	private final SummaryBuildReportBean summary;
	private LivingDocPublisherConfig config;

	private int idGenerator = 1;

	public LivingDocReportCollector(FilePath buildDir, int buildId, LivingDocPublisherConfig config,
			Map<String, String> buildVariables) throws IOException, InterruptedException {
		livingDocReportDir = livingDocReportDir(buildDir);
		summary = new SummaryBuildReportBean(buildId);
		this.config = config;
	}

	@Override
	public SummaryBuildReportBean invoke(File workspaceDir, VirtualChannel channel) throws IOException {

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
				if (config.isPublishToConfluence()) {
					publishToConfluence(fileReport);
				}
				summary.addBuildReport(buildReportBean);
			}

			LOGGER.fine("Global statistics : " + summary.getStatistics().toString());
			LOGGER.exiting(getClass().getCanonicalName(), "invoke", summary);
			return summary;
		} catch (InterruptedException ex) {
			throw new IOException("Collecting LivingDoc reports fail", ex);
		}

	}

	private FilePath[] list(File workspaceDir) throws IOException {

		try {
			return new FilePath(workspaceDir).list(config.getTestResultsPattern());
		} catch (InterruptedException ex) {
			throw new IOException(String.format("Listing xml report files from directory : %s", workspaceDir), ex);
		}
	}

	private int nextId() {
		return idGenerator++;
	}

	private String persistResult(int id, XmlReportReader xmlReport) throws IOException, InterruptedException {
		FilePath reportFile = new FilePath(livingDocReportDir, String.format("%d.result", id));
		String results = xmlReport.getResults();
		reportFile.write(results, "UTF-8");

		return reportFile.getRemote();
	}

	private static FilePath livingDocReportDir(FilePath buildDir) throws IOException, InterruptedException {
		FilePath path = new FilePath(buildDir, "livingdoc-reports");
		path.mkdirs();
		return path;
	}

	private void publishToConfluence(FilePath xmlReport) {
		LOGGER.entering(getClass().getCanonicalName(), "publishToConfluence");
		String location = getLocation(xmlReport.getName());
		try {
			LOGGER.fine("Publishing to Confluence : location=" + location + ",Url=" + config.getConfluenceUrl());
			LOGGER.finer("Current system properties:\n" + System.getProperties());
			String normalizedContent = normalizeContent(xmlReport.readToString());

			PostExecutionResultPublisher pub = new PostExecutionResultPublisher(config.getConfluenceUrl(), location,
					normalizedContent);
			pub.specificationDone();
			LOGGER.fine("Publishing to Confluence succeeded");
			BuildLogger.info("Results successfully published to confluence:\n\t\tFile:" + xmlReport.getName());
		} catch (Exception e) {
			LOGGER.severe("Error publishing to Confluence " + e.getMessage());
			BuildLogger.severe("Error publishing results to confluence: " + e.getMessage() + "\n\t\tFile:" + xmlReport.getName()
					+ "\n\t\tLocation:" + location + "\n\t\tConfluence-URL:" + config.getConfluenceUrl());

		}
		LOGGER.exiting(getClass().getCanonicalName(), "publishToConfluence");
	}

	String getLocation(String filename) {
		LOGGER.entering(getClass().getCanonicalName(), "getLocation", filename);
		StringBuilder location = new StringBuilder(filename);
		if (StringUtils.isNotEmpty(config.getFilenamePrefix()) && filename.startsWith(config.getFilenamePrefix())) {
			location.delete(0, config.getFilenamePrefix().length());
		}

		for (String postfix : REMOVEABLE_POSTFIXES) {
			if (location.toString().endsWith(postfix)) {
				int postfixLength = postfix.length();
				int filenameLength = location.length();
				location.delete(filenameLength - postfixLength, filenameLength);
				break;
			}
		}
		if (location.indexOf(config.getConfluenceSiteTitle()) >= 0) {
			int slashIndex = location.indexOf("-");
			slashIndex = location.indexOf("-", slashIndex + 1);
			location.replace(slashIndex, slashIndex + 1, "/");
		} else {
			location.insert(0, "/").insert(0, config.getConfluenceSpaceKey()).insert(0, "-")
					.insert(0, config.getConfluenceSiteTitle());
		}
		LOGGER.exiting(getClass().getCanonicalName(), "getLocation", location);
		return location.toString();
	}

	private String normalizeContent(String original) {
		return StringUtils.toEncodedString(original.getBytes(), Charset.forName("UTF-8"));

	}

	@Override
	public void checkRoles(RoleChecker roleChecker) throws SecurityException {
		// Do nothing
	}

}