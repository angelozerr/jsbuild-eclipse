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
package fr.opensagres.eclipse.jsbuild.internal.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import fr.opensagres.eclipse.jsbuild.core.IJSBuildFile;
import fr.opensagres.eclipse.jsbuild.core.IJSBuildFileFactory;

/**
 * This class reads the plugin manifests to provide factory to create instance
 * of {@link IJSBuildFile}. Here a sample :
 * 
 * <pre>
 * <extension point="fr.opensagres.eclipse.jsbuild.core.jsBuildFactories">
 *     <jsBuildFactory
 *         id="fr.opensagres.eclipse.jsbuild.core.grunt"
 *         name="Grunt"
 *         class="tern.eclipse.ide.grunt.internal.GruntFileFactory" >
 *     </jsBuildFactory>
 *   </extension>
 * </pre>
 */
public class JSBuildFileFactoriesRegistryReader {

	protected static final String EXTENSION_POINT_ID = "jsBuildFactories"; //$NON-NLS-1$
	protected static final String TAG_CONTRIBUTION = "jsBuildFactory"; //$NON-NLS-1$

	private static JSBuildFileFactoriesRegistryReader INSTANCE = null;

	private final Map<String, IJSBuildFileFactory> factories;

	public static JSBuildFileFactoriesRegistryReader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new JSBuildFileFactoriesRegistryReader();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
	}

	public JSBuildFileFactoriesRegistryReader() {
		factories = new HashMap<String, IJSBuildFileFactory>();
	}

	/**
	 * read from plugin registry and parse it.
	 */
	protected void readRegistry() {
		IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = pluginRegistry.getExtensionPoint(
				JSBuildFileCorePlugin.getDefault().getBundle()
						.getSymbolicName(), EXTENSION_POINT_ID);
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
				IJSBuildFileFactory factory = (IJSBuildFileFactory) element
						.createExecutableExtension("class");
				this.factories.put(id, factory);
			} catch (CoreException e) {
				Logger.logException(e);
			}
		}

	}

	/**
	 * Returns the factory id for the given file and null otherwise.
	 * 
	 * @param file
	 *            (Gruntfile.js, gulpfile.js, etc)
	 * @return the factory id for the given file and null otherwise.
	 */
	public String findFactoryId(IFile file) {
		Set<Entry<String, IJSBuildFileFactory>> entries = factories.entrySet();
		for (Entry<String, IJSBuildFileFactory> entry : entries) {
			if (entry.getValue().isAdaptFor(file)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Create an instance of {@link IJSBuildFile} from the given file.
	 * 
	 * @param file
	 *            (Gruntfile.js, gulpfile.js).
	 * @param factoryId
	 *            the factory id.
	 * @return an instance of {@link IJSBuildFile} from the given file.
	 */
	public IJSBuildFile create(IFile file, String factoryId) {
		IJSBuildFileFactory factory = factories.get(factoryId);
		if (factory == null) {
			return null;
		}
		return factory.create(file, factoryId);
	}

}
