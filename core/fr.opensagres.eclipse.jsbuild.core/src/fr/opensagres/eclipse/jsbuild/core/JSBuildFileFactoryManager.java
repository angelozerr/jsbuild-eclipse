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
package fr.opensagres.eclipse.jsbuild.core;

import org.eclipse.core.resources.IFile;

import fr.opensagres.eclipse.jsbuild.internal.core.JSBuildFileFactoriesRegistryReader;

/**
 * JavaScript build file factory manager.
 *
 */
public class JSBuildFileFactoryManager {

	/**
	 * Returns the factory id for the given file and null otherwise.
	 * 
	 * @param file
	 *            (Gruntfile.js, gulpfile.js, etc)
	 * @return the factory id for the given file and null otherwise.
	 */
	public static String findFactoryId(IFile file) {
		return JSBuildFileFactoriesRegistryReader.getInstance().findFactoryId(
				file);
	}

	/**
	 * Create an instance of {@link IJSBuildFile} from the given file.
	 * 
	 * @param file
	 *            (Gruntfile.js, gulpfile.js).
	 * @param factoryId
	 *            the factory id.
	 * @return an instance of {@link IJSBuildFile} from the given file.
	 */
	public static IJSBuildFile create(IFile file, String factoryId) {
		return JSBuildFileFactoriesRegistryReader.getInstance().create(file,
				factoryId);
	}
}
