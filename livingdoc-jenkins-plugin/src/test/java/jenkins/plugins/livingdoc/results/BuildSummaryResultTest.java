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
package jenkins.plugins.livingdoc.results;

import jenkins.plugins.livingdoc.results.BuildSummaryResult;

import org.junit.Test;

import com.greenpepper.Statistics;

import static org.junit.Assert.*;

public class BuildSummaryResultTest {

	@Test
	public void computeSuccessRate() {
		BuildSummaryResult result = new BuildSummaryResult(null, null);

		Statistics stats = new Statistics(10, 0, 0, 0);
		assertEquals(100, result.getSuccessRate(stats));

		stats = new Statistics(8, 2, 0, 0);
		assertEquals(80, result.getSuccessRate(stats));

		stats = new Statistics(6, 3, 0, 0);
		assertEquals(66, result.getSuccessRate(stats));
	}
}
