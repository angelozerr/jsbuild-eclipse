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
package fr.opensagres.eclipse.jsbuild.internal.ui.views;

import org.eclipse.osgi.util.NLS;

public class JSBuildFileViewMessages extends NLS {
	
	private static final String BUNDLE_NAME = "fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileViewMessages";//$NON-NLS-1$

	public static String JSBuildFileView_1;
	public static String JSBuildFileView_3;
	public static String JSBuildFileView_4;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, JSBuildFileViewMessages.class);
	}
}