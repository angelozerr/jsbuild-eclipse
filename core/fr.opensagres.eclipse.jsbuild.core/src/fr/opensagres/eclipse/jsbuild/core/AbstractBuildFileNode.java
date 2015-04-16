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

import org.eclipse.core.runtime.PlatformObject;

/**
 * Abstract class implementation for {@link IJSBuildFileNode}.
 *
 */
public abstract class AbstractBuildFileNode extends PlatformObject implements
		IJSBuildFileNode {

	@Override
	public String getLabel() {
		return getName();
	}

	@Override
	public boolean hasChildren() {
		return getChildNodes().size() > 0;
	}

	@Override
	public Location getLocation(String text) {
		return null;
	}

}
