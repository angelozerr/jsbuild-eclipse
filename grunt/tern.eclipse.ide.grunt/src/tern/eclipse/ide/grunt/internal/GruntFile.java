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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import tern.ITernFile;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.grunt.internal.query.TernGruntTasksQuery;
import tern.server.protocol.IJSONObjectHelper;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.completions.TernCompletionProposalRec;
import fr.opensagres.eclipse.jsbuild.core.AbstractBuildFile;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.grunt.core.IGruntFile;

public class GruntFile extends AbstractBuildFile implements IGruntFile {

	public GruntFile(IFile file, String factoryId) {
		super(file, factoryId);
	}

	@Override
	public ITask getDefaultTask() {
		return getTask(GruntTask.DEFAULT_NAME);
	}

	@Override
	protected void doParseBuildFile() throws CoreException {
		IFile gruntFile = getBuildFileResource();
		IIDETernProject ternProject = GruntProject.getGruntProject(gruntFile
				.getProject());
		TernGruntTasksQuery query = new TernGruntTasksQuery();

		ITernFile ternFile = ternProject.getFile(gruntFile);
		query.setFile(ternFile.getFileName());
		try {
			ternProject.request(query, ternFile,
					new ITernCompletionCollector() {

						@Override
						public void addProposal(
								TernCompletionProposalRec proposal,
								Object completion,
								IJSONObjectHelper jsonObjectHelper) {
							GruntTask task = new GruntTask(proposal.name,
									GruntFile.this);
							Iterable<Object> targets = jsonObjectHelper
									.getList(completion, "targets");
							if (targets != null) {
								for (Object target : targets) {
									String targetName = jsonObjectHelper
											.getText(target, "name");
									task.addTask(targetName);
								}
							}
							GruntFile.this.addTask(task);
						}
					});
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR,
					TernGruntPlugin.PLUGIN_ID,
					"Error while getting Grunt task with tern", e);
			throw new CoreException(status);
		}
	}
}
