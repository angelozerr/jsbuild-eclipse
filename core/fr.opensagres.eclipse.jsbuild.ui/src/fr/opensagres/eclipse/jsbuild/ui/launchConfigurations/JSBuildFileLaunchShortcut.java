/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package fr.opensagres.eclipse.jsbuild.ui.launchConfigurations;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.core.JSBuildFileFactoryManager;
import fr.opensagres.eclipse.jsbuild.core.launchConfigurationTypes.IJSBuildFileLaunchConstants;
import fr.opensagres.eclipse.jsbuild.internal.ui.JSBuildFileUIPlugin;
import fr.opensagres.eclipse.jsbuild.internal.ui.JSBuildFileUtil;
import fr.opensagres.eclipse.jsbuild.internal.ui.launchConfigurations.JSBuildFileLaunchConfigurationMessages;

/**
 * This class provides the Run/Debug As -> JavaScript Build launch shortcut.
 * 
 */
public class JSBuildFileLaunchShortcut implements ILaunchShortcut2 {

	private boolean fShowDialog = false;
	private static final int MAX_TARGET_APPEND_LENGTH = 30;

	/**
	 * Status code used by the 'Run Ant' status handler which is invoked when
	 * the launch dialog is opened by the 'Run Ant' action.
	 */
	public static final int STATUS_INIT_RUN_ANT = 1000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers
	 * .ISelection, java.lang.String)
	 */
	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object object = structuredSelection.getFirstElement();
			if (object instanceof IAdaptable) {
				if (object instanceof IJSBuildFileNode) {
					launch((IJSBuildFileNode) object, mode);
					return;
				}

				IResource resource = (IResource) ((IAdaptable) object)
						.getAdapter(IResource.class);
				if (resource != null) {
					String factoryId = null;
					if (resource.getType() == IResource.FILE) {
						factoryId = JSBuildFileFactoryManager
								.findFactoryId((IFile) resource);
					}

					if (resource != null && factoryId != null) {
						IFile file = (IFile) resource;
						launch(file.getFullPath(), file.getProject(), mode,
								null, factoryId);
						return;
					}
				}

			}
		}
		antFileNotFound();
	}

	/**
	 * Launches the given Ant node.
	 * <ul>
	 * <li>AntProjectNodes: the default target is executed</li>
	 * <li>AntTargetNodes: that target is executed</li>
	 * <li>AntTaskNodes: the owning target is executed</li>
	 * </ul>
	 * 
	 * @param node
	 *            the Ant node to use as the context for the launch
	 * @param mode
	 *            the mode of the launch
	 */
	public void launch(IJSBuildFileNode node, String mode) {
		String selectedTargetName = null;
		if (node instanceof ITask) {
			ITask targetNode = (ITask) node;
			// if (targetNode.isDefault()) {
			// selectedTargetName = targetNode.getName();
			// } else {
			// append a comma to be consistent with ant targets tab
			selectedTargetName = targetNode.getName();
			// }
		} else if (node instanceof IJSBuildFile) {
			ITask defaultTask = ((IJSBuildFile) node).getDefaultTask();
			selectedTargetName = defaultTask != null ? defaultTask.getName() : null;
		}

		String configurationTypeId = node.getFactoryId();
		IFile file = node.getBuildFile().getBuildFileResource();
		if (file != null) {
			launch(file.getFullPath(), file.getProject(), mode,
					selectedTargetName, configurationTypeId);
			return;
		}
		// external buildfile
		/*
		 * IWorkbenchPage page =
		 * AntUIPlugin.getActiveWorkbenchWindow().getActivePage(); IPath
		 * filePath = null; IEditorPart editor = page.getActiveEditor(); if
		 * (editor != null) { IEditorInput editorInput =
		 * editor.getEditorInput(); ILocationProvider locationProvider =
		 * editorInput.getAdapter(ILocationProvider.class); if (locationProvider
		 * != null) { filePath = locationProvider.getPath(editorInput); if
		 * (filePath != null) { launch(filePath, null, mode,
		 * selectedTargetName); return; } } }
		 */
		antFileNotFound();
	}

	/**
	 * Inform the user that an ant file was not found to run.
	 */
	private void antFileNotFound() {
		reportError(
				JSBuildFileLaunchConfigurationMessages.AntLaunchShortcut_Unable,
				null);
	}

	/**
	 * Walks the file hierarchy looking for a build file. Returns the first
	 * build file found that matches the search criteria.
	 */
	private IFile findBuildFile(IContainer parent) {
		String[] names = null; // JSBuildFileUtil.getKnownBuildfileNames();
		if (names == null) {
			return null;
		}
		IContainer lparent = parent;
		IResource file = null;
		while (file == null || file.getType() != IResource.FILE) {
			for (int i = 0; i < names.length; i++) {
				String string = names[i];
				file = lparent.findMember(string);
				if (file != null && file.getType() == IResource.FILE) {
					break;
				}
			}
			lparent = lparent.getParent();
			if (lparent == null) {
				return null;
			}
		}
		return (IFile) file;
	}

	/**
	 * Returns a listing of <code>ILaunchConfiguration</code>s that correspond
	 * to the specified build file.
	 * 
	 * @param filepath
	 *            the path to the buildfile to launch
	 * @return the list of <code>ILaunchConfiguration</code>s that correspond to
	 *         the specified build file.
	 * 
	 * @since 3.4
	 */
	protected List<ILaunchConfiguration> collectConfigurations(IPath filepath,
			String configurationType) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(configurationType);
		if (type != null) {
			try {
				ILaunchConfiguration[] configs = manager
						.getLaunchConfigurations(type);
				ArrayList<ILaunchConfiguration> list = new ArrayList<ILaunchConfiguration>();
				IPath location = null;
				for (int i = 0; i < configs.length; i++) {
					if (configs[i].exists()) {
						try {
							location = ExternalToolsUtil
									.getLocation(configs[i]);
							if (location != null && location.equals(filepath)) {
								list.add(configs[i]);
							}
						} catch (CoreException ce) {
							// do nothing
						}
					}
				}
				return list;
			} catch (CoreException e) {
				// do nothing
			}
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * Returns a unique name for a copy of the given launch configuration with
	 * the given targets. The name seed is the same as the name for a new launch
	 * configuration with &quot; [targetList]&quot; appended to the end.
	 * 
	 * @param filePath
	 *            the path to the buildfile
	 * @param projectName
	 *            the name of the project containing the buildfile or
	 *            <code>null</code> if no project is known
	 * @param targetAttribute
	 *            the listing of targets to execute or <code>null</code> for
	 *            default target execution
	 * @return a unique name for the copy
	 */
	public static String getNewLaunchConfigurationName(IPath filePath,
			String projectName, String targetAttribute) {
		StringBuffer buffer = new StringBuffer();
		if (projectName != null) {
			buffer.append(projectName);
			buffer.append(' ');
			buffer.append(filePath.lastSegment());
		} else {
			buffer.append(filePath.lastSegment());
		}

		if (targetAttribute != null) {
			buffer.append(" ["); //$NON-NLS-1$
			if (targetAttribute.length() > MAX_TARGET_APPEND_LENGTH + 3) {
				// The target attribute can potentially be a long,
				// comma-separated list
				// of target. Make sure the generated name isn't extremely long.
				buffer.append(targetAttribute.substring(0,
						MAX_TARGET_APPEND_LENGTH));
				buffer.append("..."); //$NON-NLS-1$
			} else {
				buffer.append(targetAttribute);
			}
			buffer.append(']');
		}

		String name = DebugPlugin.getDefault().getLaunchManager()
				.generateLaunchConfigurationName(buffer.toString());
		return name;
	}

	/**
	 * Launch the given targets in the given build file. The targets are
	 * launched in the given mode.
	 * 
	 * @param filePath
	 *            the path to the build file to launch
	 * @param project
	 *            the project for the path
	 * @param mode
	 *            the mode in which the build file should be executed
	 * @param targetAttribute
	 *            the targets to launch or <code>null</code> to use targets on
	 *            existing configuration, or <code>DEFAULT</code> for default
	 *            target explicitly.
	 * 
	 *            configuration targets attribute.
	 */
	public void launch(IPath filePath, IProject project, String mode,
			String targetAttribute, String configurationTypeId) {
		ILaunchConfiguration configuration = null;
		IFile backingfile = null;
		if (project != null) {
			// need to get the full location of a workspace file to compare
			// against the resolved config location attribute
			backingfile = project.getFile(filePath.removeFirstSegments(1));
		}
		List<ILaunchConfiguration> configs = collectConfigurations(
				(backingfile != null && backingfile.exists() ? backingfile.getLocation()
						: filePath), configurationTypeId);
		if (configs.isEmpty()) {
			configuration = createDefaultLaunchConfiguration(filePath,
					(project != null && project.exists() ? project : null),
					configurationTypeId);
		} else if (configs.size() == 1) {
			configuration = configs.get(0);
		} else {
			configuration = chooseConfig(configs);
			if (configuration == null) {
				// fail gracefully if the user cancels choosing a configuration
				return;
			}
		}

		// set the target to run, if applicable
		if (configuration != null) {
			try {
				if (targetAttribute != null
				/*
				 * && !targetAttribute.equals(configuration.getAttribute(
				 * IAntLaunchConstants.ATTR_ANT_TARGETS, DEFAULT_TARGET))
				 */) {
					ILaunchConfigurationWorkingCopy copy = configuration
							.getWorkingCopy();
					String attrValue = targetAttribute;
					copy.setAttribute(IJSBuildFileLaunchConstants.ATTR_BUILDFILE_TASKS,
							attrValue);
					configuration = copy.doSave();
				}
			} catch (CoreException exception) {
				reportError(
						MessageFormat.format(
								JSBuildFileLaunchConfigurationMessages.AntLaunchShortcut_Exception_launching,
								new Object[] { filePath.toFile().getName() }),
						exception);
				return;
			}
			launch(mode, configuration);
		} else {
			antFileNotFound();
		}
	}

	/**
	 * Delegate method to launch the specified <code>ILaunchConfiguration</code>
	 * in the specified mode
	 * 
	 * @param mode
	 *            the mode to launch in
	 * @param configuration
	 *            the <code>ILaunchConfiguration</code> to launch
	 */
	private void launch(String mode, ILaunchConfiguration configuration) {
		if (fShowDialog) {
			/*
			 * // Offer to save dirty editors before opening the dialog as the
			 * contents // of an Ant editor often affect the contents of the
			 * dialog. if (!DebugUITools.saveBeforeLaunch()) { return; }
			 */
			IStatus status = new Status(IStatus.INFO,
					JSBuildFileUIPlugin.PLUGIN_ID, STATUS_INIT_RUN_ANT, "",
					null);
			String groupId;
			if (mode.equals(ILaunchManager.DEBUG_MODE)) {
				groupId = IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP;
			} else {
				groupId = org.eclipse.ui.externaltools.internal.model.IExternalToolConstants.ID_EXTERNAL_TOOLS_LAUNCH_GROUP;
			}
			DebugUITools.openLaunchConfigurationDialog(JSBuildFileUIPlugin
					.getActiveWorkbenchWindow().getShell(), configuration,
					groupId, status);
		} else {
			DebugUITools.launch(configuration, mode);
		}
	}

	/**
	 * Creates and returns a default launch configuration for the given file.
	 * 
	 * @param file
	 * @return default launch configuration
	 */
	public static ILaunchConfiguration createDefaultLaunchConfiguration(
			IFile file, String configurationTypeId) {
		return createDefaultLaunchConfiguration(file.getFullPath(),
				file.getProject(), configurationTypeId);
	}

	/**
	 * Creates and returns a default launch configuration for the given file
	 * path and project.
	 * 
	 * @param filePath
	 *            the path to the buildfile
	 * @param project
	 *            the project containing the buildfile or <code>null</code> if
	 *            the buildfile is not contained in a project (is external).
	 * @return default launch configuration or <code>null</code> if one could
	 *         not be created
	 */
	public static ILaunchConfiguration createDefaultLaunchConfiguration(
			IPath filePath, IProject project, String configurationTypeId) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(configurationTypeId);

		String projectName = project != null ? project.getName() : null;
		String name = getNewLaunchConfigurationName(filePath, projectName, null);
		try {
			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(
					null, name);
			if (project != null) {
				workingCopy.setAttribute(
						IExternalToolConstants.ATTR_LOCATION,
						VariablesPlugin
								.getDefault()
								.getStringVariableManager()
								.generateVariableExpression(
										"workspace_loc", filePath.toString())); //$NON-NLS-1$
			} else {
				workingCopy.setAttribute(IExternalToolConstants.ATTR_LOCATION,
						filePath.toString());
			}

			// set default for common settings
			CommonTab tab = new CommonTab();
			tab.setDefaults(workingCopy);
			tab.dispose();

			IFile file = JSBuildFileUtil
					.getFileForLocation(filePath.toString());
			workingCopy.setMappedResources(new IResource[] { file });

			return workingCopy.doSave();
		} catch (CoreException e) {
			reportError(MessageFormat.format(
					JSBuildFileLaunchConfigurationMessages.AntLaunchShortcut_2,
					new Object[] { filePath.toString() }), e);
		}
		return null;
	}

	/**
	 * Returns a list of existing launch configuration for the given file.
	 * 
	 * @param file
	 *            the buildfile resource
	 * @return list of launch configurations
	 */
	public static List<ILaunchConfiguration> findExistingLaunchConfigurations(
			IFile file, String configurationTypeId) {
		List<ILaunchConfiguration> validConfigs = new ArrayList<ILaunchConfiguration>();
		if (file != null) {
			IPath filePath = file.getLocation();
			if (filePath != null) {
				ILaunchManager manager = DebugPlugin.getDefault()
						.getLaunchManager();
				ILaunchConfigurationType type = manager
						.getLaunchConfigurationType(configurationTypeId);
				if (type != null) {
					try {
						ILaunchConfiguration[] configs = manager
								.getLaunchConfigurations(type);
						for (int i = 0; i < configs.length; i++) {
							try {
								if (filePath.equals(ExternalToolsUtil
										.getLocation(configs[i]))) {
									validConfigs.add(configs[i]);
								}
							} catch (CoreException ce) {
								// do nothing
							}
						}
					} catch (CoreException e) {
						reportError(
								JSBuildFileLaunchConfigurationMessages.AntLaunchShortcut_3,
								e);
					}
				}
			}
		}
		return validConfigs;
	}

	/**
	 * Prompts the user to choose from the list of given launch configurations
	 * and returns the config the user choose or <code>null</code> if the user
	 * pressed Cancel or if the given list is empty.
	 * 
	 * @param configs
	 *            the list of {@link ILaunchConfiguration}s to choose from
	 * @return the chosen {@link ILaunchConfiguration} or <code>null</code>
	 */
	public static ILaunchConfiguration chooseConfig(
			List<ILaunchConfiguration> configs) {
		if (configs.isEmpty()) {
			return null;
		}
		ILabelProvider labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				Display.getDefault().getActiveShell(), labelProvider);
		dialog.setElements(configs.toArray(new ILaunchConfiguration[configs
				.size()]));
		dialog.setTitle(JSBuildFileLaunchConfigurationMessages.AntLaunchShortcut_4);
		dialog.setMessage(JSBuildFileLaunchConfigurationMessages.AntLaunchShortcut_5);
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart,
	 * java.lang.String)
	 */
	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IFile file = (IFile) input.getAdapter(IFile.class);
		IPath filepath = null;
		if (file != null) {
			filepath = file.getFullPath();
		}
		if (filepath == null) {
			// ILocationProvider locationProvider = input
			// .getAdapter(ILocationProvider.class);
			// if (locationProvider != null) {
			// filepath = locationProvider.getPath(input);
			// }
		}
		/*
		 * if (filepath != null && (JSBuildFileUtil.isKnownJSBuildFile(file) ||
		 * JSBuildFileUtil.isKnownAntFile(filepath.toFile()))) {
		 * launch(filepath, (file == null ? null : file.getProject()), mode,
		 * null); return; } if (file != null) { if
		 * (!JSBuildFileUtil.isKnownBuildfileName(file.getName())) { file =
		 * findBuildFile(file.getParent()); } if (file != null) {
		 * launch(file.getFullPath(), file.getProject(), mode, null); return; }
		 * }
		 */
		antFileNotFound();
	}

	/**
	 * Opens an error dialog presenting the user with the specified message and
	 * throwable
	 * 
	 * @param message
	 * @param throwable
	 */
	protected static void reportError(String message, Throwable throwable) {
		IStatus status = null;
		if (throwable instanceof CoreException) {
			status = ((CoreException) throwable).getStatus();
		} else {
			status = new Status(IStatus.ERROR, JSBuildFileUIPlugin.PLUGIN_ID,
					0, message, throwable);
		}
		ErrorDialog
				.openError(
						JSBuildFileUIPlugin.getActiveWorkbenchWindow()
								.getShell(),
						JSBuildFileLaunchConfigurationMessages.AntLaunchShortcut_Error_7,
						JSBuildFileLaunchConfigurationMessages.AntLaunchShortcut_Build_Failed_2,
						status);
	}

	/**
	 * Sets whether to show the external tools launch configuration dialog
	 * 
	 * @param showDialog
	 *            If true the launch configuration dialog will always be shown
	 */
	public void setShowDialog(boolean showDialog) {
		fShowDialog = showDialog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchShortcut2#getLaunchConfigurations(org.eclipse
	 * .jface.viewers.ISelection)
	 */
	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object object = structuredSelection.getFirstElement();
			if (object instanceof IAdaptable) {
				if (object instanceof IJSBuildFileNode) {
					// return an empty list so that the shortcut is delegated to
					// and we can prompt
					// the user for which config to run and specify the correct
					// target
					return new ILaunchConfiguration[0];
				}
				/*
				 * IResource resource = (IResource) ((IAdaptable) object)
				 * .getAdapter(IResource.class); if (resource != null) { if
				 * (!(JSBuildFileUtil.isKnownAntFile(resource))) { if
				 * (!JSBuildFileUtil.isKnownBuildfileName(resource .getName()))
				 * { if (resource.getType() == IResource.FILE) { resource =
				 * resource.getParent(); } resource = findBuildFile((IContainer)
				 * resource); } } if (resource != null) { IPath location =
				 * ((IFile) resource).getLocation(); if (location != null) {
				 * List<ILaunchConfiguration> list =
				 * collectConfigurations(location); return list.toArray(new
				 * ILaunchConfiguration[list .size()]); } } }
				 */
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchShortcut2#getLaunchConfigurations(org.eclipse
	 * .ui.IEditorPart)
	 */
	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		IFile file = (IFile) input.getAdapter(IFile.class);
		IPath filepath = null;
		if (file != null) {
			filepath = file.getLocation();
		}
		/*
		 * if (filepath == null) { ILocationProvider locationProvider = input
		 * .getAdapter(ILocationProvider.class); if (locationProvider != null) {
		 * filepath = locationProvider.getPath(input); } }
		 * 
		 * if (filepath != null &&
		 * JSBuildFileUtil.isKnownAntFileName(filepath.toString())) {
		 * List<ILaunchConfiguration> list = collectConfigurations(filepath);
		 * return list.toArray(new ILaunchConfiguration[list.size()]); }
		 */
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchShortcut2#getLaunchableResource(org.eclipse
	 * .jface.viewers.ISelection)
	 */
	@Override
	public IResource getLaunchableResource(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object object = structuredSelection.getFirstElement();
			if (object instanceof IAdaptable) {
				IResource resource = (IResource) ((IAdaptable) object)
						.getAdapter(IResource.class);
				/*
				 * if (resource != null) { if
				 * (!(JSBuildFileUtil.isKnownAntFile(resource))) { if
				 * (JSBuildFileUtil.isKnownBuildfileName(resource .getName())) {
				 * return resource; } if (resource.getType() == IResource.FILE)
				 * { resource = resource.getParent(); } resource =
				 * findBuildFile((IContainer) resource); } return resource; }
				 */
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchShortcut2#getLaunchableResource(org.eclipse
	 * .ui.IEditorPart)
	 */
	@Override
	public IResource getLaunchableResource(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		return (IResource) input.getAdapter(IFile.class);
	}
}
