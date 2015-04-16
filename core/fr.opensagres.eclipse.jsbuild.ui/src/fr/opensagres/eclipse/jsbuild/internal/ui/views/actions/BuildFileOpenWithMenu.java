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
package fr.opensagres.eclipse.jsbuild.internal.ui.views.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.internal.ui.EditorUtility;
import fr.opensagres.eclipse.jsbuild.internal.ui.JSBuildFileUIPlugin;
import fr.opensagres.eclipse.jsbuild.internal.ui.Logger;

/**
 * 
 * Code mostly a copy of the OpenWithMenu which cannot be effectively
 * sub-classed
 */
public class BuildFileOpenWithMenu extends ContributionItem {

	private IWorkbenchPage fPage;
	private IEditorRegistry fRegistry = PlatformUI.getWorkbench()
			.getEditorRegistry();
	private static final String SYSTEM_EDITOR_ID = PlatformUI.PLUGIN_ID
			+ ".SystemEditor"; //$NON-NLS-1$

	private static Map<ImageDescriptor, Image> imageCache = new Hashtable<ImageDescriptor, Image>(
			11);

	private IJSBuildFileNode fNode;

	/**
	 * The id of this action.
	 */
	public static final String ID = JSBuildFileUIPlugin.PLUGIN_ID
			+ ".JSBuildFileOpenWithMenu"; //$NON-NLS-1$

	public BuildFileOpenWithMenu(IWorkbenchPage page) {
		super(ID);
		this.fPage = page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IContributionItem#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		for (Image image : imageCache.values()) {
			image.dispose();
		}
		imageCache.clear();
	}

	/**
	 * Returns an image to show for the corresponding editor descriptor.
	 * 
	 * @param editorDesc
	 *            the editor descriptor, or <code>null</code> for the system
	 *            editor
	 * @return the image or <code>null</code>
	 */
	private Image getImage(IEditorDescriptor editorDesc) {
		ImageDescriptor imageDesc = getImageDescriptor(editorDesc);
		if (imageDesc == null) {
			return null;
		}
		Image image = imageCache.get(imageDesc);
		if (image == null) {
			image = imageDesc.createImage();
			imageCache.put(imageDesc, image);
		}
		return image;
	}

	/**
	 * Returns the image descriptor for the given editor descriptor, or
	 * <code>null</code> if it has no image.
	 */
	private ImageDescriptor getImageDescriptor(IEditorDescriptor editorDesc) {
		ImageDescriptor imageDesc = null;
		if (editorDesc == null) {
			imageDesc = fRegistry.getImageDescriptor(fNode.getBuildFile()
					.getBuildFileResource().getName());
		} else {
			imageDesc = editorDesc.getImageDescriptor();
		}
		if (imageDesc == null && editorDesc != null) {
			if (editorDesc.getId().equals(SYSTEM_EDITOR_ID)) {
				imageDesc = getSystemEditorImageDescriptor(fNode.getBuildFile()
						.getBuildFileResource().getFileExtension());
			}
		}
		return imageDesc;
	}

	/**
	 * Return the image descriptor of the system editor that is registered with
	 * the OS to edit files of this type. <code>null</code> if none can be
	 * found.
	 */
	private ImageDescriptor getSystemEditorImageDescriptor(String extension) {
		Program externalProgram = null;
		if (extension != null) {
			externalProgram = Program.findProgram(extension);
		}
		if (externalProgram == null) {
			return null;
		}
		return new EditorImageDescriptor(externalProgram);
	}

	/**
	 * Creates the menu item for the editor descriptor.
	 * 
	 * @param menu
	 *            the menu to add the item to
	 * @param descriptor
	 *            the editor descriptor, or null for the system editor
	 * @param preferredEditor
	 *            the descriptor of the preferred editor, or <code>null</code>
	 */
	private void createMenuItem(Menu menu, final IEditorDescriptor descriptor,
			final IEditorDescriptor preferredEditor) {
		// XXX: Would be better to use bold here, but SWT does not support it.
		final MenuItem menuItem = new MenuItem(menu, SWT.RADIO);
		boolean isPreferred = preferredEditor != null
				&& descriptor.getId().equals(preferredEditor.getId());
		menuItem.setSelection(isPreferred);
		menuItem.setText(descriptor.getLabel());
		Image image = getImage(descriptor);
		if (image != null) {
			menuItem.setImage(image);
		}
		Listener listener = new Listener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.
			 * widgets.Event)
			 */
			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					if (menuItem.getSelection()) {
						openEditor(descriptor);
					}
					break;
				default:
					break;
				}
			}
		};
		menuItem.addListener(SWT.Selection, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets
	 * .Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		IFile fileResource = fNode.getBuildFile().getBuildFileResource();
		if (fileResource == null) {
			return;
		}

		IEditorDescriptor defaultEditor = fRegistry
				.findEditor(IEditorRegistry.SYSTEM_INPLACE_EDITOR_ID); // should
																		// not
																		// be
																		// null
		IEditorDescriptor preferredEditor = IDE.getDefaultEditor(fileResource); // may
																				// be
																				// null

		IEditorDescriptor[] editors = fRegistry.getEditors(fileResource
				.getName());
		Arrays.sort(editors, new Comparator<IEditorDescriptor>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Comparator#compare(java.lang.Object,
			 * java.lang.Object)
			 */
			@Override
			public int compare(IEditorDescriptor o1, IEditorDescriptor o2) {
				String s1 = o1.getLabel();
				String s2 = o2.getLabel();
				// Return true if elementTwo is 'greater than' elementOne
				return s1.compareToIgnoreCase(s2);
			}
		});
		IEditorDescriptor javaScriptEditor = fRegistry
				.findEditor("org.eclipse.wst.jsdt.ui.CompilationUnitEditor"); //$NON-NLS-1$

		boolean defaultFound = false;
		boolean antFound = false;
		List<String> alreadyAddedEditors = new ArrayList<String>(editors.length);
		for (int i = 0; i < editors.length; i++) {
			IEditorDescriptor editor = editors[i];
			if (alreadyAddedEditors.contains(editor.getId())) {
				continue;
			}
			createMenuItem(menu, editor, preferredEditor);
			if (defaultEditor != null
					&& editor.getId().equals(defaultEditor.getId())) {
				defaultFound = true;
			}
			if (javaScriptEditor != null
					&& editor.getId().equals(javaScriptEditor.getId())) {
				antFound = true;
			}
			alreadyAddedEditors.add(editor.getId());

		}

		// Only add a separator if there is something to separate
		if (editors.length > 0) {
			new MenuItem(menu, SWT.SEPARATOR);
		}

		// Add ant editor.
		if (!antFound && javaScriptEditor != null) {
			createMenuItem(menu, javaScriptEditor, preferredEditor);
		}

		// Add default editor.
		if (!defaultFound && defaultEditor != null) {
			createMenuItem(menu, defaultEditor, preferredEditor);
		}

		// Add system editor.
		IEditorDescriptor descriptor = fRegistry
				.findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
		createMenuItem(menu, descriptor, preferredEditor);
		createDefaultMenuItem(menu, fileResource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}

	/**
	 * Opens the given editor on the selected file.
	 * 
	 * @param editor
	 *            the editor descriptor, or <code>null</code> for the system
	 *            editor
	 */
	private void openEditor(IEditorDescriptor editorDescriptor) {
		EditorUtility.openInEditor(fPage, editorDescriptor, fNode);
	}

	/**
	 * Creates the menu item for the default editor
	 * 
	 * @param menu
	 *            the menu to add the item to
	 * @param file
	 *            the file being edited
	 * @param registry
	 *            the editor registry
	 */
	private void createDefaultMenuItem(Menu menu, final IFile fileResource) {
		final MenuItem menuItem = new MenuItem(menu, SWT.RADIO);
		menuItem.setSelection(IDE.getDefaultEditor(fileResource) == null);
		menuItem.setText(JSBuildFileViewActionMessages.JSBuildFileViewOpenWithMenu_Default_Editor_4);

		Listener listener = new Listener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.
			 * widgets.Event)
			 */
			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					if (menuItem.getSelection()) {
						IDE.setDefaultEditor(fileResource, null);
						try {
							IDE.openEditor(fPage, fileResource, true);
						} catch (PartInitException e) {
							Logger.logException(
									MessageFormat
											.format(JSBuildFileViewActionMessages.JSBuildFileViewOpenWithMenu_Editor_failed,
													new Object[] { fileResource
															.getLocation()
															.toOSString() }), e);
						}
					}
					break;
				default:
					break;
				}
			}
		};

		menuItem.addListener(SWT.Selection, listener);
	}

	public void setNode(IJSBuildFileNode node) {
		fNode = node;
	}
}
