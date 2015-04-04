package tern.eclipse.ide.gulp.internal.query;

import org.eclipse.core.resources.IFile;

import tern.ITernFile;
import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.eclipse.ide.gulp.internal.Logger;
import tern.server.protocol.TernQuery;
import tern.server.protocol.definition.ITernDefinitionCollector;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.core.Location;

public class TernGulpTaskQuery extends TernQuery {

	public TernGulpTaskQuery(String name) {
		super("gulp-task");
		super.add("name", name);
	}

	public static Location getLocation(ITask task) {
		IFile gulpFile = task.getBuildFile().getBuildFileResource();
		String taskName = task.getName();
		return getLocation(gulpFile, taskName);
	}

	public static Location getLocation(IFile gulpFile, String taskName) {
		try {
			IIDETernProject ternProject = TernCorePlugin
					.getTernProject(gulpFile.getProject());
			TernGulpTaskQuery query = new TernGulpTaskQuery(taskName);
			ITernFile ternFile = ternProject.getFile(gulpFile);
			query.setFile(ternFile.getFileName());
			LocationCollector collector = new LocationCollector();
			ternProject.request(query, ternFile, collector);
			return collector.getLocation();
		} catch (Exception e) {
			Logger.logException(e);
		}
		return null;
	}

	private static class LocationCollector implements ITernDefinitionCollector {

		private Location location;

		@Override
		public void setDefinition(String filename, Long start, Long end) {
			if (start != null) {
				int s = start.intValue();
				int length = end != null ? end.intValue() - s : 0;
				location = new Location(s, length);
			}
		}

		public Location getLocation() {
			return location;
		}
	}

}
