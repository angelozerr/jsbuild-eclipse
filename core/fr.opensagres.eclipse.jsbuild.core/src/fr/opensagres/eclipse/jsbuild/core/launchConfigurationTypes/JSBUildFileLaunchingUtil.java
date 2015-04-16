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
package fr.opensagres.eclipse.jsbuild.core.launchConfigurationTypes;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Utilities for JavaScript build file launch.
 *
 */
public class JSBUildFileLaunchingUtil {

	public static final String ATTRIBUTE_SEPARATOR = ","; //$NON-NLS-1$;

	/**
	 * Returns an array of targets to be run, or <code>null</code> if none are
	 * specified (indicating the default target or implicit target should be
	 * run).
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return array of target names, or <code>null</code>
	 * @throws CoreException
	 *             if unable to access the associated attribute
	 */
	public static String[] getTargetNames(ILaunchConfiguration configuration)
			throws CoreException {
		String attribute = null;
		/*
		 * if (IAntLaunchConstants.ID_ANT_BUILDER_LAUNCH_CONFIGURATION_TYPE
		 * .equals(configuration.getType().getIdentifier())) { attribute =
		 * getTargetNamesForAntBuilder(configuration); }
		 */
		if (attribute == null) {
			attribute = configuration.getAttribute(
					IJSBuildFileLaunchConstants.ATTR_BUILDFILE_TASKS,
					(String) null);
			if (attribute == null) {
				return null;
			}
		}

		return JSBUildFileLaunchingUtil.parseRunTargets(attribute);
	}

	/**
	 * Returns the list of target names to run
	 * 
	 * @param extraAttibuteValue
	 *            the external tool's extra attribute value for the run targets
	 *            key.
	 * @return a list of target names
	 */
	public static String[] parseRunTargets(String extraAttibuteValue) {
		return parseString(extraAttibuteValue, ATTRIBUTE_SEPARATOR);
	}

	/**
	 * Returns the list of Strings that were delimiter separated.
	 * 
	 * @param delimString
	 *            the String to be tokenized based on the delimiter
	 * @return a list of Strings
	 */
	public static String[] parseString(String delimString, String delim) {
		if (delimString == null) {
			return new String[0];
		}

		// Need to handle case where separator character is
		// actually part of the target name!
		StringTokenizer tokenizer = new StringTokenizer(delimString, delim);
		String[] results = new String[tokenizer.countTokens()];
		for (int i = 0; i < results.length; i++) {
			results[i] = tokenizer.nextToken().trim();
		}

		return results;
	}

}
