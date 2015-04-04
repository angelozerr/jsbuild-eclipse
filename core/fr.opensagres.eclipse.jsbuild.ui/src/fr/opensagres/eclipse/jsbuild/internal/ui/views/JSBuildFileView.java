/**
 *  Copyright (c) 2015 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package fr.opensagres.eclipse.jsbuild.internal.ui.views;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IUpdate;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.core.JSBuildFileFactoryManager;
import fr.opensagres.eclipse.jsbuild.internal.ui.IJSBuildFileUIHelpContextIds;
import fr.opensagres.eclipse.jsbuild.internal.ui.JSBuildFileUIPlugin;
import fr.opensagres.eclipse.jsbuild.internal.ui.JSBuildFileUtil;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.actions.AddBuildFilesAction;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.actions.BuildFileOpenWithMenu;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.actions.RefreshBuildFilesAction;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.actions.RemoveAllAction;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.actions.RemoveBuildFileAction;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.actions.RunTaskAction;

/**
 * A view which displays a hierarchical view of JavaScript build files
 * (Gruntfile.js, gulpfile.js) and allows the user to run selected tasks from
 * those files.
 */
public class JSBuildFileView extends ViewPart implements
		IResourceChangeListener {

	/**
	 * XML tag used to identify a JavaScript build file in storage
	 */
	private static final String TAG_PROJECT = "project"; //$NON-NLS-1$
	/**
	 * XML key used to store an JavaScript build file's path
	 */
	private static final String KEY_PATH = "path"; //$NON-NLS-1$
	/**
	 * XML key used to store an JavaScript build file's factory id
	 */
	private static final String KEY_FACTORY_ID = "factoryId"; //$NON-NLS-1$	

	/**
	 * The view root elements
	 */
	private List<IJSBuildFile> fInput = new ArrayList<IJSBuildFile>();

	/**
	 * The tree viewer that displays the users JavaScript build files
	 */
	private TreeViewer projectViewer;
	private JSBuildFileContentProvider contentProvider;

	/**
	 * Collection of <code>IUpdate</code> actions that need to update on
	 * selection changed in the project viewer.
	 */
	private List<IUpdate> updateProjectActions;
	// Ant View Actions
	private AddBuildFilesAction addBuildFileAction;
	// private SearchForBuildFilesAction searchForBuildFilesAction;
	private RefreshBuildFilesAction refreshBuildFilesAction;
	private RemoveBuildFileAction removeProjectAction;
	private RemoveAllAction removeAllAction;
	private RunTaskAction runTargetAction;
	// Context-menu-only actions
	private BuildFileOpenWithMenu openWithMenu;

	@Override
	public void createPartControl(Composite parent) {
		initializeActions();
		createProjectViewer(parent);
		initializeDragAndDrop();
		fillMainToolBar();
		if (getProjects().length > 0) {
			// If any projects have been added to the view during startup,
			// begin listening for resource changes
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		}
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(parent, IJSBuildFileUIHelpContextIds.JSBUILD_VIEW);
		updateProjectActions();
	}

	/**
	 * Initialize the actions for this view
	 */
	private void initializeActions() {
		updateProjectActions = new ArrayList();

		addBuildFileAction = new AddBuildFilesAction(this);

		removeProjectAction = new RemoveBuildFileAction(this);
		updateProjectActions.add(removeProjectAction);

		removeAllAction = new RemoveAllAction(this);
		updateProjectActions.add(removeAllAction);

		runTargetAction = new RunTaskAction(this);
		updateProjectActions.add(runTargetAction);

		// searchForBuildFilesAction = new SearchForBuildFilesAction(this);

		refreshBuildFilesAction = new RefreshBuildFilesAction(this);
		updateProjectActions.add(refreshBuildFilesAction);

		openWithMenu = new BuildFileOpenWithMenu(this.getViewSite().getPage());

	}

	/**
	 * Create the viewer which displays the JavaScript builfiles
	 */
	private void createProjectViewer(Composite parent) {
		projectViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI);
		contentProvider = new JSBuildFileContentProvider();
		projectViewer.setContentProvider(contentProvider);

		projectViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(
				new JSBuildFileLabelProvider()));
		projectViewer.setInput(fInput);
		projectViewer.setComparator(new ViewerComparator() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse
			 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof IJSBuildFile && e2 instanceof IJSBuildFile
						|| e1 instanceof ITask && e2 instanceof ITask) {
					return e1.toString().compareToIgnoreCase(e2.toString());
				}
				return 0;
			}
		});

		projectViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						handleSelectionChanged((IStructuredSelection) event
								.getSelection());
					}
				});

		projectViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (!event.getSelection().isEmpty()) {
					handleProjectViewerDoubleClick();
				}
			}
		});

		projectViewer.getControl().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				handleProjectViewerKeyPress(event);
			}
		});

		createContextMenu(projectViewer);
		getSite().setSelectionProvider(projectViewer);

	}

	private void initializeDragAndDrop() {
		int ops = DND.DROP_COPY | DND.DROP_DEFAULT;
		Transfer[] transfers = new Transfer[] { FileTransfer.getInstance() };
		TreeViewer viewer = getViewer();
		JSBuildFileViewDropAdapter adapter = new JSBuildFileViewDropAdapter(
				this);
		viewer.addDropSupport(ops, transfers, adapter);
	}

	/**
	 * Creates a pop-up menu on the given control
	 * 
	 * @param menuControl
	 *            the control with which the pop-up menu will be associated
	 */
	private void createContextMenu(Viewer viewer) {
		Control menuControl = viewer.getControl();
		MenuManager menuMgr = new MenuManager("#PopUp"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
			}
		});
		Menu menu = menuMgr.createContextMenu(menuControl);
		menuControl.setMenu(menu);

		// register the context menu such that other plugins may contribute to
		// it
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * Adds actions to the context menu
	 * 
	 * @param viewer
	 *            the viewer who's menu we're configuring
	 * @param menu
	 *            The menu to contribute to
	 */
	private void fillContextMenu(IMenuManager menu) {
		addOpenWithMenu(menu);
		menu.add(new Separator());
		menu.add(addBuildFileAction);
		menu.add(removeProjectAction);
		menu.add(removeAllAction);
		menu.add(refreshBuildFilesAction);

		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void addOpenWithMenu(IMenuManager menu) {
		IJSBuildFileNode node = getSelectionNode();
		if (node != null) {
			IFile buildFile = node.getBuildFile().getBuildFileResource();
			if (buildFile != null) {
				menu.add(new Separator("group.open")); //$NON-NLS-1$
				IMenuManager submenu = new MenuManager(
						JSBuildFileViewMessages.JSBuildFileView_1);
				openWithMenu.setNode(node);
				submenu.add(openWithMenu);
				menu.appendToGroup("group.open", submenu); //$NON-NLS-1$
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite,
	 * org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		String persistedMemento = JSBuildFileUIPlugin.getDefault()
				.getDialogSettingsSection(getClass().getName()).get("memento"); //$NON-NLS-1$
		if (persistedMemento != null) {
			try {
				memento = XMLMemento.createReadRoot(new StringReader(
						persistedMemento));
			} catch (WorkbenchException e) {
				// don't do anything. Simply don't restore the settings
			}
		}
		if (memento != null) {
			restoreViewerInput(memento);
			/*
			 * IMemento child = memento.getChild(TAG_FILTER_INTERNAL_TARGETS);
			 * if (child != null) { filterInternalTargets =
			 * Boolean.valueOf(child.getString(KEY_VALUE)).booleanValue(); }
			 */
		}
	}

	/**
	 * Restore the viewer content that was persisted
	 * 
	 * @param memento
	 *            the memento containing the persisted viewer content
	 */
	private void restoreViewerInput(IMemento memento) {
		IMemento[] projects = memento.getChildren(TAG_PROJECT);
		if (projects.length < 1) {
			return;
		}
		for (int i = 0; i < projects.length; i++) {
			IMemento projectMemento = projects[i];
			String pathString = projectMemento.getString(KEY_PATH);
			IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(new Path(pathString));
			if (!file.exists()) {
				// If the file no longer exists, don't add it.
				continue;
			}
			String factoryId = projectMemento.getString(KEY_FACTORY_ID);
			IJSBuildFile buildFile = JSBuildFileFactoryManager.create(file,
					factoryId);
			if (buildFile != null) {
				internalAddBuildFile(buildFile);
			}
			/*
			 * String nameString = projectMemento.getString(KEY_NAME); String
			 * defaultTarget = projectMemento.getString(KEY_DEFAULT); String
			 * errorString = projectMemento.getString(KEY_ERROR); String
			 * warningString = projectMemento.getString(KEY_WARNING);
			 * 
			 * AntProjectNodeProxy project = null; if (nameString == null) {
			 * nameString = IAntCoreConstants.EMPTY_STRING; } project = new
			 * AntProjectNodeProxy(nameString, pathString); if (errorString !=
			 * null && Boolean.valueOf(errorString).booleanValue()) {
			 * project.setProblemSeverity(AntModelProblem.SEVERITY_ERROR); }
			 * else if (warningString != null &&
			 * Boolean.valueOf(warningString).booleanValue()) {
			 * project.setProblemSeverity(AntModelProblem.SEVERITY_WARNING); }
			 * if (defaultTarget != null) {
			 * project.setDefaultTargetName(defaultTarget); }
			 * fInput.add(project);
			 */
		}
	}

	/**
	 * Saves the state of the viewer into the dialog settings. Works around the
	 * issues of {@link #saveState()} not being called when a view is closed
	 * while the workbench is still running
	 * 
	 */
	private void saveViewerState() {
		XMLMemento memento = XMLMemento.createWriteRoot("jsBuildFileView"); //$NON-NLS-1$
		StringWriter writer = new StringWriter();
		IJSBuildFile[] projects = getProjects();
		if (projects.length > 0) {
			IJSBuildFile project;
			IMemento projectMemento;
			for (int i = 0; i < projects.length; i++) {
				project = projects[i];
				projectMemento = memento.createChild(TAG_PROJECT);
				projectMemento.putString(KEY_PATH, project.getBuildFileName());
				projectMemento
						.putString(KEY_FACTORY_ID, project.getFactoryId());
				// projectMemento.putString(KEY_NAME, project.getLabel());
				/*
				 * String defaultTarget = project.getDefaultTargetName(); if
				 * (project.isErrorNode()) { projectMemento.putString(KEY_ERROR,
				 * String.valueOf(true)); } else { if (project.isWarningNode())
				 * { projectMemento.putString(KEY_WARNING,
				 * String.valueOf(true)); } if (defaultTarget != null) {
				 * projectMemento.putString(KEY_DEFAULT, defaultTarget); }
				 * projectMemento.putString(KEY_ERROR, String.valueOf(false)); }
				 */
			}
			// IMemento filterTargets =
			// memento.createChild(TAG_FILTER_INTERNAL_TARGETS);
			// filterTargets.putString(KEY_VALUE, isFilterInternalTargets() ?
			// String.valueOf(true) : String.valueOf(false));
		}
		try {
			memento.save(writer);
			JSBuildFileUIPlugin.getDefault()
					.getDialogSettingsSection(getClass().getName())
					.put("memento", writer.getBuffer().toString()); //$NON-NLS-1$
		} catch (IOException e) {
			// don't do anything. Simply don't store the settings
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		saveViewerState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		saveViewerState();
		fInput.clear();
		super.dispose();
		/*
		 * if (openWithMenu != null) { openWithMenu.dispose(); }
		 */
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org
	 * .eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta != null) {
			IJSBuildFile projects[] = getProjects();
			IPath buildFilePath;
			for (int i = 0; i < projects.length; i++) {
				buildFilePath = new Path(projects[i].getBuildFileName());
				IResourceDelta change = delta.findMember(buildFilePath);
				if (change != null) {
					handleChangeDelta(change, projects[i]);
				}
			}
		}
	}

	/**
	 * Update the view for the given resource delta. The delta is a resource
	 * delta for the given build file in the view
	 * 
	 * @param delta
	 *            a delta for a build file in the view
	 * @param project
	 *            the project node that has changed
	 */
	private void handleChangeDelta(IResourceDelta delta,
			final IJSBuildFile project) {
		IResource resource = delta.getResource();
		if (resource.getType() != IResource.FILE) {
			return;
		}
		if (delta.getKind() == IResourceDelta.REMOVED) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					removeProject(project);
				}
			});
		} else if (delta.getKind() == IResourceDelta.CHANGED
				&& (delta.getFlags() & IResourceDelta.CONTENT) != 0) {
			handleBuildFileChanged(project);
		}
	}

	private void handleProjectViewerKeyPress(KeyEvent event) {
		if (event.character == SWT.DEL && event.stateMask == 0) {
			if (removeProjectAction.isEnabled()) {
				removeProjectAction.run();
			}
		} else if (event.keyCode == SWT.F5 && event.stateMask == 0) {
			if (refreshBuildFilesAction.isEnabled()) {
				refreshBuildFilesAction.run();
			}
		}
	}

	private void handleProjectViewerDoubleClick() {
		IJSBuildFileNode node = getSelectionNode();
		if (node != null) {
			runTargetAction.run(node);
		}
	}

	/**
	 * The given build file has changed. Refresh the view to pick up any
	 * structural changes.
	 */
	private void handleBuildFileChanged(IJSBuildFile project) {
		try {
			((IJSBuildFile) project).parseBuildFile(true);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// must do a full refresh to re-sort
				projectViewer.refresh();
				// update the status line
				handleSelectionChanged((IStructuredSelection) projectViewer
						.getSelection());
			}
		});
	}

	/**
	 * Updates the actions and status line for selection change in one of the
	 * viewers.
	 */
	private void handleSelectionChanged(IStructuredSelection selection) {
		updateProjectActions();
		Iterator<IJSBuildFileNode> selectionIter = selection.iterator();
		IJSBuildFileNode selected = null;
		if (selectionIter.hasNext()) {
			selected = selectionIter.next();
		}
		String messageString = null;
		if (!selectionIter.hasNext()) {
			if (selected != null) {
				/*
				 * String errorString = selected.getProblemMessage(); if
				 * (errorString != null) {
				 * getViewSite().getActionBars().getStatusLineManager
				 * ().setErrorMessage(errorString); return; }
				 */
			}
			getViewSite().getActionBars().getStatusLineManager()
					.setErrorMessage(null);
			messageString = getStatusLineText(selected);
		}
		getViewSite().getActionBars().getStatusLineManager()
				.setMessage(messageString);
	}

	/**
	 * Returns text appropriate for display in the workbench status line for the
	 * given node.
	 */
	private String getStatusLineText(IJSBuildFileNode node) {
		/*
		 * if (node instanceof IJSBuildFile) { IJSBuildFile project =
		 * (IJSBuildFile) node; StringBuffer message = new
		 * StringBuffer(project.getBuildFileName()); String description =
		 * project.getDescription(); if (description != null &&
		 * description.length() > 0) { message.append(": "); //$NON-NLS-1$
		 * message.append(description); } return message.toString(); } else if
		 * (node instanceof AntTargetNode) { AntTargetNode target =
		 * (AntTargetNode) node; StringBuffer message = new StringBuffer();
		 * Enumeration<String> depends = target.getTarget().getDependencies();
		 * if (depends.hasMoreElements()) {
		 * message.append(AntViewMessages.AntView_3);
		 * message.append(depends.nextElement()); // Unroll the loop to avoid
		 * trailing comma while (depends.hasMoreElements()) { String dependancy
		 * = depends.nextElement(); message.append(',').append(dependancy); }
		 * message.append('\"'); } String description =
		 * target.getTarget().getDescription(); if (description != null &&
		 * description.length() != 0) {
		 * message.append(AntViewMessages.AntView_4);
		 * message.append(description); message.append('\"'); } return
		 * message.toString(); }
		 */
		return null;
	}

	private void fillMainToolBar() {
		IToolBarManager toolBarMgr = getViewSite().getActionBars()
				.getToolBarManager();
		toolBarMgr.removeAll();

		toolBarMgr.add(addBuildFileAction);
		// toolBarMgr.add(searchForBuildFilesAction);

		toolBarMgr.add(runTargetAction);
		toolBarMgr.add(removeProjectAction);
		toolBarMgr.add(removeAllAction);

		toolBarMgr.update(false);
	}

	private IJSBuildFileNode getSelectionNode() {
		IStructuredSelection selection = (IStructuredSelection) getViewer()
				.getSelection();
		if (selection.size() == 1) {
			Object element = selection.getFirstElement();
			if (element instanceof IJSBuildFileNode) {
				IJSBuildFileNode node = (IJSBuildFileNode) element;
				return node;
			}
		}
		return null;
	}

	/**
	 * Updates the enabled state of all <code>IUpdate</code> actions associated
	 * with the project viewer.
	 */
	private void updateProjectActions() {
		Iterator iter = updateProjectActions.iterator();
		while (iter.hasNext()) {
			((IUpdate) iter.next()).update();
		}
	}

	/**
	 * Returns the <code>IJSBuildFile</code>s currently displayed in this view.
	 * 
	 * @return IJSBuildFile[] the <code>ProjectNode</code>s currently displayed
	 *         in this view
	 */
	public IJSBuildFile[] getProjects() {
		return fInput.toArray(new IJSBuildFile[fInput.size()]);
	}

	/**
	 * Adds the given project to the view
	 * 
	 * @param project
	 *            the project to add
	 */
	public void addProject(IJSBuildFile project) {
		internalAddBuildFile(project);
		projectViewer.refresh();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		handleSelectionChanged(new StructuredSelection(project));
	}

	protected void internalAddBuildFile(IJSBuildFile project) {
		fInput.add(project);
	}

	/**
	 * Removes the given project from the view
	 * 
	 * @param project
	 *            the project to remove
	 */
	private void removeProject(IJSBuildFile project) {
		fInput.remove(project);
		projectViewer.refresh();
		setProjectViewerSelectionAfterDeletion();
	}

	private void setProjectViewerSelectionAfterDeletion() {
		Object[] children = getProjects();
		if (children.length > 0) {
			ViewerComparator comparator = projectViewer.getComparator();
			comparator.sort(projectViewer, children);
			IStructuredSelection selection = new StructuredSelection(
					children[0]);
			projectViewer.setSelection(selection);
			handleSelectionChanged(selection);
		}
	}

	/**
	 * Removes the given list of <code>IJSBuildFile</code> objects from the
	 * view. This method should be called whenever multiple projects are to be
	 * removed because this method optimizes the viewer refresh associated with
	 * removing multiple items.
	 * 
	 * @param projectNodes
	 *            the list of <code>ProjectNode</code> objects to remove
	 */
	public void removeProjects(List<IJSBuildFile> projectNodes) {
		for (IJSBuildFile project : projectNodes) {
			fInput.remove(project);
		}
		projectViewer.refresh();
		setProjectViewerSelectionAfterDeletion();
	}

	/**
	 * Removes all projects from the view
	 */
	public void removeAllProjects() {
		IJSBuildFile[] projects = getProjects();
		for (int i = 0; i < projects.length; i++) {
			IJSBuildFile node = projects[i];
			node.dispose();
		}
		fInput.clear();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		updateProjectActions();
		projectViewer.refresh();
	}

	@Override
	public void setFocus() {
		if (getViewer() != null) {
			getViewer().getControl().setFocus();
		}
	}

	public TreeViewer getViewer() {
		return projectViewer;
	}

	public void addBuildFile(IFile buildFile) {
		if (!JSBuildFileUtil.isKnownJSBuildFile(buildFile)) {
			return;
		}
		String name = buildFile.getFullPath().toOSString();
		IJSBuildFile[] existingProjects = getProjects();
		for (int j = 0; j < existingProjects.length; j++) {
			IJSBuildFile existingProject = (IJSBuildFile) existingProjects[j];
			if (existingProject.getBuildFileName().equals(name)) {
				// Don't parse projects that have already been added.
				return;
			}
		}
		String factoryId = JSBuildFileFactoryManager.findFactoryId(buildFile);
		if (factoryId == null) {
			// TODO : show error
		} else {
			IJSBuildFile project = JSBuildFileFactoryManager.create(buildFile,
					factoryId);
			if (project == null) {
				// TODO : show error
			} else {
				addProject(project);
			}
		}
	}

}
