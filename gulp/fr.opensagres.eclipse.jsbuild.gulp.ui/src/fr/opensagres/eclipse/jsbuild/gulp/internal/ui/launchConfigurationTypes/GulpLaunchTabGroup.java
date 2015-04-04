package fr.opensagres.eclipse.jsbuild.gulp.internal.ui.launchConfigurationTypes;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.RefreshTab;

public class GulpLaunchTabGroup extends AbstractLaunchConfigurationTabGroup {

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new GulpLaunchTasksTab(), new GulpLaunchArgumentsTab(),
				new RefreshTab(), new CommonTab() };
		setTabs(tabs);
	}

}
