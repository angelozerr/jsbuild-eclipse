package fr.opensagres.eclipse.jsbuild.internal.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IContainmentAdapter;
import org.eclipse.ui.IContributorResourceAdapter;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ide.IContributorResourceAdapter2;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.tasklist.ITaskListResourceAdapter;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;

public class JSBuildFileAdapterFactory implements IAdapterFactory {

	private static Class[] PROPERTIES = new Class[] { IPropertySource.class,
			IResource.class, IWorkbenchAdapter.class,
			IPersistableElement.class, IContributorResourceAdapter.class,
			IContributorResourceAdapter2.class, ITaskListResourceAdapter.class,
			IContainmentAdapter.class };

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
