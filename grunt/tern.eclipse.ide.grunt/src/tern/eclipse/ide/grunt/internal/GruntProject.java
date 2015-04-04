package tern.eclipse.ide.grunt.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import tern.eclipse.ide.core.IIDETernProject;
import tern.eclipse.ide.core.TernCorePlugin;
import tern.server.TernPlugin;

public class GruntProject {

	/**
	 * Return true if the given project have Grunt nature and false otherwise.
	 * 
	 * @param project
	 *            Eclipse project.
	 * @return true if the given project have Grunt nature and false otherwise.
	 */
	public static boolean hasGruntNature(IProject project) {
		if (project.isAccessible()) {
			try {
				return (TernCorePlugin.hasTernNature(project) && TernCorePlugin
						.getTernProject(project).hasPlugin(TernPlugin.grunt));
			} catch (CoreException e) {
				Logger.logException("Error Grunt project", e);
			}
		}
		return false;
	}

	/**
	 * Returns the Grunt project from the given Eclipse project.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 *             if the given project has not Grunt nature.
	 */
	public static IIDETernProject getGruntProject(IProject project)
			throws CoreException {
		if (!hasGruntNature(project)) {
			throw new CoreException(new Status(IStatus.ERROR,
					TernGruntPlugin.PLUGIN_ID, "The project "
							+ project.getName() + " is not a grunt project."));
		}
		return TernCorePlugin.getTernProject(project);
	}
}
