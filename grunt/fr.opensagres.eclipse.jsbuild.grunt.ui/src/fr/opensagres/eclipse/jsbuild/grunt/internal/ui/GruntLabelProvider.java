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
package fr.opensagres.eclipse.jsbuild.grunt.internal.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import fr.opensagres.eclipse.jsbuild.grunt.core.IGruntFile;
import fr.opensagres.eclipse.jsbuild.grunt.core.IGruntTarget;
import fr.opensagres.eclipse.jsbuild.grunt.core.IGruntTask;

/**
 * Custom label provider for Grunt file, task, targets.
 */
public class GruntLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof IGruntFile) {
			return ImageResource.getImage(ImageResource.IMG_GRUNTFILE);
		} else if (element instanceof IGruntTask) {
			if (((IGruntTask) element).isDefault()) {
				return ImageResource.getImage(ImageResource.IMG_TASK_DEFAULT);
			}
			return ImageResource.getImage(ImageResource.IMG_TASK);
		} else if (element instanceof IGruntTarget) {
			return ImageResource.getImage(ImageResource.IMG_TARGET);
		}
		return super.getImage(element);
	}
}
