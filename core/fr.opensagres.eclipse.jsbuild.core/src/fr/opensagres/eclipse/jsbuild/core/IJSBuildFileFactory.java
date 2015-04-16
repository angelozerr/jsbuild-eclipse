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
package fr.opensagres.eclipse.jsbuild.core;

import org.eclipse.core.resources.IFile;

/**
 * Factory to create JavaScript build file {@link IJSBuildFile}.
 *
 */
public interface IJSBuildFileFactory {

	/**
	 * Returns true if the given file can use this factory to create
	 * {@link IJSBuildFile} and false otherwise.
	 * 
	 * @param file
	 * @return true if the given file can use this factory to create
	 *         {@link IJSBuildFile} and false otherwise.
	 */
	boolean isAdaptFor(IFile file);

	/**
	 * Create an instance of {@link IJSBuildFile} from the given file.
	 * 
	 * @param file
	 *            (Gruntfile.js, gulpfile.js).
	 * @param factoryId
	 *            the factory id.
	 * @return an instance of {@link IJSBuildFile} from the given file.
	 */
	IJSBuildFile create(IFile file, String factoryId);

}
