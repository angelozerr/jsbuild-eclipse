package fr.opensagres.eclipse.jsbuild.core.launchConfigurationTypes;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;

public interface IAntLaunchConstants {

	/**
	 * String attribute indicating the Ant targets to execute. Default value is
	 * <code>null</code> which indicates that the default target is to be
	 * executed. Format is a comma separated listing of targets.
	 */
	public static final String ATTR_BUILDFILE_TASKS = IExternalToolConstants.UI_PLUGIN_ID
			+ ".ATTR_BUILDFILE_TASKS"; //$NON-NLS-1$

}
