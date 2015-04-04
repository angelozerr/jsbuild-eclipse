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

/**
 * {@link ITask} implementation for task which have task's children.
 *
 */
public class CompositeTask extends AbstractTask {

	private final Map<String, ITask> tasks;

	public CompositeTask(String name, IJSBuildFileNode node) {
		super(name, node);
		this.tasks = new HashMap<String, ITask>();
	}

	@Override
	public Collection<ITask> getChildNodes() {
		return tasks.values();
	}

	public ITask addTask(String name) {
		ITask task = createTask(name, this);
		tasks.put(task.getName(), task);
		return task;
	}

	protected ITask createTask(String name, ITask parentTask) {
		return new CompositeTask(name, parentTask);
	}

	@Override
	public boolean isDefault() {
		return false;
	}

}
