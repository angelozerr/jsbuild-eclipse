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
package fr.opensagres.eclipse.jsbuild.internal.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;

import fr.opensagres.eclipse.jsbuild.internal.ui.views.JSBuildFileView;

/**
 * This class reads the plugin manifests to customize label provider image for
 * {@link JSBuildFileView}. Here a sample :
 * 
 * <pre>
 * <extension
 * 	      point="fr.opensagres.eclipse.jsbuild.ui.labelProviders">
 * 	   <labelProvider
 * 	         id="fr.opensagres.eclipse.jsbuild.core.grunt"
 * 	         class="fr.opensagres.eclipse.jsbuild.grunt.internal.ui.GruntLabelProvider" >
 * 	   </labelProvider>
 * 	</extension>
 * </pre>
 */
public class LabelProviderRegistryReader {

	protected static final String EXTENSION_POINT_ID = "labelProviders"; //$NON-NLS-1$
	protected static final String TAG_CONTRIBUTION = "labelProvider"; //$NON-NLS-1$

	private static LabelProviderRegistryReader INSTANCE = null;

	private final Map<String, ILabelProvider> providers;

	public static LabelProviderRegistryReader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LabelProviderRegistryReader();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
	}

	public LabelProviderRegistryReader() {
		providers = new HashMap<String, ILabelProvider>();
	}

	/**
	 * read from plugin registry and parse it.
	 */
	protected void readRegistry() {
		IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = pluginRegistry.getExtensionPoint(
				JSBuildFileUIPlugin.getDefault().getBundle().getSymbolicName(),
				EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				readElement(elements[i]);
			}
		}
	}

	protected void readElement(IConfigurationElement element) {
		if (TAG_CONTRIBUTION.equals(element.getName())) {
			String id = element.getAttribute("id");
			try {
				ILabelProvider labelProvider = (ILabelProvider) element
						.createExecutableExtension("class");
				this.providers.put(id, labelProvider);
			} catch (CoreException e) {
				Logger.logException(e);
			}
		}

	}

	/**
	 * Returns the label provider for the given factory id and null otherwise.
	 * 
	 * @param factoryId
	 * @return
	 */
	public ILabelProvider getLabelProvider(String factoryId) {
		return providers.get(factoryId);
	}
}
