package fr.opensagres.eclipse.jsbuild.internal.ui;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileNode;
import fr.opensagres.eclipse.jsbuild.core.Location;

public class EditorUtility {

	/**
	 * Tests if a is currently shown in an editor
	 * 
	 * @return the IEditorPart if shown, null if element is not open in an
	 *         editor
	 */
	public static IEditorPart isOpenInEditor(IJSBuildFileNode inputElement) {
		IEditorInput input = new FileEditorInput(inputElement.getBuildFile()
				.getBuildFileResource());
		IWorkbenchPage p = JSBuildFileUIPlugin.getActivePage();
		if (p != null) {
			return p.findEditor(input);
		}
		return null;
	}

	public static void openInEditor(IWorkbenchPage page,
			IEditorDescriptor editorDescriptor, IJSBuildFileNode node) {
		IEditorPart editorPart = null;
		IFile fileResource = node.getBuildFile().getBuildFileResource();
		try {
			if (editorDescriptor == null) {
				editorPart = page.openEditor(new FileEditorInput(fileResource),
						IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
			} else {
				editorPart = page.openEditor(new FileEditorInput(fileResource),
						editorDescriptor.getId());
			}
		} catch (PartInitException e) {
			Logger.logException(MessageFormat.format(
					JSBuildFileUIMessages.JSBuildFileUtil_0,
					new Object[] { fileResource.getLocation().toOSString() }),
					e);
		}
		revealInEditor(editorPart, node, page);
	}

	public static void revealInEditor(IEditorPart editorPart,
			IJSBuildFileNode element) {
		revealInEditor(editorPart, element, null);
	}

	public static void revealInEditor(IEditorPart editorPart,
			IJSBuildFileNode element, IWorkbenchPage page) {
		if (element == null)
			return;

		ITextEditor textEditor = null;
		if (editorPart instanceof ITextEditor)
			textEditor = (ITextEditor) editorPart;
		else if (editorPart instanceof IAdaptable)
			textEditor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
		if (textEditor != null) {
			IDocument document = textEditor.getDocumentProvider().getDocument(
					editorPart.getEditorInput());
			Location location = element.getLocation(document != null ? document
					.get() : null);
			if (location != null) {
				int start = location.getStart();
				if (start > 0) {
					int length = location.getLength();
					textEditor.selectAndReveal(start, length);
					if (page != null) {
						page.activate(editorPart);
					}
				}
			}
		} else {
			Location location = element.getLocation(null);
			if (location != null) {
				IFile fileResource = element.getBuildFile()
						.getBuildFileResource();
				int start = location.getStart();
				try {
					IMarker marker = fileResource
							.createMarker("org.eclipse.core.resources.textmarker");
					marker.setAttribute("lineNumber", start);
					editorPart = IDE.openEditor(page, marker, true);
					marker.delete();
				} catch (CoreException e) {
					Logger.logException(MessageFormat.format(
							JSBuildFileUIMessages.JSBuildFileUtil_0,
							new Object[] { fileResource.getLocation()
									.toOSString() }), e);
				}
			}
		}

	}
}
