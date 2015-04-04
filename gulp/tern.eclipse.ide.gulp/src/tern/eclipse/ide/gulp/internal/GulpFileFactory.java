package tern.eclipse.ide.gulp.internal;

import org.eclipse.core.resources.IFile;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileFactory;

public class GulpFileFactory implements IJSBuildFileFactory {

	private static final String GULPFILE_JS = "gulpfile.js";

	@Override
	public boolean isAdaptFor(IFile file) {
		return GULPFILE_JS.equals(file.getName());
	}

	@Override
	public IJSBuildFile create(IFile file, String factoryId) {
		return new GulpFile(file, factoryId);
	}
}
