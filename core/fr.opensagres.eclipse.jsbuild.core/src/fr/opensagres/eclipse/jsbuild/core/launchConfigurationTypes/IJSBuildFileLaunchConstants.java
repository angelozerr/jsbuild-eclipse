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
package fr.opensagres.eclipse.jsbuild.core.launchConfigurationTypes;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;

/**
 * Launch constants for JavaScritp build file.
 *
 */
public interface IJSBuildFileLaunchConstants {

	/**
	 * String attribute indicating the Ant targets to execute. Default value is
	 * <code>null</code> which indicates that the default target is to be
	 * executed. Format is a comma separated listing of targets.
	 */
	public static final String ATTR_BUILDFILE_TASKS = IExternalToolConstants.UI_PLUGIN_ID
			+ ".ATTR_BUILDFILE_TASKS"; //$NON-NLS-1$

}
