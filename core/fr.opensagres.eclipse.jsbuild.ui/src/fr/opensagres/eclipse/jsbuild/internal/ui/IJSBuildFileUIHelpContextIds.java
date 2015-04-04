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
package fr.opensagres.eclipse.jsbuild.internal.ui;

/**
 * Help context ids for the JavaScript Build file UI
 * <p>
 * This interface contains constants only; it is not intended to be implemented
 * or extended.
 * </p>
 */
public interface IJSBuildFileUIHelpContextIds {

	public static final String PREFIX = "fr.opensagres.eclipse.jsbuild.ui."; //$NON-NLS-1$

	// Views
	public static final String JSBUILD_VIEW = PREFIX + "jsbuild_view_context"; //$NON-NLS-1$

	// Actions
	public static final String REMOVE_ALL_ACTION = PREFIX
			+ "remove_all_action_context"; //$NON-NLS-1$
	public static final String ADD_BUILDFILE_ACTION = PREFIX
			+ "add_buildfile_action_context"; //$NON-NLS-1$
	public static final String REFRESH_BUILDFILE_ACTION = PREFIX
			+ "refresh_buildfile_action_context"; //$NON-NLS-1$
	public static final String RUN_TARGET_ACTION = PREFIX
			+ "run_target_action_context"; //$NON-NLS-1$
	public static final String REMOVE_PROJECT_ACTION = PREFIX
			+ "remove_project_action_context"; //$NON-NLS-1$

}
