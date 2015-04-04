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
package fr.opensagres.eclipse.jsbuild.internal.ui.views.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IUpdate;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.internal.ui.IJSBuildFileUIHelpContextIds;
import fr.opensagres.eclipse.jsbuild.internal.ui.ImageResource;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileView;

/**
 * Action that removes selected build files from an <code>JSBuildFileView</code>
 */
public class RemoveBuildFileAction extends Action implements IUpdate {

	private JSBuildFileView view;

	public RemoveBuildFileAction(JSBuildFileView view) {
		super(JSBuildFileViewActionMessages.RemoveProjectAction_Remove, ImageResource
				.getImageDescriptor(ImageResource.IMG_REMOVE));
		this.view = view;
		setToolTipText(JSBuildFileViewActionMessages.RemoveProjectAction_Remove_2);
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(this,
						IJSBuildFileUIHelpContextIds.REMOVE_PROJECT_ACTION);
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) view
				.getViewer().getSelection();
		Iterator<?> iter = selection.iterator();
		Object element;
		List<IJSBuildFile> projectNodes = new ArrayList<IJSBuildFile>();
		while (iter.hasNext()) {
			element = iter.next();
			if (element instanceof IJSBuildFile) {
				projectNodes.add((IJSBuildFile) element);
			}
		}
		view.removeProjects(projectNodes);
	}

	/**
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	@Override
	public void update() {
		IStructuredSelection selection = (IStructuredSelection) view
				.getViewer().getSelection();
		if (selection.isEmpty()) {
			setEnabled(false);
			return;
		}
		Object element;
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			element = iter.next();
			if (!(element instanceof IJSBuildFile)) {
				setEnabled(false);
				return;
			}
		}
		setEnabled(true);
	}

}
