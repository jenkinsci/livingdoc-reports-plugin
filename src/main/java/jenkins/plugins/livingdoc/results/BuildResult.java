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
package jenkins.plugins.livingdoc.results;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import hudson.model.Run;
import info.novatec.testit.livingdoc.util.ExceptionImposter;
import jenkins.plugins.livingdoc.BuildReportBean;


public class BuildResult implements Serializable {

    private static final long serialVersionUID = 2L;
    private final transient Run< ? , ? > run;
    private final BuildReportBean report;

    public BuildResult(Run< ? , ? > run, BuildReportBean report) {
        this.run = run;
        this.report = report;
    }

    public BuildReportBean getReport() {
        return report;
    }

    public String getResult() {

        // Backward compatibility
        if (report.getResult() != null) {
            return report.getResult();
        }

        if ( ! report.isResultFileExist()) {
            throw new IllegalStateException("No results!");
        }

        InputStreamReader reader = null;

        try {
            reader = new InputStreamReader(new FileInputStream(report.getResultFile()), Charset.forName("UTF-8"));
            return IOUtils.toString(reader);
        } catch (IOException ex) {
            throw ExceptionImposter.imposterize(ex);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public Run< ? , ? > getRun() {
        return run;
    }
}
