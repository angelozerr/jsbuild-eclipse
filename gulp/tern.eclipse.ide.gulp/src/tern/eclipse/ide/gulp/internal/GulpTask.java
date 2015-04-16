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
package tern.eclipse.ide.gulp.internal;

import tern.eclipse.ide.gulp.internal.query.TernGulpTaskQuery;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.Location;
import fr.opensagres.eclipse.jsbuild.core.SimpleTask;
import fr.opensagres.eclipse.jsbuild.gulp.core.IGulpTask;

public class GulpTask extends SimpleTask implements IGulpTask {

	public GulpTask(String name, IJSBuildFile buildFile) {
		super(name, buildFile);
	}

	@Override
	public Location getLocation(String text) {
		return TernGulpTaskQuery.getLocation(this, text);
	}

}
