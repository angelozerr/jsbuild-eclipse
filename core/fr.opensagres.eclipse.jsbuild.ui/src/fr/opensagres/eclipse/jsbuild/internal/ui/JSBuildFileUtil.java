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

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.core.JSBuildFileFactoryManager;

/**
 * Utilities for JavaScript build file.
 *
 */
public class JSBuildFileUtil {

	public static IFile getFileForLocation(String path) {
		if (path == null) {
			return null;
		}
		IPath filePath = new Path(path);
		IFile file = null;
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
				.findFilesForLocation(filePath);
		if (files.length > 0) {
			return files[0];
		}
		return null;
	}

	/**
	 * Returns if the given extension is a known extension to JS Build file i.e.
	 * a supported content type extension.
	 * 
	 * @param resource
	 * @return true if the file extension is supported false otherwise
	 * 
	 */
	public static boolean isKnownJSBuildFile(IResource resource) {
		// if (resource != null) {
		// // workspace file
		// IFile file = null;
		// if (resource.getType() == IResource.FILE) {
		// file = (IFile) resource;
		// } else {
		// file = resource.getAdapter(IFile.class);
		// }
		// if (file != null) {
		// IContentType fileType = IDE.getContentType(file);
		// if (fileType == null) {
		// return false;
		// }
		// IContentType antType =
		// Platform.getContentTypeManager().getContentType(AntCorePlugin.ANT_BUILDFILE_CONTENT_TYPE);
		// if (antType != null) {
		// return fileType.isKindOf(antType);
		// }
		// }
		// }
		// return false;
		// How to check content type of Gruntfile.js, glupfile.js?
		return true;
	}

	public static String getKnownBuildFileExtensionsAsPattern() {
		return "js";
	}

	public static IFile getFile(String fullPath) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getFile(new Path(fullPath));
	}

	public static ITask[] getTargets(String path,
			ILaunchConfiguration fLaunchConfiguration) throws CoreException {
		File buildfile = getBuildFile(path);
		if (buildfile == null) {
			return null;
		}
		IFile file = getFileForLocation(buildfile.getAbsolutePath());
		if (file == null) {
			return null;
		}
		IJSBuildFileNode buildFileNode = JSBuildFileFactoryManager
				.tryToCreate(file);
		return getTasks(buildFileNode).toArray(ITask.EMPTY_TASK);
	}

	public static Collection<ITask> getTasks(IJSBuildFileNode buildFileNode) {
		if (buildFileNode != null) {
			try {
				buildFileNode.getBuildFile().parseBuildFile();
			} catch (CoreException e) {
				Logger.logException("Error while loading tasks", e);
			}
			if (buildFileNode.hasChildren()) {
				return buildFileNode.getChildNodes();
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Return a buildfile from the specified location. If there isn't one return
	 * null.
	 */
	private static File getBuildFile(String path) {
		File buildFile = new File(path);
		if (!buildFile.isFile() || !buildFile.exists()) {
			return null;
		}

		return buildFile;
	}
}
