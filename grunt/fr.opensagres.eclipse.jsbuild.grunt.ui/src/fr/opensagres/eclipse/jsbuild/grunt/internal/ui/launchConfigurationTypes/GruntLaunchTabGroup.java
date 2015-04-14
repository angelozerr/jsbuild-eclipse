package fr.opensagres.eclipse.jsbuild.grunt.internal.ui.launchConfigurationTypes;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.RefreshTab;

import fr.opensagres.eclipse.jsbuild.ui.launchConfigurations.JSBUildFileMainTab;

public class GruntLaunchTabGroup extends AbstractLaunchConfigurationTabGroup {

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new JSBUildFileMainTab(), new GruntTasksTab(),
				new RefreshTab(), new CommonTab() };
		setTabs(tabs);
	}

}
