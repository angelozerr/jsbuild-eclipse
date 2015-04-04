package fr.opensagres.eclipse.jsbuild.core.launchConfigurationTypes;

import java.io.File;

import org.eclipse.core.externaltools.internal.launchConfigurations.ExternalToolsCoreUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.core.model.RuntimeProcess;

public abstract class JSBuildFileLaunchConfigurationDelegate extends
		LaunchConfigurationDelegate {

	protected static final String[] EMPTY_STRING = new String[0];

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (monitor.isCanceled()) {
			return;
		}

		// resolve location
		IPath location = ExternalToolsCoreUtil.getLocation(configuration);
		monitor.worked(1);

		if (monitor.isCanceled()) {
			return;
		}

		IPath workingDirectory = ExternalToolsCoreUtil
				.getWorkingDirectory(configuration);
		File basedir = null;
		if (workingDirectory != null) {
			basedir = workingDirectory.toFile();
		}
		if (basedir == null) {
			basedir = location.toFile().getParentFile();
		}
		monitor.worked(1);

		if (monitor.isCanceled()) {
			return;
		}

		String[] cmdLine = getCmdLine(configuration, location);
		String[] envp = getEnvironmentVariables(configuration);
		Process p = DebugPlugin.exec(cmdLine, basedir, envp);
		// no way to get private p.handle from java.lang.ProcessImpl
		RuntimeProcess process = (RuntimeProcess) DebugPlugin.newProcess(
				launch, p, getProcessLabel());

		monitor.done();

	}

	protected String[] getEnvironmentVariables(
			ILaunchConfiguration configuration) {
		return null;
	}

	protected String[] getTargetNames(ILaunchConfiguration configuration)
			throws CoreException {
		return AntLaunchingUtil.getTargetNames(configuration);
	}

	/**
	 * Returns true if OS is Windows and false otherwise.
	 * 
	 * @return
	 */
	protected boolean isWindowsOS() {
		return Platform.getOS().startsWith("win");
	}

	protected abstract String getProcessLabel();

	protected abstract String[] getCmdLine(ILaunchConfiguration configuration,
			IPath location) throws CoreException;
}
