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
 * Abstract class for {@link ITask}
 *
 */
public abstract class AbstractTask extends AbstractBuildFileNode implements
		ITask {

	private final String name;
	private final IJSBuildFileNode parent;

	public AbstractTask(String name, IJSBuildFileNode parent) {
		this.name = name;
		this.parent = parent;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IJSBuildFileNode getParentNode() {
		return parent;
	}

	@Override
	public IJSBuildFile getBuildFile() {
		return parent.getBuildFile();
	}

	@Override
	public String getFactoryId() {
		return getBuildFile().getFactoryId();
	}
}
