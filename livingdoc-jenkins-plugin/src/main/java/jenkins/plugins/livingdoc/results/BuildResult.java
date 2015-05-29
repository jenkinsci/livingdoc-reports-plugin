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
package jenkins.plugins.livingdoc.results;

import hudson.model.AbstractBuild;
import hudson.util.IOUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import jenkins.plugins.livingdoc.BuildReportBean;

import com.greenpepper.util.ExceptionImposter;


public class BuildResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final AbstractBuild< ? , ? > build;
    private final BuildReportBean report;

    public BuildResult (AbstractBuild< ? , ? > build, BuildReportBean report) {
        this.build = build;
        this.report = report;
    }

    public AbstractBuild< ? , ? > getBuild () {
        return build;
    }

    public BuildReportBean getReport () {
        return report;
    }

    @Deprecated
    public String getResult () throws IllegalStateException, RuntimeException {

        // Backward compatibility
        if (report.getResult() != null) {
            return report.getResult();
        }

        if ( ! report.isResultFileExist()) {
            throw new IllegalStateException("No results!");
        }

        try (FileReader reader = new FileReader(report.getResultFile())) {
            return IOUtils.toString(reader);
        } catch (IOException ex) {
            throw ExceptionImposter.imposterize(ex);
        }

    }
}
