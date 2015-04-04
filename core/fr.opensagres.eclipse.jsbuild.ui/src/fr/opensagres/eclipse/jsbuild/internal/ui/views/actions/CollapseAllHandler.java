/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package fr.opensagres.eclipse.jsbuild.internal.ui.views.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileView;

/**
 * Default handler for Collapse All in the JS Build file view
 */
public class CollapseAllHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof JSBuildFileView) {
			JSBuildFileView view = (JSBuildFileView) part;
			TreeViewer viewer = view.getViewer();
			try {
				viewer.getTree().setRedraw(false);
				viewer.collapseAll();
			} finally {
				viewer.getTree().setRedraw(true);
			}
		}
		return null;
	}
}