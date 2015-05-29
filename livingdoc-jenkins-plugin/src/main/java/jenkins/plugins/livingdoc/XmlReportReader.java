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

import hudson.FilePath;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.ctc.wstx.stax.WstxInputFactory;
import com.greenpepper.Statistics;
import com.greenpepper.TimeStatistics;


public class XmlReportReader {

    private static final XMLInputFactory XIF = XMLInputFactory.newInstance();

    static {
        if (XIF instanceof WstxInputFactory) {
            WstxInputFactory wstxInputfactory = ( WstxInputFactory ) XIF;
            wstxInputfactory.configureForLowMemUsage();
            wstxInputfactory.configureForSpeed();
        }

        XIF.setProperty(XMLInputFactory.IS_VALIDATING, false);
        XIF.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XIF.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        XIF.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    }

    private String name = "";
    private String externalLink = "";
    private String results = "";
    private String globalException = "";
    private MutableStatistics statistics = new MutableStatistics();
    private MutableTimeStatistics timeStatistics = new MutableTimeStatistics();

    public String getName () {
        return name;
    }

    public String getExternalLink () {
        return externalLink;
    }

    public String getResults () {
        return results;
    }

    public boolean hasGlobalException () {
        return StringUtils.isNotEmpty(getGlobalException());
    }

    public String getGlobalException () {
        return globalException;
    }

    public Statistics getStatistics () {
        return statistics.toImmutableStatistics();
    }

    public TimeStatistics getTimeStatistics () {
        return timeStatistics.toImmutableTimeStatistics();
    }

    public XmlReportReader parse (FilePath fileReport) throws IOException {

        this.name = getFilenameWithoutExtension(fileReport);

        FileReader fileReader = null;
        XMLEventReader xmlEventReader = null;

        try {
            fileReader = new FileReader(new File(fileReport.toURI()));
            xmlEventReader = XIF.createXMLEventReader(fileReader);

            while (xmlEventReader.hasNext()) {
                final XMLEvent event = xmlEventReader.nextEvent();

                if (event.isStartElement()) {
                    final String localPart = event.asStartElement().getName().getLocalPart();

                    if (localPart.equals("external-link")) {
                        this.externalLink = readString(xmlEventReader.nextEvent());
                        continue;
                    }

                    if (localPart.equals("success")) {
                        int success = readInteger(xmlEventReader.nextEvent());
                        this.statistics.setSuccess(success);
                        continue;
                    }

                    if (localPart.equals("failure")) {
                        int failure = readInteger(xmlEventReader.nextEvent());
                        this.statistics.setFailure(failure);
                        continue;
                    }

                    if (localPart.equals("error")) {
                        int error = readInteger(xmlEventReader.nextEvent());
                        this.statistics.setError(error);
                        continue;
                    }

                    if (localPart.equals("ignored")) {
                        int ignored = readInteger(xmlEventReader.nextEvent());
                        this.statistics.setIgnored(ignored);
                        continue;
                    }

                    if (localPart.equals("total")) {
                        int total = readInteger(xmlEventReader.nextEvent());
                        this.timeStatistics.setTotal(total);
                        continue;
                    }

                    if (localPart.equals("execution")) {
                        int execution = readInteger(xmlEventReader.nextEvent());
                        this.timeStatistics.setExecution(execution);
                        continue;
                    }

                    if (localPart.equals("results")) {
                        this.results = readString(xmlEventReader.nextEvent());
                    }

                    if (localPart.equals("global-exception")) {
                        this.globalException = readString(xmlEventReader.nextEvent());
                    }
                }
            }
        } catch (Exception ex) {
            throw new IOException(String.format("Cannot read xml report file %s", fileReport), ex);
        } finally {
            IOUtils.closeQuietly(fileReader);
            closeQuietly(xmlEventReader);
        }

        return this;
    }

    private static void closeQuietly (XMLEventReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                // ignore
            }
        }
    }

    private static int readInteger (XMLEvent event) {
        return Integer.parseInt(readString(event));
    }

    private static String readString (XMLEvent event) {
        if (event.isCharacters()) {
            return event.asCharacters().getData();
        }
        throw new IllegalArgumentException("XMLEvent is not a Characters");
    }

    private String getFilenameWithoutExtension (FilePath file) {
        return FilenameUtils.removeExtension(file.getName());
    }

    private static class MutableStatistics {

        private int success;
        private int failure;
        private int error;
        private int ignored;

        public void setSuccess (int success) {
            this.success = success;
        }

        public void setFailure (int failure) {
            this.failure = failure;
        }

        public void setError (int error) {
            this.error = error;
        }

        public void setIgnored (int ignored) {
            this.ignored = ignored;
        }

        public Statistics toImmutableStatistics () {
            return new Statistics(success, failure, error, ignored);
        }
    }

    private static class MutableTimeStatistics {

        private int execution;
        private int total;

        public void setExecution (int execution) {
            this.execution = execution;
        }

        public void setTotal (int total) {
            this.total = total;
        }

        public TimeStatistics toImmutableTimeStatistics () {
            return new TimeStatistics(total, execution);
        }
    }
}
