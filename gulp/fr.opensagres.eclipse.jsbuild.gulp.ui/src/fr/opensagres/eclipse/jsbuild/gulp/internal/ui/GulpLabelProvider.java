/**
 *  Copyright (c) 2015 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package fr.opensagres.eclipse.jsbuild.gulp.internal.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import fr.opensagres.eclipse.jsbuild.gulp.core.IGulpFile;
import fr.opensagres.eclipse.jsbuild.gulp.core.IGulpTask;

/**
 * Custom label provider for Gulp file, task, targets.
 */
public class GulpLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof IGulpFile) {
			return ImageResource.getImage(ImageResource.IMG_GULPFILE);
		} else if (element instanceof IGulpTask) {
			if (((IGulpTask) element).isDefault()) {
				return ImageResource.getImage(ImageResource.IMG_TASK_DEFAULT);
			}
			return ImageResource.getImage(ImageResource.IMG_TASK);
		}
		return super.getImage(element);
	}
}
