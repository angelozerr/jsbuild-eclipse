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
import org.eclipse.core.runtime.CoreException;

/**
 * Build file API like Gruntfile.js, glupfile.js.
 *
 */
public interface IJSBuildFile extends IJSBuildFileNode {

	/**
	 * Returns the default task and null otherwise.
	 * 
	 * @return the default task and null otherwise.
	 */
	ITask getDefaultTask();

	/**
	 * Returns the resource file.
	 * 
	 * @return the resource file.
	 */
	IFile getBuildFileResource();

	/**
	 * Returns the build file name.
	 * 
	 * @return the build file name.
	 */
	String getBuildFileName();

	/**
	 * Parse JavaScript build file if needed to load tasks.
	 * 
	 * @throws CoreException
	 */
	void parseBuildFile() throws CoreException;

	/**
	 * Force the parse of the JavaScript build file to load tasks.
	 * 
	 * @param force
	 * @throws CoreException
	 */
	void parseBuildFile(boolean force) throws CoreException;

	/**
	 * Dispose the build file.
	 */
	void dispose();

}
