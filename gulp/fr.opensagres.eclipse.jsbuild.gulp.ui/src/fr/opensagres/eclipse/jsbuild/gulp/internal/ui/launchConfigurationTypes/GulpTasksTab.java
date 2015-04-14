package fr.opensagres.eclipse.jsbuild.gulp.internal.ui.launchConfigurationTypes;

import org.eclipse.swt.graphics.Image;

import fr.opensagres.eclipse.jsbuild.gulp.internal.ui.ImageResource;
import fr.opensagres.eclipse.jsbuild.ui.launchConfigurations.AbstractJSBUildFileTasksTab;

public class GulpTasksTab extends AbstractJSBUildFileTasksTab {

	@Override
	public Image getImage() {
		return ImageResource.getImage(ImageResource.IMG_TASK);
	}
}
