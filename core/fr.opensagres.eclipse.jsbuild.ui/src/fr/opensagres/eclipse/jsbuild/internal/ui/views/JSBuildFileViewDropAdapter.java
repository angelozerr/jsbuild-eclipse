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
package fr.opensagres.eclipse.jsbuild.internal.ui.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.JSBuildFileFactoryManager;
import fr.opensagres.eclipse.jsbuild.internal.ui.JSBuildFileUtil;

/**
 * A drop adapter which adds files to the JSBuild file view.
 */
public class JSBuildFileViewDropAdapter extends DropTargetAdapter {

	private JSBuildFileView view;

	/**
	 * Creates a new drop adapter for the given JSBuild view.
	 * 
	 * @param view
	 *            the view which dropped files will be added to
	 */
	public JSBuildFileViewDropAdapter(JSBuildFileView view) {
		this.view = view;
	}

	/**
	 * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void drop(DropTargetEvent event) {
		Object data = event.data;
		if (data instanceof String[]) {
			final String[] strings = (String[]) data;
			BusyIndicator.showWhile(null, new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < strings.length; i++) {
						processString(strings[i]);
					}
				}
			});
		}
	}

	/**
	 * Attempts to process the given string as a path to an XML file. If the
	 * string is determined to be a path to an XML file in the workspace, that
	 * file is added to the JSBuild view.
	 * 
	 * @param buildFileName
	 *            the string to process
	 */
	private void processString(String buildFileName) {
		IFile buildFile = JSBuildFileUtil.getFileForLocation(buildFileName);
		view.addBuildFile(buildFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	@Override
	public void dragEnter(DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
		super.dragEnter(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse
	 * .swt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragOperationChanged(DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
		super.dragOperationChanged(event);
	}
}