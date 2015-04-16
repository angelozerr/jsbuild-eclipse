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
package fr.opensagres.eclipse.jsbuild.ui.launchConfigurations;

public class JSBuildFileLaunchShortcutWithDialog extends
		JSBuildFileLaunchShortcut {

	/**
	 * Creates a new JavaScript Build file launch shortcut that will open the
	 * launch configuration dialog.
	 */
	public JSBuildFileLaunchShortcutWithDialog() {
		super();
		setShowDialog(true);
	}
}
