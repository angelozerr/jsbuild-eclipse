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

/**
 * Build file task API.
 *
 */
public interface ITask extends IJSBuildFileNode {

	/**
	 * Returns true if this task is the default task and false otherwise.
	 * 
	 * @return true if this task is the default task and false otherwise.
	 */
	boolean isDefault();

}
