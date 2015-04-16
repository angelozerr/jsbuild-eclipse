/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package tern.eclipse.ide.grunt.internal.query;

import org.eclipse.core.resources.IFile;

import tern.ITernFile;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.eclipse.ide.core.resources.TernTextFile;
import tern.eclipse.ide.grunt.internal.Logger;
import tern.server.protocol.TernQuery;
import tern.server.protocol.definition.ITernDefinitionCollector;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.core.Location;

public class TernGruntTaskQuery extends TernQuery {

	public TernGruntTaskQuery(String name) {
		super("grunt-task");
		super.add("name", name);
	}

	public static Location getLocation(ITask task, String text) {
		IFile gruntFile = task.getBuildFile().getBuildFileResource();
		String taskName = task.getName();
		return getLocation(gruntFile, taskName, text);
	}

	public static Location getLocation(IFile gruntFile, String taskName,
			String text) {
		try {
			IIDETernProject ternProject = TernCorePlugin
					.getTernProject(gruntFile.getProject());
			TernGruntTaskQuery query = new TernGruntTaskQuery(taskName);
			ITernFile ternFile = text != null ? new TernTextFile(gruntFile,
					text) : ternProject.getFile(gruntFile);
			query.setFile(ternFile.getFileName());
			LocationCollector collector = new LocationCollector();
			ternProject.request(query, ternFile, collector);
			return collector.getLocation();
		} catch (Exception e) {
			Logger.logException(e);
		}
		return null;
	}

	private static class LocationCollector implements ITernDefinitionCollector {

		private Location location;

		@Override
		public void setDefinition(String filename, Long start, Long end) {
			if (start != null) {
				int s = start.intValue();
				int length = end != null ? end.intValue() - s : 0;
				location = new Location(s, length);
			}
		}

		public Location getLocation() {
			return location;
		}
	}

}
