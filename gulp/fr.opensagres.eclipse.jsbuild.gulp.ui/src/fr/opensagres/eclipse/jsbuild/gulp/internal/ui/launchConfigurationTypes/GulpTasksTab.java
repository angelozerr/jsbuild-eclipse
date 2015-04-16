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
