package fr.opensagres.eclipse.jsbuild.internal.ui.views;

import java.util.Set;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;

public class NavigatorJSBuildFileContentProvider extends JSBuildFileContentProvider implements IPipelinedTreeContentProvider {

	@Override
	public void init(
			ICommonContentExtensionSite paramICommonContentExtensionSite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restoreState(IMemento paramIMemento) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveState(IMemento paramIMemento) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getPipelinedChildren(Object paramObject, Set paramSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getPipelinedElements(Object paramObject, Set paramSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getPipelinedParent(Object paramObject1, Object paramObject2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipelinedShapeModification interceptAdd(
			PipelinedShapeModification paramPipelinedShapeModification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipelinedShapeModification interceptRemove(
			PipelinedShapeModification paramPipelinedShapeModification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean interceptRefresh(
			PipelinedViewerUpdate paramPipelinedViewerUpdate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean interceptUpdate(
			PipelinedViewerUpdate paramPipelinedViewerUpdate) {
		// TODO Auto-generated method stub
		return false;
	}

}
