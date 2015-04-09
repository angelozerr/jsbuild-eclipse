package tern.eclipse.ide.gulp.internal;

import tern.eclipse.ide.gulp.internal.query.TernGulpTaskQuery;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.Location;
import fr.opensagres.eclipse.jsbuild.core.SimpleTask;
import fr.opensagres.eclipse.jsbuild.gulp.core.IGulpTask;

public class GulpTask extends SimpleTask implements IGulpTask {

	public GulpTask(String name, IJSBuildFile buildFile) {
		super(name, buildFile);
	}

	@Override
	public Location getLocation(String text) {
		return TernGulpTaskQuery.getLocation(this, text);
	}

}
