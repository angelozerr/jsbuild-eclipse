package fr.opensagres.eclipse.jsbuild.grunt.internal.ui.launchConfigurationTypes;

import org.eclipse.swt.graphics.Image;

import fr.opensagres.eclipse.jsbuild.grunt.internal.ui.ImageResource;
import fr.opensagres.eclipse.jsbuild.ui.launchConfigurations.AbstractJSBUildFileTasksTab;

public class GruntTasksTab extends AbstractJSBUildFileTasksTab {

	@Override
	public Image getImage() {
		return ImageResource.getImage(ImageResource.IMG_TASK);
	}
}
