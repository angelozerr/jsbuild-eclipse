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
package fr.opensagres.eclipse.jsbuild.internal.ui.actions;

import org.eclipse.osgi.util.NLS;

public class ActionMessages extends NLS {

	private static final String BUNDLE_NAME = "fr.opensagres.eclipse.jsbuild.internal.ui.actions.ActionMessages";//$NON-NLS-1$

	public static String OpenAction_error_title;
	public static String OpenAction_label;
	public static String OpenAction_tooltip;
	public static String OpenAction_description;
	public static String OpenWithMenu_label;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ActionMessages.class);
	}
}
