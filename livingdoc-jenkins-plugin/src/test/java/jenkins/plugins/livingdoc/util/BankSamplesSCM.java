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

import java.io.IOException;
import java.net.URL;

public class BankSamplesSCM
		extends MultiFileSCM {

	public BankSamplesSCM()
			throws IOException {

		addFile("LivingDoc-BNKT-Story 1.xml");
		addFile("LivingDoc-BNKT-Story 2.xml");
		addFile("LivingDoc-BNKT-Story 3.xml");
		addFile("LivingDoc-BNKT-Story 4.xml");
		addFile("LivingDoc-BNKT-Story 5.xml");
		addFile("LivingDoc-BNKT-Story 6.xml");
	}

	private void addFile(String name)
			throws IOException {
		addFile(String.format("livingdoc-reports/%s", name), getResource(name));
	}

	private URL getResource(String name) {
		return getClass().getResource(String.format("/jenkins/plugins/livingdoc/samples/%s", name));
	}
}