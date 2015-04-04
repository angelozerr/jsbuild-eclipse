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
import java.util.Collections;

/**
 * {@link ITask} simple implementation.
 *
 */
public class SimpleTask extends AbstractTask {

	public SimpleTask(String name, IJSBuildFileNode parent) {
		super(name, parent);
	}

	@Override
	public Collection<ITask> getChildNodes() {
		return Collections.emptyList();
	}

	@Override
	public boolean isDefault() {
		return false;
	}
}
