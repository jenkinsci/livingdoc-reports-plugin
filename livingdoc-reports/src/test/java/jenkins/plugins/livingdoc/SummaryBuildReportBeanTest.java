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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import info.novatec.testit.livingdoc.Statistics;
import info.novatec.testit.livingdoc.TimeStatistics;

import org.junit.Test;

public class SummaryBuildReportBeanTest {

	@Test
	public void statisticsShouldBeAggregated() {

		SummaryBuildReportBean summary = new SummaryBuildReportBean(0);

		summary.addBuildReport(newBuildReport(1, new Statistics(4, 3, 2, 1), new TimeStatistics(2000, 500)));
		summary.addBuildReport(newBuildReport(2, new Statistics(5, 1, 1, 0), new TimeStatistics(1500, 200)));

		assertFalse(summary.hasNoReports());
		assertEquals(new Statistics(9, 4, 3, 1), summary.getBuildSummary().getStatistics());
		assertEquals(new TimeStatistics(3500, 700), summary.getBuildSummary().getTimeStatistics());
	}

	@Test
	public void canFindBuildReportById() {

		SummaryBuildReportBean summary = new SummaryBuildReportBean(0);

		summary.addBuildReport(newBuildReport(1, new Statistics(4, 3, 2, 1), new TimeStatistics(2000, 500)));
		summary.addBuildReport(newBuildReport(2, new Statistics(5, 1, 1, 0), new TimeStatistics(1500, 200)));

		assertNotNull(summary.findBuildById(1));
		assertEquals(new Statistics(4, 3, 2, 1), summary.findBuildById(1).getStatistics());

		assertNotNull(summary.findBuildById(2));
		assertEquals(new Statistics(5, 1, 1, 0), summary.findBuildById(2).getStatistics());

		assertNull(summary.findBuildById(999));
	}

	private BuildReportBean newBuildReport(int id, Statistics statistics, TimeStatistics timeStatistics) {
		BuildReportBean report = new BuildReportBean(id, 0);
		report.setStatistics(statistics);
		report.setTimeStatistics(timeStatistics);
		return report;
	}
}
