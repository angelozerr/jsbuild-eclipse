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
package fr.opensagres.eclipse.jsbuild.internal.ui.navigator;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileLabelProvider;

/**
 * * JavaScript build label provider for using it with navigator (like Project
 * Explorer).
 *
 */
public class NavigatorJSBuildFileLabelProvider extends JSBuildFileLabelProvider
		implements ICommonLabelProvider {

	@Override
	public String getDescription(Object element) {
		if (element instanceof IJSBuildFileNode) {
			IJSBuildFileNode node = (IJSBuildFileNode) element;
			StringBuilder description = new StringBuilder();
			description.append(node.getBuildFile().getLabel());
			description.append(" : ");
			description.append(node.getName());
			return description.toString();
		}
		return null;
	}

	@Override
	public void restoreState(IMemento memento) {
	}

	@Override
	public void saveState(IMemento memento) {

	}

	@Override
	public void init(ICommonContentExtensionSite site) {
	}

}
