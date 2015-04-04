package tern.eclipse.ide.grunt.internal;

import org.eclipse.core.resources.IFile;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileFactory;

public class GruntFileFactory implements IJSBuildFileFactory {

	private static final String GRUNTFILE_JS = "Gruntfile.js";

	@Override
	public boolean isAdaptFor(IFile file) {
		return GRUNTFILE_JS.equals(file.getName());
	}

	@Override
	public IJSBuildFile create(IFile file, String factoryId) {
		return new GruntFile(file, factoryId);
	}
}
