package tern.eclipse.ide.grunt.internal;

import tern.eclipse.ide.grunt.internal.query.TernGruntTaskQuery;
import fr.opensagres.eclipse.jsbuild.core.CompositeTask;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.core.Location;
import fr.opensagres.eclipse.jsbuild.grunt.core.IGruntTask;

public class GruntTask extends CompositeTask implements IGruntTask {

	public static final String DEFAULT_NAME = "default";

	public GruntTask(String name, IJSBuildFile buildFile) {
		super(name, buildFile);
	}

	@Override
	public boolean isDefault() {
		return DEFAULT_NAME.equals(getName());
	}

	@Override
	protected ITask createTask(String name, ITask parentTask) {
		return new GruntTarget(name, parentTask);
	}
	
	@Override
	public Location getLocation(String text) {
		return TernGruntTaskQuery.getLocation(this, text);
	}

}
