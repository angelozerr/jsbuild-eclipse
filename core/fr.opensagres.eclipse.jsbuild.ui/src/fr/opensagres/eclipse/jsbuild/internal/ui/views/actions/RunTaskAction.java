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

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IUpdate;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.internal.ui.IJSBuildFileUIHelpContextIds;
import fr.opensagres.eclipse.jsbuild.internal.ui.ImageResource;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileView;
import fr.opensagres.eclipse.jsbuild.ui.launchConfigurations.JSBuildFileLaunchShortcut;

/**
 * Action which runs the selected target or the default target of the selected
 * project in the JSBuildFileView.
 */
public class RunTaskAction extends Action implements IUpdate {

	private JSBuildFileView fView;

	/**
	 * Creates a new <code>RunTargetAction</code> which will execute targets in
	 * the given view.
	 * 
	 * @param view
	 *            the Ant view whose selection this action will use when
	 *            determining which target to run.
	 */
	public RunTaskAction(JSBuildFileView view) {

		setText(JSBuildFileViewActionMessages.RunTaskAction_Run_1);
		setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_RUN));
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(this, IJSBuildFileUIHelpContextIds.RUN_TARGET_ACTION);

		setToolTipText(JSBuildFileViewActionMessages.RunTaskAction_3);
		fView = view;
	}

	/**
	 * Executes the appropriate target based on the selection in the Ant view.
	 */
	@Override
	public void run() {
		run(getSelectedElement());
	}

	/**
	 * @param selectedElement
	 *            The element to use as the context for launching
	 */
	public void run(final IJSBuildFileNode selectedElement) {
		UIJob job = new UIJob(JSBuildFileViewActionMessages.RunTaskAction_2) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				launch(selectedElement);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/**
	 * Launches the given Ant element node
	 * 
	 * @param node
	 *            the node to use to launch
	 * @see JSBuildFileLaunchShortcut#launch(AntElementNode, String)
	 */
	public void launch(IJSBuildFileNode node) {
		JSBuildFileLaunchShortcut shortcut = new JSBuildFileLaunchShortcut();
		shortcut.setShowDialog(false);
		shortcut.launch(node, ILaunchManager.RUN_MODE);
	}

	/**
	 * Updates the enablement of this action based on the user's selection
	 */
	@Override
	public void update() {
		IJSBuildFileNode selection = getSelectedElement();
		boolean enabled = false;
		if (selection instanceof IJSBuildFile) {
			setToolTipText(JSBuildFileViewActionMessages.RunTaskAction_4);
			enabled = true;
		} else if (selection instanceof ITask) {
			setToolTipText(JSBuildFileViewActionMessages.RunTaskAction_3);
			enabled = true;
		}
		setEnabled(enabled);
	}

	/**
	 * Returns the selected node or <code>null</code> if more than one element
	 * is selected.
	 * 
	 * @return AntElementNode the selected node
	 */
	private IJSBuildFileNode getSelectedElement() {
		IStructuredSelection selection = (IStructuredSelection) fView
				.getViewer().getSelection();
		if (selection.isEmpty()) {
			return null;
		}
		Iterator<?> iter = selection.iterator();
		Object data = iter.next();
		if (iter.hasNext()) {
			return null;
		}
		return (IJSBuildFileNode) data;
	}
}
