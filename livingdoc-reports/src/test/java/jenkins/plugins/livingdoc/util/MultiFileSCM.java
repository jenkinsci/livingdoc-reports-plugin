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
package jenkins.plugins.livingdoc.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jvnet.hudson.test.SingleFileSCM;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.scm.NullSCM;

public class MultiFileSCM
		extends NullSCM {

	private List<SingleFileSCM> files = new ArrayList<SingleFileSCM>();

	public void addFile(String path, URL resource)
			throws IOException {

		if (path == null) {
			throw new NullPointerException(String.format("Path is null! (%s)", resource));
		}

		if (resource == null) {
			throw new NullPointerException(String.format("Resource is null! (%s)", path));
		}
		
		files.add(new SingleFileSCM(path, resource));
	}

	@Override
	public boolean checkout(AbstractBuild build, Launcher launcher, FilePath workspace, BuildListener listener,
							File changeLogFile)
			throws IOException, InterruptedException {

		for (SingleFileSCM file : this.files) {
			file.checkout(build, launcher, workspace, listener, changeLogFile);
		}

		return true;
	}
}
