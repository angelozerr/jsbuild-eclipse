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
package fr.opensagres.eclipse.jsbuild.internal.ui.views.actions;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IUpdate;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.internal.ui.IJSBuildFileUIHelpContextIds;
import fr.opensagres.eclipse.jsbuild.internal.ui.ImageResource;
import fr.opensagres.eclipse.jsbuild.internal.ui.Logger;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileView;

/**
 * Action which refreshes the selected buildfiles in the Ant view
 */
public class RefreshBuildFilesAction extends Action implements IUpdate {

	private JSBuildFileView fView;

	/**
	 * Creates a new <code>RefreshBuildFilesAction</code> which will refresh
	 * buildfiles in the given Ant view.
	 * 
	 * @param view
	 *            the Ant view whose selection this action will use when
	 *            determining which buildfiles to refresh.
	 */
	public RefreshBuildFilesAction(JSBuildFileView view) {
		super(
				JSBuildFileViewActionMessages.RefreshBuildFilesAction_Refresh_Buildfiles_1,
				ImageResource.getImageDescriptor(ImageResource.IMG_REFRESH));
		setToolTipText(JSBuildFileViewActionMessages.RefreshBuildFilesAction_Refresh_Buildfiles_1);
		fView = view;
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(this,
						IJSBuildFileUIHelpContextIds.REFRESH_BUILDFILE_ACTION);
	}

	/**
	 * Refreshes the selected buildfiles (or all buildfiles if none selected) in
	 * the Ant view
	 */
	@Override
	public void run() {
		final Set<IJSBuildFile> projects = getSelectedProjects();
		if (projects.isEmpty()) {
			// If no selection, add all
			IJSBuildFile[] allProjects = fView.getProjects();
			for (int i = 0; i < allProjects.length; i++) {
				projects.add(allProjects[i]);
			}
		}
		final Iterator<IJSBuildFile> iter = projects.iterator();
		if (!iter.hasNext()) {
			return;
		}

		try {
			PlatformUI.getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) {
							monitor.beginTask(
									JSBuildFileViewActionMessages.RefreshBuildFilesAction_Refreshing_buildfiles_3,
									projects.size());
							IJSBuildFile project;
							while (iter.hasNext()) {
								project = (IJSBuildFile) iter.next();
								monitor.subTask(MessageFormat
										.format(JSBuildFileViewActionMessages.RefreshBuildFilesAction_Refreshing__0__4,
												new Object[] { project
														.getBuildFileName() }));
								try {
									project.parseBuildFile(true);
								} catch (CoreException e) {
									Logger.logException(e);
								}
								monitor.worked(1);
							}
						}
					});
		} catch (InvocationTargetException e) {
			// do nothing
		} catch (InterruptedException e) {
			// do nothing
		}
		fView.getViewer().refresh();
	}

	/**
	 * Returns the selected project nodes to refresh
	 * 
	 * @return Set the selected <code>AntProjectNode</code>s to refresh.
	 */
	private Set<IJSBuildFile> getSelectedProjects() {
		IStructuredSelection selection = (IStructuredSelection) fView
				.getViewer().getSelection();
		Set<IJSBuildFile> set = new HashSet<IJSBuildFile>();
		Iterator<?> iter = selection.iterator();
		Object data;
		while (iter.hasNext()) {
			data = iter.next();
			if (data instanceof IJSBuildFileNode) {
				set.add(((IJSBuildFileNode) data).getBuildFile());
			}
		}
		return set;
	}

	/**
	 * Updates the enablement of this action based on the user's selection
	 */
	@Override
	public void update() {
		setEnabled(fView.getProjects().length > 0);
	}

}
