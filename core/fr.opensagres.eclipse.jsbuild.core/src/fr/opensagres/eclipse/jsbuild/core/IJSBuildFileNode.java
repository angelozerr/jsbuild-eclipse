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

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;

/**
 * Base node of build file API.
 *
 */
public interface IJSBuildFileNode {

	/**
	 * Returns the node name.
	 * 
	 * @return the node name.
	 */
	String getName();

	/**
	 * Returns the label of the task.
	 * 
	 * @return the label of the task.
	 */
	String getLabel();

	/**
	 * Returns true if the node have children and false otherwise.
	 * 
	 * @return true if the node have children and false otherwise.
	 */
	boolean hasChildren();

	/**
	 * Returns the parent node.
	 * 
	 * @return the parent node.
	 */
	IJSBuildFileNode getParentNode();

	/**
	 * Returns the tasks children.
	 * 
	 * @return the tasks children.
	 * @throws CoreException
	 */
	Collection<ITask> getChildNodes();

	/**
	 * Returns the owner build file.
	 * 
	 * @return the owner build file.
	 */
	IJSBuildFile getBuildFile();

	/**
	 * Returns the factory id {@link IJSBuildFileFactory} which have created
	 * this instance.
	 * 
	 * @return the factory id {@link IJSBuildFileFactory} which have created
	 *         this instance.
	 */
	String getFactoryId();

	/**
	 * Returns the location of the node and null otherwise.
	 * @param document 
	 * 
	 * @return the location of the node and null otherwise.
	 */
	Location getLocation(String text);
}
