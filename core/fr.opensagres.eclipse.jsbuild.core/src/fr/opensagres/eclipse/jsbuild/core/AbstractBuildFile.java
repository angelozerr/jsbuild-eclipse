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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Abstract class implementation for {@link IJSBuildFile}.
 *
 */
public abstract class AbstractBuildFile extends AbstractBuildFileNode implements
		IJSBuildFile {

	private final IFile file;
	private final String factoryId;
	private boolean parsed;
	private final Map<String, ITask> tasks;

	public AbstractBuildFile(IFile file, String factoryId) {
		this.file = file;
		this.factoryId = factoryId;
		this.tasks = new HashMap<String, ITask>();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public IFile getBuildFileResource() {
		return file;
	}

	@Override
	public String getBuildFileName() {
		return file.getFullPath().toOSString();
	}

	@Override
	public IJSBuildFileNode getParentNode() {
		return null;
	}

	@Override
	public String getFactoryId() {
		return factoryId;
	}

	@Override
	public void parseBuildFile() throws CoreException {
		parseBuildFile(false);
	}

	@Override
	public void parseBuildFile(boolean force) throws CoreException {
		if (parsed && !force) {
			return;
		}
		tasks.clear();
		doParseBuildFile();
		parsed = true;
	}

	protected void addTask(ITask task) {
		tasks.put(task.getName(), task);
	}

	@Override
	public void dispose() {

	}

	public ITask getTask(String name) {
		return tasks.get(name);
	}

	@Override
	public Collection<ITask> getChildNodes() {
		return tasks.values();
	}

	@Override
	public IJSBuildFile getBuildFile() {
		return this;
	}

	@Override
	public boolean hasChildren() {
		// even if build file has not tasks, we consider that there are children
		// in order to load
		// the tasks when getChildNodes is called with the
		// JSBuildFileContentProvider in order to load tasks with lazy mode.
		return true;
	}

	protected abstract void doParseBuildFile() throws CoreException;
}
