package tern.eclipse.ide.gulp.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import tern.ITernFile;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.gulp.internal.query.TernGulpTasksQuery;
import tern.server.protocol.IJSONObjectHelper;
import tern.server.protocol.completions.ITernCompletionCollector;
import tern.server.protocol.completions.TernCompletionProposalRec;
import fr.opensagres.eclipse.jsbuild.core.AbstractBuildFile;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.gulp.core.IGulpFile;

public class GulpFile extends AbstractBuildFile implements IGulpFile {

	public GulpFile(IFile file, String factoryId) {
		super(file, factoryId);
	}

	@Override
	public ITask getDefaultTask() {
		return null;
	}

	@Override
	protected void doParseBuildFile() throws CoreException {
		IFile gulpFile = getBuildFileResource();
		IIDETernProject ternProject = GulpProject.getGulpProject(gulpFile
				.getProject());
		TernGulpTasksQuery query = new TernGulpTasksQuery();

		ITernFile ternFile = ternProject.getFile(gulpFile);
		query.setFile(ternFile.getFileName());
		try {
			ternProject.request(query, ternFile,
					new ITernCompletionCollector() {

						@Override
						public void addProposal(
								TernCompletionProposalRec proposal,
								Object completion,
								IJSONObjectHelper jsonObjectHelper) {
							GulpTask task = new GulpTask(proposal.name,
									GulpFile.this);
							GulpFile.this.addTask(task);
						}
					});
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR,
					TernGulpPlugin.PLUGIN_ID,
					"Error while getting Gulp task with tern", e);
			throw new CoreException(status);
		}
	}
}
