/**
 *  Copyright (c) 2015 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package fr.opensagres.eclipse.jsbuild.gulp.internal.core.launchConfigurationTypes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;

import fr.opensagres.eclipse.jsbuild.core.launchConfigurationTypes.JSBuildFileLaunchConfigurationDelegate;

/**
 * Launch configuration delegate for Gulp.
 *
 */
public class GulpLaunchConfigurationDelegate extends
		JSBuildFileLaunchConfigurationDelegate {

	@Override
	protected String[] getCmdLine(ILaunchConfiguration configuration,
			IPath location) throws CoreException {
		List<String> cmdLine = new ArrayList<String>();
		cmdLine.add("gulp.cmd");
		cmdLine.add("--no-color");
		String[] tasks = getTargetNames(configuration);
		if (tasks != null) {
			cmdLine.add(tasks[0]);
		}
		return cmdLine.toArray(EMPTY_STRING);
	}

	@Override
	protected String getProcessLabel() {
		return "Gulp process";
	}
}
