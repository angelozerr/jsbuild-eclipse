package fr.opensagres.eclipse.jsbuild.internal.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;

public class JSBuildFileAdapterFactory implements IAdapterFactory {

	private static Class[] PROPERTIES = new Class[] { IResource.class };

	@Override
	public Object getAdapter(Object element, Class key) {
		IJSBuildFileNode node = getJSBuildFileNode(element);
		if (node == null) {
			return null;
		}
		if (IResource.class.equals(key)) {
			return getResource(node);
		}
		return null;
	}

	private Object getResource(IJSBuildFileNode node) {
		return node.getBuildFile().getBuildFileResource();
	}

	private IJSBuildFileNode getJSBuildFileNode(Object element) {
		if (element instanceof IJSBuildFileNode) {
			return (IJSBuildFileNode) element;
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return PROPERTIES;
	}

}
