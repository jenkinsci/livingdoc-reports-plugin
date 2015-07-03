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
import static org.junit.Assert.assertTrue;
import hudson.FilePath;
import info.novatec.testit.livingdoc.Statistics;
import info.novatec.testit.livingdoc.TimeStatistics;

import java.io.File;
import java.net.URL;

import org.junit.Test;


public class XmlReportReaderTest {

    @Test
    public void readXmlSuccessfully () throws Exception {

        XmlReportReader reader = new XmlReportReader();

        URL xml = getClass().getResource("/jenkins/plugins/livingdoc/XmlReportReaderTest.xml");
        reader.parse(new FilePath(new File(xml.toURI())));

        assertEquals("http://server/XmlReportReaderTest", reader.getExternalLink());
        assertEquals("html-result", reader.getResults());
        assertTrue(reader.hasGlobalException());
        assertEquals(new Statistics(10, 3, 1, 7), reader.getStatistics());
        assertEquals(new TimeStatistics(336, 195), reader.getTimeStatistics());
    }
}
