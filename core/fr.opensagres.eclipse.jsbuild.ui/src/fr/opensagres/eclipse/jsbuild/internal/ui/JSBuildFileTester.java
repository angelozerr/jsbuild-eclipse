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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

import fr.opensagres.eclipse.jsbuild.core.JSBuildFileFactoryManager;

/**
 * Property Tester for a IResource receiver object
 * 
 * Property to be tested: "isJSBuildFile"
 * 
 */
public class JSBuildFileTester extends
		org.eclipse.core.expressions.PropertyTester {
	private static final String IS_JS_BUILD_FILE_PROPERTY = "isJSBuildFile";

	public JSBuildFileTester() {
		// Default constructor is required for property tester
	}

	/**
	 * Returns true if the receiver object is a JavaScript build file and false
	 * otherwise.
	 * 
	 * @return true if the receiver object is a JavaScript build file and false
	 *         otherwise.
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if (IS_JS_BUILD_FILE_PROPERTY.equals(property))
			return testIsJSBuildFile(receiver,
					expectedValue != null ? expectedValue.toString() : "");

		return false;
	}

	private boolean testIsJSBuildFile(Object receiver, String expectedFactoryId) {
		if (receiver instanceof IAdaptable) {
			IResource resource = (IResource) ((IAdaptable) receiver)
					.getAdapter(IResource.class);
			if (resource != null && resource.getType() == IResource.FILE) {
				IFile file = (IFile) resource;
				String actualFactoryId = JSBuildFileFactoryManager
						.findFactoryId(file);
				if (expectedFactoryId == null || expectedFactoryId.length() < 1) {
					return actualFactoryId != null;
				}
				return (actualFactoryId != null && actualFactoryId
						.equals(expectedFactoryId));
			}
		}

		return false;
	}

}
