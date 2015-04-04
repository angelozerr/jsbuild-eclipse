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

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.internal.ui.Logger;

/**
 * JavaScript build file content provider.
 *
 */
public class JSBuildFileContentProvider implements ITreeContentProvider {

	protected static final Object[] EMPTY_ARRAY = new Object[0];

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentNode) {
		if (parentNode instanceof IJSBuildFileNode) {
			IJSBuildFileNode parentElement = (IJSBuildFileNode) parentNode;
			try {
				parentElement.getBuildFile().parseBuildFile();
			} catch (CoreException e) {
				Logger.logException("Error while loading tasks", e);
			}
			if (parentElement.hasChildren()) {
				Collection<ITask> children = parentElement.getChildNodes();
				return children.toArray();
			}
		}
		/*
		 * else if (parentNode instanceof IAntModel) { return new Object[] {
		 * ((IAntModel) parentNode).getProjectNode() }; }
		 */
		return EMPTY_ARRAY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		return ((IJSBuildFileNode) element).getParentNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		return ((IJSBuildFileNode) element).hasChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List) {
			return ((List<?>) inputElement).toArray();
		}
		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		return EMPTY_ARRAY;
	}
}
