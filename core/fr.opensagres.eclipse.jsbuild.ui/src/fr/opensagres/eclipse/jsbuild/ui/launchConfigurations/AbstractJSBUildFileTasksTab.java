/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Martin Karpisek (martin.karpisek@gmail.com) - bug 229474
 *******************************************************************************/
package fr.opensagres.eclipse.jsbuild.ui.launchConfigurations;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.ITask;
import fr.opensagres.eclipse.jsbuild.core.launchConfigurationTypes.IJSBuildFileLaunchConstants;
import fr.opensagres.eclipse.jsbuild.internal.ui.JSBuildFileUtil;
import fr.opensagres.eclipse.jsbuild.internal.ui.Logger;
import fr.opensagres.eclipse.jsbuild.internal.ui.launchConfigurations.JSBuildFileLaunchConfigurationMessages;
import fr.opensagres.eclipse.jsbuild.internal.ui.launchConfigurations.TaskTableLabelProvider;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileContentProvider;
import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileLabelProvider;

/**
 * Launch configuration tab which allows the user to choose the targets from an
 * Grunt / Gulp buildfile that will be executed when the configuration is
 * launched.
 */
public abstract class AbstractJSBUildFileTasksTab extends
		AbstractLaunchConfigurationTab {

	private ITask fDefaultTarget = null;
	private ITask[] fAllTargets = null;
	private List<ITask> fOrderedTargets = null;

	private CheckboxTableViewer fTableViewer = null;
	private Label fSelectionCountLabel = null;
	private Text fTargetOrderText = null;
	private Button fOrderButton = null;
	// private Button fFilterInternalTargets;
	// private InternalTargetFilter fInternalTargetFilter = null;
	private Button fSortButton;

	private ILaunchConfiguration fLaunchConfiguration;
	private int fSortDirection = 0;
	private boolean fInitializing = false;

	/**
	 * Sort direction constants.
	 */
	public final static int SORT_NONE = 0;
	public final static int SORT_NAME = 1;
	public final static int SORT_NAME_REVERSE = -1;
	public final static int SORT_DESCRIPTION = 2;
	public final static int SORT_DESCRIPTION_REVERSE = -2;

	/**
	 * A comparator which can sort targets by name or description, in forward or
	 * reverse order.
	 */
	private class AntTargetsComparator extends ViewerComparator {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface
		 * .viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (!(e1 instanceof ITask && e2 instanceof ITask)) {
				return super.compare(viewer, e1, e2);
			}
			if (fSortDirection == SORT_NONE) {
				return 0;
			}
			String string1 = null, string2 = null;
			int result = 0;
			if (fSortDirection == SORT_NAME
					|| fSortDirection == SORT_NAME_REVERSE) {
				string1 = ((ITask) e1).getLabel();
				string2 = ((ITask) e2).getLabel();
			}
			/*
			 * else { string1 = ((ITask) e1).getTarget().getDescription();
			 * string2 = ((ITask) e2).getTarget().getDescription(); }
			 */
			if (string1 != null && string2 != null) {
				result = getComparator().compare(string1, string2);
			} else if (string1 == null) {
				result = 1;
			} else if (string2 == null) {
				result = -1;
			}
			if (fSortDirection < 0) { // reverse sort
				if (result == 0) {
					result = -1;
				} else {
					result = -result;
				}
			}
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Font font = parent.getFont();

		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IAntUIHelpContextIds.ANT_TARGETS_TAB);
		GridLayout topLayout = new GridLayout();
		comp.setLayout(topLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gd);
		comp.setFont(font);

		createTargetsTable(comp);
		createSelectionCount(comp);

		Composite buttonComposite = new Composite(comp, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonComposite.setLayout(layout);
		buttonComposite.setFont(font);

		createSortTargets(buttonComposite);
		// createFilterInternalTargets(buttonComposite);

		createVerticalSpacer(comp, 1);
		// createTargetOrder(comp);
		Dialog.applyDialogFont(parent);
	}

	/**
	 * Creates the selection count widget
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createSelectionCount(Composite parent) {
		fSelectionCountLabel = new Label(parent, SWT.NONE);
		fSelectionCountLabel.setFont(parent.getFont());
		fSelectionCountLabel
				.setText(JSBuildFileLaunchConfigurationMessages.AntTargetsTab_0_out_of_0_selected_2);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fSelectionCountLabel.setLayoutData(gd);
	}

	/**
	 * Creates the widgets that display the target order
	 * 
	 * @param parent
	 *            the parent composite
	 */
	/*
	 * private void createTargetOrder(Composite parent) { Font font =
	 * parent.getFont();
	 * 
	 * Label label = new Label(parent, SWT.NONE);
	 * label.setText(JSBuildFileLaunchConfigurationMessages
	 * .AntTargetsTab_Target_execution_order__3); label.setFont(font);
	 * 
	 * Composite orderComposite = new Composite(parent, SWT.NONE); GridData gd =
	 * new GridData(GridData.FILL_HORIZONTAL); orderComposite.setLayoutData(gd);
	 * GridLayout layout = new GridLayout(2, false); layout.marginHeight = 0;
	 * layout.marginWidth = 0; orderComposite.setLayout(layout);
	 * orderComposite.setFont(font);
	 * 
	 * fTargetOrderText = new Text(orderComposite, SWT.MULTI | SWT.WRAP |
	 * SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
	 * fTargetOrderText.setFont(font); gd = new
	 * GridData(GridData.FILL_HORIZONTAL); gd.heightHint = 40; gd.widthHint =
	 * IDialogConstants.ENTRY_FIELD_WIDTH; fTargetOrderText.setLayoutData(gd);
	 * 
	 * fOrderButton = createPushButton(orderComposite,
	 * JSBuildFileLaunchConfigurationMessages.AntTargetsTab__Order____4, null);
	 * gd = (GridData) fOrderButton.getLayoutData(); gd.verticalAlignment =
	 * GridData.BEGINNING; fOrderButton.setFont(font);
	 * fOrderButton.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override public void widgetSelected(SelectionEvent e) {
	 * handleOrderPressed(); } }); }
	 */

	/**
	 * Creates the toggle to filter internal targets from the table
	 * 
	 * @param parent
	 *            the parent composite
	 */
	// private void createFilterInternalTargets(Composite parent) {
	// fFilterInternalTargets = createCheckButton(parent,
	// JSBuildFileLaunchConfigurationMessages.AntTargetsTab_12);
	// fFilterInternalTargets.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// handleFilterTargetsSelected();
	// }
	// });
	// }

	/**
	 * Creates the toggle to sort targets in the table
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createSortTargets(Composite parent) {
		fSortButton = createCheckButton(parent,
				JSBuildFileLaunchConfigurationMessages.AntTargetsTab_14);
		fSortButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSortTargetsSelected();
			}
		});
	}

	/**
	 * The filter targets button has been toggled. If it's been turned on,
	 * filter out internal targets. Else, restore internal targets to the table.
	 */
	// private void handleFilterTargetsSelected() {
	// boolean filter = fFilterInternalTargets.getSelection();
	// if (filter) {
	// fTableViewer.addFilter(getInternalTargetsFilter());
	// } else {
	// fTableViewer.removeFilter(getInternalTargetsFilter());
	// }
	//
	// // Must refresh before updating selection count because the selection
	// // count's "hidden" reporting needs the content provider to be queried
	// // first to count how many targets are hidden.
	// updateSelectionCount();
	// if (!fInitializing) {
	// updateLaunchConfigurationDialog();
	// }
	// }

	/**
	 * The button to sort targets has been toggled. Set the tab's sorting as
	 * appropriate.
	 */
	private void handleSortTargetsSelected() {
		setSort(fSortButton.getSelection() ? SORT_NAME : SORT_NONE);
	}

	/**
	 * Sets the sorting of targets in this tab. See the sort constants defined
	 * above.
	 * 
	 * @param column
	 *            the column which should be sorted on
	 */
	private void setSort(int column) {
		fSortDirection = column;
		fTableViewer.refresh();
		if (!fInitializing) {
			updateLaunchConfigurationDialog();
		}
	}

	/**
	 * The target order button has been pressed. Prompt the user to reorder the
	 * selected targets.
	 */
	/*
	 * private void handleOrderPressed() { TargetOrderDialog dialog = new
	 * TargetOrderDialog(getShell(), fOrderedTargets.toArray(new
	 * ITask[fOrderedTargets.size()])); int ok = dialog.open(); if (ok ==
	 * Window.OK) { fOrderedTargets.clear(); Object[] targets =
	 * dialog.getTargets(); for (int i = 0; i < targets.length; i++) {
	 * fOrderedTargets.add((ITask) targets[i]); updateSelectionCount();
	 * updateLaunchConfigurationDialog(); } } }
	 */

	/**
	 * Creates the table which displays the available targets
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createTargetsTable(Composite parent) {
		Font font = parent.getFont();
		Label label = new Label(parent, SWT.NONE);
		label.setFont(font);
		label.setText(JSBuildFileLaunchConfigurationMessages.AntTargetsTab_Check_targets_to_e_xecute__1);

		final Table table = new Table(parent, SWT.CHECK | SWT.BORDER
				| SWT.FULL_SELECTION);

		GridData data = new GridData(GridData.FILL_BOTH);
		int availableRows = availableRows(parent);
		data.heightHint = table.getItemHeight() * (availableRows / 20);
		data.widthHint = 250;
		table.setLayoutData(data);
		table.setFont(font);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableLayout tableLayout = new TableLayout();
		ColumnWeightData weightData = new ColumnWeightData(100, true);
		tableLayout.addColumnData(weightData);
//		weightData = new ColumnWeightData(70, true);
//		tableLayout.addColumnData(weightData);
		table.setLayout(tableLayout);

		final TableColumn column1 = new TableColumn(table, SWT.NULL);
		column1.setText(JSBuildFileLaunchConfigurationMessages.AntTargetsTab_Name_5);

		//final TableColumn column2 = new TableColumn(table, SWT.NULL);
		//column2.setText(JSBuildFileLaunchConfigurationMessages.AntTargetsTab_Description_6);

		// TableLayout only sizes columns once. If showing the targets
		// tab as the initial tab, the dialog isn't open when the layout
		// occurs and the column size isn't computed correctly. Need to
		// recompute the size of the columns once all the parent controls
		// have been created/sized.
		// HACK Bug 139190
//		getShell().addShellListener(new ShellAdapter() {
//			@Override
//			public void shellActivated(ShellEvent e) {
//				if (!table.isDisposed()) {
//					int tableWidth = table.getSize().x;
//					if (tableWidth > 0) {
//						int c1 = tableWidth / 3;
//						column1.setWidth(c1);
//						//column2.setWidth(tableWidth - c1);
//					}
//					getShell().removeShellListener(this);
//				}
//			}
//		});

		fTableViewer = new CheckboxTableViewer(table);
		fTableViewer.setLabelProvider(getLabelProvider());
		fTableViewer.setContentProvider(getContentProvider());
		fTableViewer.setComparator(new AntTargetsComparator());

		fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (!selection.isEmpty()
						&& selection instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) selection;
					Object element = ss.getFirstElement();
					boolean checked = !fTableViewer.getChecked(element);
					fTableViewer.setChecked(element, checked);
					updateOrderedTargets(element, checked);
				}
			}
		});

		fTableViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateOrderedTargets(event.getElement(), event.getChecked());
			}
		});

		TableColumn[] columns = fTableViewer.getTable().getColumns();
		for (int i = 0; i < columns.length; i++) {
			final int index = i;
			columns[index].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (fSortButton.getSelection()) {
						// index 0 => sort_name (1)
						// index 1 => sort_description (2)
						int column = index + 1;
						if (column == fSortDirection) {
							column = -column; // invert the sort when the same
												// column is selected twice in a
												// row
						}
						setSort(column);
					}
				}
			});
		}
	}

	/**
	 * Return the number of rows available in the current display using the
	 * current font.
	 * 
	 * @param parent
	 *            The Composite whose Font will be queried.
	 * @return int The result of the display size divided by the font size.
	 */
	private int availableRows(Composite parent) {

		int fontHeight = (parent.getFont().getFontData())[0].getHeight();
		int displayHeight = parent.getDisplay().getClientArea().height;

		return displayHeight / fontHeight;
	}

	/**
	 * Updates the ordered targets list in response to an element being checked
	 * or unchecked. When the element is checked, it's added to the list. When
	 * unchecked, it's removed.
	 * 
	 * @param element
	 *            the element in question
	 * @param checked
	 *            whether the element has been checked or unchecked
	 */
	private void updateOrderedTargets(Object element, boolean checked) {
		if (checked) {
			fOrderedTargets.add((ITask) element);
		} else {
			fOrderedTargets.remove(element);
		}
		updateSelectionCount();
		updateLaunchConfigurationDialog();
	}

	/**
	 * Updates the selection count widget to display how many targets are
	 * selected (example, "1 out of 6 selected") and filtered.
	 */
	private void updateSelectionCount() {
		Object[] checked = fTableViewer.getCheckedElements();
		String numSelected = Integer.toString(checked.length);

		int all = fAllTargets == null ? 0 : fAllTargets.length;
		int visible = fTableViewer.getTable().getItemCount();
		String total = Integer.toString(visible);
		int numHidden = all - visible;
		if (numHidden > 0) {
			fSelectionCountLabel.setText(MessageFormat.format(
					JSBuildFileLaunchConfigurationMessages.AntTargetsTab_13,
					new Object[] { numSelected, String.valueOf(all),
							String.valueOf(numHidden) }));
		} else {
			fSelectionCountLabel
					.setText(MessageFormat
							.format(JSBuildFileLaunchConfigurationMessages.AntTargetsTab__0__out_of__1__selected_7,
									new Object[] { numSelected, total }));
		}

		// fOrderButton.setEnabled(checked.length > 1);

		/*
		 * StringBuffer buffer = new StringBuffer(); Iterator<ITask> iter =
		 * fOrderedTargets.iterator(); while (iter.hasNext()) {
		 * buffer.append(iter.next().getName()); buffer.append(", ");
		 * //$NON-NLS-1$ } if (buffer.length() > 2) { // remove trailing comma
		 * buffer.setLength(buffer.length() - 2); }
		 * fTargetOrderText.setText(buffer.toString());
		 */
	}

	/**
	 * Returns all targets in the buildfile.
	 * 
	 * @return all targets in the buildfile
	 */
	private ITask[] getTargets() {
		if (fAllTargets == null || isDirty()) {
			fAllTargets = null;
			fDefaultTarget = null;
			setDirty(false);
			setErrorMessage(null);
			setMessage(null);

			final String expandedLocation = validateLocation();
			if (expandedLocation == null) {
				return fAllTargets;
			}
			final CoreException[] exceptions = new CoreException[1];
			try {
				IRunnableWithProgress operation = new IRunnableWithProgress() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.eclipse.jface.operation.IRunnableWithProgress#run
					 * (org.eclipse.core.runtime.IProgressMonitor)
					 */
					@Override
					public void run(IProgressMonitor monitor) {
						try {
							fAllTargets = JSBuildFileUtil.getTargets(expandedLocation,
									fLaunchConfiguration);
						} catch (CoreException ce) {
							exceptions[0] = ce;
						}

					}
				};

				IRunnableContext context = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				if (context == null) {
					context = getLaunchConfigurationDialog();
				}

				ISchedulingRule rule = null;
				if (!ResourcesPlugin.getWorkspace().isTreeLocked()) {
					// only set a scheduling rule if not in a resource change
					// callback
					rule = JSBuildFileUtil.getFileForLocation(expandedLocation);
				}
				PlatformUI.getWorkbench().getProgressService()
						.runInUI(context, operation, rule);
			} catch (InvocationTargetException e) {
				Logger.logException(
						"Internal error occurred retrieving targets", e.getTargetException()); //$NON-NLS-1$
				setErrorMessage(JSBuildFileLaunchConfigurationMessages.AntTargetsTab_1);
				fAllTargets = null;
				return null;
			} catch (InterruptedException e) {
				Logger.logException(
						"Internal error occurred retrieving targets", e); //$NON-NLS-1$
				setErrorMessage(JSBuildFileLaunchConfigurationMessages.AntTargetsTab_1);
				fAllTargets = null;
				return null;
			}

			if (exceptions[0] != null) {
				IStatus exceptionStatus = exceptions[0].getStatus();
				IStatus[] children = exceptionStatus.getChildren();
				StringBuffer message = new StringBuffer(
						exceptions[0].getMessage());
				for (int i = 0; i < children.length; i++) {
					message.append(' ');
					IStatus childStatus = children[i];
					message.append(childStatus.getMessage());
				}
				setErrorMessage(message.toString());
				fAllTargets = null;
				return fAllTargets;
			}

			if (fAllTargets == null || fAllTargets.length  < 1) {
				// if an error was not thrown during parsing then having no
				// targets is valid (Ant 1.6.*)
				return fAllTargets;
			}

			ITask target = fAllTargets[0];
			IJSBuildFile projectNode = target.getBuildFile();
			// setErrorMessageFromNode(projectNode);
			for (int i = 0; i < fAllTargets.length; i++) {
				target = fAllTargets[i];
				if (target.isDefault()) {
					fDefaultTarget = target;
				}
				// setErrorMessageFromNode(target);
			}
		}

		return fAllTargets;
	}

	private void setErrorMessageFromNode(IJSBuildFile node) {
		if (getErrorMessage() != null) {
			return;
		}
		/*
		 * if (node.isErrorNode() || node.isWarningNode()) { String message =
		 * node.getProblemMessage(); if (message != null) {
		 * setErrorMessage(message); } else {
		 * setErrorMessage(JSBuildFileLaunchConfigurationMessages
		 * .AntTargetsTab_0); } }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.
	 * debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse
	 * .debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		fInitializing = true;
		fLaunchConfiguration = configuration;
		fOrderedTargets = new ArrayList<ITask>();
		setErrorMessage(null);
		setMessage(null);
		setDirty(true);
		// boolean hideInternal = false;
		// try {
		// hideInternal =
		// fLaunchConfiguration.getAttribute(IAntLaunchConstants.ATTR_HIDE_INTERNAL_TARGETS,
		// false);
		// }
		// catch (CoreException e) {
		// Logger.logException(e);
		// }
		// fFilterInternalTargets.setSelection(hideInternal);
		// handleFilterTargetsSelected();
		// int sort = SORT_NONE;
		// try {
		// sort =
		// fLaunchConfiguration.getAttribute(IAntLaunchConstants.ATTR_SORT_TARGETS,
		// sort);
		// }
		// catch (CoreException e) {
		// Logger.logException(e);
		// }
		// fSortButton.setSelection(sort != SORT_NONE);
		// setSort(sort);
		String configTargets = null;
		String newLocation = null;

		try {
			configTargets = configuration.getAttribute(
					IJSBuildFileLaunchConstants.ATTR_BUILDFILE_TASKS,
					(String) null);
			newLocation = configuration.getAttribute(
					IExternalToolConstants.ATTR_LOCATION, (String) null);
		} catch (CoreException ce) {
			Logger.logException(
					JSBuildFileLaunchConfigurationMessages.AntTargetsTab_Error_reading_configuration_12,
					ce);
		}

		if (newLocation == null) {
			fAllTargets = null;
			initializeForNoTargets();
			return;
		}

		ITask[] allTargetNodes = getTargets();
		if (allTargetNodes == null) {
			initializeForNoTargets();
			return;
		}

		String[] targetNames = new String[] {}; // TODO
												// JSBuildFileUtil.parseRunTargets(configTargets);
		if (targetNames.length == 0) {
			fTableViewer.setAllChecked(false);
			setExecuteInput(allTargetNodes);
			if (fDefaultTarget != null) {
				fOrderedTargets.add(fDefaultTarget);
				fTableViewer.setChecked(fDefaultTarget, true);
				updateSelectionCount();
				updateLaunchConfigurationDialog();
			}
			fInitializing = false;
			return;
		}

		setExecuteInput(allTargetNodes);
		fTableViewer.setAllChecked(false);
		for (int i = 0; i < targetNames.length; i++) {
			for (int j = 0; j < fAllTargets.length; j++) {
				if (targetNames[i].equals(fAllTargets[j].getLabel())) {
					fOrderedTargets.add(fAllTargets[j]);
					fTableViewer.setChecked(fAllTargets[j], true);
				}
			}
		}
		updateSelectionCount();
		fInitializing = false;
	}

	private void initializeForNoTargets() {
		setExecuteInput(new ITask[0]);
		fTableViewer.setInput(new ITask[0]);
		fInitializing = false;
	}

	/**
	 * Sets the execute table's input to the given input.
	 */
	private void setExecuteInput(Object input) {
		fTableViewer.setInput(input);
		updateSelectionCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse
	 * .debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// attribute added in 3.0, so null must be used instead of 0 for
		// backwards compatibility
		/*
		 * if (fSortDirection != SORT_NONE) {
		 * configuration.setAttribute(IAntLaunchConstants.ATTR_SORT_TARGETS,
		 * fSortDirection); } else {
		 * configuration.setAttribute(IAntLaunchConstants.ATTR_SORT_TARGETS,
		 * (String) null); }
		 * 
		 * if (fOrderedTargets.size() == 1) { ITask item =
		 * fOrderedTargets.get(0); if (item.isDefaultTarget()) {
		 * configuration.setAttribute(IAntLaunchConstants.ATTR_ANT_TARGETS,
		 * (String) null); return; } } else if (fOrderedTargets.size() == 0) {
		 * configuration.setAttribute(IAntLaunchConstants.ATTR_ANT_TARGETS,
		 * (String) null); return; }
		 * 
		 * StringBuffer buff = new StringBuffer(); Iterator<ITask> iter =
		 * fOrderedTargets.iterator(); String targets = null; while
		 * (iter.hasNext()) { ITask item = iter.next();
		 * buff.append(item.getName()); buff.append(','); } if (buff.length() >
		 * 0) { targets = buff.toString(); }
		 * 
		 * configuration.setAttribute(IAntLaunchConstants.ATTR_ANT_TARGETS,
		 * targets);
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	@Override
	public String getName() {
		return JSBuildFileLaunchConfigurationMessages.AntTargetsTab_Tar_gets_14;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug
	 * .core.ILaunchConfiguration)
	 */
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (fAllTargets == null || isDirty()) {
			if (getErrorMessage() != null && !isDirty()) {
				// error in parsing;
				return false;
			}
			// targets not up to date and no error message...we have not parsed
			// recently
			initializeFrom(launchConfig);
			if (getErrorMessage() != null) {
				// error in parsing;
				return false;
			}
		}

		setErrorMessage(null);
		return super.isValid(launchConfig);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.AbstractLaunchConfigurationTab#setDirty(boolean)
	 */
	@Override
	protected void setDirty(boolean dirty) {
		// provide package visibility
		super.setDirty(dirty);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#activated(org.eclipse.debug
	 * .core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		if (isDirty()) {
			super.activated(workingCopy);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#deactivated(org.eclipse.
	 * debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		if (fOrderedTargets.size() == 0) {
			// set the dirty flag so that the state will be reinitialized on
			// activation
			setDirty(true);
		}
	}

	private String validateLocation() {
		String expandedLocation = null;
		String location = null;
		IStringVariableManager manager = VariablesPlugin.getDefault()
				.getStringVariableManager();
		try {
			location = fLaunchConfiguration.getAttribute(
					IExternalToolConstants.ATTR_LOCATION, (String) null);
			if (location == null) {
				return null;
			}

			expandedLocation = manager.performStringSubstitution(location);
			if (expandedLocation == null) {
				return null;
			}
			File file = new File(expandedLocation);
			if (!file.exists()) {
				setErrorMessage(JSBuildFileLaunchConfigurationMessages.AntTargetsTab_15);
				return null;
			}
			if (!file.isFile()) {
				setErrorMessage(JSBuildFileLaunchConfigurationMessages.AntTargetsTab_16);
				return null;
			}

			return expandedLocation;

		} catch (CoreException e1) {
			if (location != null) {
				try {
					manager.validateStringVariables(location);
					setMessage(JSBuildFileLaunchConfigurationMessages.AntTargetsTab_17);
					return null;
				} catch (CoreException e2) {// invalid variable
					setErrorMessage(e2.getStatus().getMessage());
					return null;
				}
			}

			setErrorMessage(e1.getStatus().getMessage());
			return null;
		}
	}

	protected boolean isTargetSelected() {
		return !fOrderedTargets.isEmpty();
	}

	protected IContentProvider getContentProvider() {
		return new JSBuildFileContentProvider();
	}

	protected IBaseLabelProvider getLabelProvider() {
		return new TaskTableLabelProvider();
	}

}
