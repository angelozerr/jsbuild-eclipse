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
package tern.eclipse.ide.grunt.internal;

import tern.eclipse.ide.grunt.internal.query.TernGruntTaskQuery;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.core.Location;
import fr.opensagres.eclipse.jsbuild.core.SimpleTask;
import fr.opensagres.eclipse.jsbuild.grunt.core.IGruntTarget;

/**
 * Grunt target implementation.
 *
 */
public class GruntTarget extends SimpleTask implements IGruntTarget {

	private final String targetName;
	private final String targetLabel;

	public GruntTarget(String name, ITask parent) {
		super(name, parent);
		this.targetName = new StringBuilder(parent.getName()).append(":")
				.append(name).toString();
		this.targetLabel = name;
	}

	@Override
	public String getName() {
		return targetName;
	}

	@Override
	public String getLabel() {
		return targetLabel;
	}

	@Override
	public Location getLocation(String text) {
		return TernGruntTaskQuery.getLocation(this, text);
	}
}
