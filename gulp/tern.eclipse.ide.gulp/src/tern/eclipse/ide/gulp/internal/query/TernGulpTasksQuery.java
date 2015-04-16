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
package tern.eclipse.ide.gulp.internal.query;

import tern.server.protocol.TernQuery;

public class TernGulpTasksQuery extends TernQuery {

	public TernGulpTasksQuery() {
		super("gulp-tasks");
	}

}
