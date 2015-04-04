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
package fr.opensagres.eclipse.jsbuild.internal.ui.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.internal.ui.LabelProviderRegistryReader;

/**
 * JavaScript build file label provider.
 *
 */
public class JSBuildFileLabelProvider extends LabelProvider implements
		IStyledLabelProvider, IColorProvider {

	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof IJSBuildFile) {
			IJSBuildFile node = (IJSBuildFile) element;
			StyledString buff = new StyledString(node.getLabel());
			IFile buildfile = node.getBuildFileResource();
			if (buildfile != null) {
				buff.append("  "); //$NON-NLS-1$
				buff.append('[', StyledString.DECORATIONS_STYLER);
				buff.append(buildfile.getFullPath().makeRelative().toString(),
						StyledString.DECORATIONS_STYLER);
				buff.append(']', StyledString.DECORATIONS_STYLER);
			}
			return buff;
		} else if (element instanceof ITask) {
			return new StyledString(((ITask) element).getLabel());
		}
		return null;
	}

	@Override
	public Color getForeground(Object node) {
		if (node instanceof ITask && ((ITask) node).isDefault()) {
			return Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		}
		return Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	}

	@Override
	public Color getBackground(Object element) {
		return Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IJSBuildFileNode) {
			String factoryId = ((IJSBuildFileNode) element).getFactoryId();
			ILabelProvider labelProvider = LabelProviderRegistryReader
					.getInstance().getLabelProvider(factoryId);
			if (labelProvider != null) {
				// custom image.
				Image image = labelProvider.getImage(element);
				if (image != null) {
					return image;
				}
			}
		}
		return super.getImage(element);
	}

}
