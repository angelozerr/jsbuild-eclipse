package fr.opensagres.eclipse.jsbuild.internal.ui.launchConfigurations;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileLabelProvider;

/**
 * Task label provider for a table
 */
public class TaskTableLabelProvider extends JSBuildFileLabelProvider implements
		ITableLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return getImage(element);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
	 * .Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			ITask node = (ITask) element;
			return node.getLabel();
		}
		// String desc = ((AntTargetNode) element).getTarget().getDescription();
		// if (desc == null) {
		// return IAntCoreConstants.EMPTY_STRING;
		// }
		return null;
	}
}
