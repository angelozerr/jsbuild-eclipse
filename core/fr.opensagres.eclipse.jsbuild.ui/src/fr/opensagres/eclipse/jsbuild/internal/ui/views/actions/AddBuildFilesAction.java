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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.internal.ui.IJSBuildFileUIHelpContextIds;
import fr.opensagres.eclipse.jsbuild.internal.ui.ImageResource;
import fr.opensagres.eclipse.jsbuild.internal.ui.JSBuildFileUtil;
import fr.opensagres.eclipse.jsbuild.internal.ui.dialogs.FileSelectionDialog;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileView;

/**
 * Action that prompts the user for build files and adds the selected files to
 * an <code>JSBuildFileView</code>
 */
public class AddBuildFilesAction extends Action {

	private JSBuildFileView view;

	public AddBuildFilesAction(JSBuildFileView view) {
		super(JSBuildFileViewActionMessages.AddBuildFilesAction_1, ImageResource
				.getImageDescriptor(ImageResource.IMG_ADD));
		this.view = view;
		setToolTipText(JSBuildFileViewActionMessages.AddBuildFilesAction_0);
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(this,
						IJSBuildFileUIHelpContextIds.ADD_BUILDFILE_ACTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() {
		String title = JSBuildFileViewActionMessages.AddBuildFilesAction_2;
		String message = JSBuildFileViewActionMessages.AddBuildFilesAction_4;
		String filterExtension = JSBuildFileUtil
				.getKnownBuildFileExtensionsAsPattern();
		String filterMessage = JSBuildFileViewActionMessages.AddBuildFilesAction_5;

		FileSelectionDialog dialog = new FileSelectionDialog(Display
				.getCurrent().getActiveShell(), getBuildFiles(), title,
				message, filterExtension, filterMessage);
		dialog.open();
		final Object[] result = dialog.getResult();
		if (result == null) {
			return;
		}

		try {
			PlatformUI.getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) {
							monitor.beginTask(
									JSBuildFileViewActionMessages.AddBuildFilesAction_3,
									result.length);
							for (int i = 0; i < result.length
									&& !monitor.isCanceled(); i++) {
								final Object file = result[i];
								if (file instanceof IFile) {
									monitor.worked(1);
									Display.getDefault().asyncExec(
											new Runnable() {
												@Override
												public void run() {
													view.addBuildFile((IFile) file);
												}
											});
								}
							}
						}
					});
		} catch (InvocationTargetException e) {
			// do nothing
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	private List<IFile> getBuildFiles() {
		IJSBuildFileNode[] existingProjects = view.getProjects();
		List<IFile> buildFiles = new ArrayList<IFile>(existingProjects.length);
		for (int j = 0; j < existingProjects.length; j++) {
			IJSBuildFileNode existingProject = existingProjects[j];
			buildFiles.add(JSBuildFileUtil.getFile(existingProject
					.getBuildFile().getBuildFileName()));
		}
		return buildFiles;
	}
}
