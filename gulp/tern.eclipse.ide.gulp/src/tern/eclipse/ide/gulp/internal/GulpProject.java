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
package tern.eclipse.ide.gulp.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.server.TernPlugin;

public class GulpProject {

	/**
	 * Return true if the given project have Gulp nature and false otherwise.
	 * 
	 * @param project
	 *            Eclipse project.
	 * @return true if the given project have Gulp nature and false otherwise.
	 */
	public static boolean hasGulpNature(IProject project) {
		if (project.isAccessible()) {
			try {
				return (TernCorePlugin.hasTernNature(project) && TernCorePlugin
						.getTernProject(project).hasPlugin(TernPlugin.gulp));
			} catch (CoreException e) {
				Logger.logException("Error Gulp project", e);
			}
		}
		return false;
	}

	/**
	 * Returns the Gulp project from the given Eclipse project.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 *             if the given project has not Gulp nature.
	 */
	public static IIDETernProject getGulpProject(IProject project)
			throws CoreException {
		if (!hasGulpNature(project)) {
			throw new CoreException(new Status(IStatus.ERROR,
					TernGulpPlugin.PLUGIN_ID, "The project "
							+ project.getName() + " is not a gulp project."));
		}
		return TernCorePlugin.getTernProject(project);
	}
}
