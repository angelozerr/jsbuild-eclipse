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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IUpdate;

import fr.opensagres.eclipse.jsbuild.internal.ui.IJSBuildFileUIHelpContextIds;
import fr.opensagres.eclipse.jsbuild.internal.ui.ImageResource;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileView;

public class RemoveAllAction extends Action implements IUpdate {

	private JSBuildFileView view;

	public RemoveAllAction(JSBuildFileView view) {
		super(JSBuildFileViewActionMessages.RemoveAllAction_Remove_All, ImageResource
				.getImageDescriptor(ImageResource.IMG_REMOVE_ALL));
		setDescription(JSBuildFileViewActionMessages.RemoveAllAction_Remove_All);
		setToolTipText(JSBuildFileViewActionMessages.RemoveAllAction_Remove_All);
		this.view = view;
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(this, IJSBuildFileUIHelpContextIds.REMOVE_ALL_ACTION);
	}

	@Override
	public void run() {
		boolean proceed = MessageDialog.openQuestion(view.getViewSite()
				.getShell(), JSBuildFileViewActionMessages.RemoveAllAction_0,
				JSBuildFileViewActionMessages.RemoveAllAction_1);
		if (proceed) {
			view.removeAllProjects();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	@Override
	public void update() {
		setEnabled(view.getViewer().getTree().getItemCount() != 0);
	}
}
