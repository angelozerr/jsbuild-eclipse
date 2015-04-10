package fr.opensagres.eclipse.jsbuild.core.launchConfigurationTypes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.externaltools.internal.launchConfigurations.BackgroundResourceRefresher;
import org.eclipse.core.externaltools.internal.launchConfigurations.ExternalToolsCoreUtil;
import org.eclipse.core.externaltools.internal.launchConfigurations.ExternalToolsProgramMessages;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.RefreshUtil;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.osgi.util.NLS;

/**
 * Abstract launch configuration delegate for JavaScript Build file
 * (Gruntfile.js, gulpfile.js).
 *
 */
public abstract class JSBuildFileLaunchConfigurationDelegate extends
		LaunchConfigurationDelegate {

	private static final String ATTR_LAUNCH_IN_BACKGROUND = "org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND";

	protected static final String[] EMPTY_STRING = new String[0];

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (monitor.isCanceled()) {
			return;
		}
		// resolve location
		IPath location = ExternalToolsCoreUtil.getLocation(configuration);
		if (monitor.isCanceled()) {
			return;
		}
		// resolve working directory
		IPath workingDirectory = ExternalToolsCoreUtil
				.getWorkingDirectory(configuration);
		if (monitor.isCanceled()) {
			return;
		}
		String[] cmdLine = getCmdLine(configuration, location);

		File workingDir = null;
		if (workingDirectory != null) {
			workingDir = workingDirectory.toFile();
		} else {
			workingDir = location.toFile().getParentFile();
		}
		if (monitor.isCanceled()) {
			return;
		}
		String[] envp = DebugPlugin.getDefault().getLaunchManager()
				.getEnvironment(configuration);
		if (monitor.isCanceled()) {
			return;
		}
		Process p = DebugPlugin.exec(cmdLine, workingDir, envp);
		IProcess process = null;
		// add process type to process attributes
		Map<String, String> processAttributes = new HashMap<String, String>();
		String programName = location.lastSegment();
		String extension = location.getFileExtension();
		if (extension != null) {
			programName = programName.substring(0, programName.length()
					- (extension.length() + 1));
		}
		programName = programName.toLowerCase();
		processAttributes.put(IProcess.ATTR_PROCESS_TYPE, programName);
		if (p != null) {
			monitor.beginTask(NLS.bind(
					ExternalToolsProgramMessages.ProgramLaunchDelegate_3,
					new String[] { configuration.getName() }),
					IProgressMonitor.UNKNOWN);
			process = DebugPlugin.newProcess(launch, p, location.toOSString(),
					processAttributes);
		}
		if (p == null || process == null) {
			if (p != null) {
				p.destroy();
			}
			throw new CoreException(new Status(IStatus.ERROR,
					IExternalToolConstants.PLUGIN_ID,
					IExternalToolConstants.ERR_INTERNAL_ERROR,
					ExternalToolsProgramMessages.ProgramLaunchDelegate_4, null));
		}
		process.setAttribute(IProcess.ATTR_CMDLINE,
				generateCommandLine(cmdLine));
		if (configuration.getAttribute(ATTR_LAUNCH_IN_BACKGROUND, true)) {
			// refresh resources after process finishes
			String scope = configuration.getAttribute(
					RefreshUtil.ATTR_REFRESH_SCOPE, (String) null);
			if (scope != null) {
				BackgroundResourceRefresher refresher = new BackgroundResourceRefresher(
						configuration, process);
				refresher.startBackgroundRefresh();
			}
		} else {
			// wait for process to exit
			while (!process.isTerminated()) {
				try {
					if (monitor.isCanceled()) {
						process.terminate();
						break;
					}
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
			// refresh resources
			RefreshUtil.refreshResources(configuration, monitor);
		}
	}

	private String generateCommandLine(String[] commandLine) {
		if (commandLine.length < 1) {
			return IExternalToolConstants.EMPTY_STRING;
		}
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < commandLine.length; i++) {
			buf.append(' ');
			char[] characters = commandLine[i].toCharArray();
			StringBuilder command = new StringBuilder();
			boolean containsSpace = false;
			for (int j = 0; j < characters.length; j++) {
				char character = characters[j];
				if (character == '\"') {
					command.append('\\');
				} else if (character == ' ') {
					containsSpace = true;
				}
				command.append(character);
			}
			if (containsSpace) {
				buf.append('\"');
				buf.append(command);
				buf.append('\"');
			} else {
				buf.append(command);
			}
		}
		return buf.toString();
	}

	//
	// @Override
	// public void launch(ILaunchConfiguration configuration, String mode,
	// ILaunch launch, IProgressMonitor monitor) throws CoreException {
	// if (monitor.isCanceled()) {
	// return;
	// }
	//
	// // resolve location
	// IPath location = ExternalToolsCoreUtil.getLocation(configuration);
	// monitor.worked(1);
	//
	// if (monitor.isCanceled()) {
	// return;
	// }
	//
	// IPath workingDirectory = ExternalToolsCoreUtil
	// .getWorkingDirectory(configuration);
	// File basedir = null;
	// if (workingDirectory != null) {
	// basedir = workingDirectory.toFile();
	// }
	// if (basedir == null) {
	// basedir = location.toFile().getParentFile();
	// }
	// monitor.worked(1);
	//
	// if (monitor.isCanceled()) {
	// return;
	// }
	//
	// String[] cmdLine = getCmdLine(configuration, location);
	// String[] envp = getEnvironmentVariables(configuration);
	// Process p = DebugPlugin.exec(cmdLine, basedir, envp);
	// // no way to get private p.handle from java.lang.ProcessImpl
	// RuntimeProcess process = (RuntimeProcess) DebugPlugin.newProcess(
	// launch, p, getProcessLabel());
	//
	// monitor.done();
	//
	// }

	protected String[] getEnvironmentVariables(
			ILaunchConfiguration configuration) {
		return null;
	}

	protected String[] getTargetNames(ILaunchConfiguration configuration)
			throws CoreException {
		return JSBUildFileLaunchingUtil.getTargetNames(configuration);
	}

	@Override
	protected boolean saveBeforeLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		if (IExternalToolConstants.ID_EXTERNAL_TOOLS_BUILDER_LAUNCH_CATEGORY
				.equals(configuration.getType().getCategory())) {
			// don't prompt for builders
			return true;
		}
		return super.saveBeforeLaunch(configuration, mode, monitor);
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