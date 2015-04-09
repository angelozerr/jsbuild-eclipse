package fr.opensagres.eclipse.jsbuild.internal.ui.views;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;

import fr.opensagres.eclipse.jsbuild.core.JSBuildFileFactoryManager;

public class NavigatorJSBuildFileContentProvider extends
		JSBuildFileContentProvider implements IPipelinedTreeContentProvider,
		IResourceChangeListener, IResourceDeltaVisitor {

	private TreeViewer fViewer;

	/*
	 * (non-Javadoc) Method declared on IContentProvider.
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		fViewer = (TreeViewer) viewer;
		if (oldInput == null && newInput != null) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		} else if (oldInput != null && newInput == null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public void init(
			ICommonContentExtensionSite paramICommonContentExtensionSite) {

	}

	@Override
	public void restoreState(IMemento paramIMemento) {

	}

	@Override
	public void saveState(IMemento paramIMemento) {

	}

	@Override
	public void getPipelinedChildren(Object paramObject, Set paramSet) {

	}

	@Override
	public void getPipelinedElements(Object paramObject, Set paramSet) {

	}

	@Override
	public Object getPipelinedParent(Object paramObject1, Object paramObject2) {
		return null;
	}

	@Override
	public PipelinedShapeModification interceptAdd(
			PipelinedShapeModification paramPipelinedShapeModification) {
		return paramPipelinedShapeModification;
	}

	@Override
	public PipelinedShapeModification interceptRemove(
			PipelinedShapeModification paramPipelinedShapeModification) {
		return paramPipelinedShapeModification;
	}

	@Override
	public boolean interceptRefresh(
			PipelinedViewerUpdate paramPipelinedViewerUpdate) {
		return false;
	}

	@Override
	public boolean interceptUpdate(
			PipelinedViewerUpdate paramPipelinedViewerUpdate) {
		return false;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta != null) {
			try {
				delta.accept(this);
			} catch (CoreException e) {
			}
		}
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (resource == null) {
			return false;
		}
		switch (resource.getType()) {
		case IResource.ROOT:
		case IResource.PROJECT:
		case IResource.FOLDER:
			return true;
		case IResource.FILE:
			IFile file = (IFile) delta.getResource();
			if (fViewer != null
					&& JSBuildFileFactoryManager.findFactoryId(file) != null) {
				// refresh the tasks for the Build file (Gruntfile.js,
				// gulpfile.js) inside the Project Explorer
				fViewer.refresh(file);
			}
			return false;
		}
		return false;

	}

}
