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
package fr.opensagres.eclipse.jsbuild.internal.ui;

import org.eclipse.osgi.util.NLS;

public class JSBuildFileUIMessages extends NLS {
	
	private static final String BUNDLE_NAME = "fr.opensagres.eclipse.jsbuild.internal.ui.JSBuildFileUIMessages";//$NON-NLS-1$

	public static String ImageDescriptorRegistry_Allocating_image_for_wrong_display_1;

	public static String JSBuildFileUtil_6;
	public static String JSBuildFileUtil_0;
	public static String JSBuildFileUtil_1;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, JSBuildFileUIMessages.class);
	}
}