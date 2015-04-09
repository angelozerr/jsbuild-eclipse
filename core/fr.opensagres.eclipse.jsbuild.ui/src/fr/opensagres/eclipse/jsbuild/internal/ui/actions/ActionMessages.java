package fr.opensagres.eclipse.jsbuild.internal.ui.actions;

import org.eclipse.osgi.util.NLS;

public class ActionMessages extends NLS {

	private static final String BUNDLE_NAME = "fr.opensagres.eclipse.jsbuild.internal.ui.actions.ActionMessages";//$NON-NLS-1$

	public static String OpenAction_error_title;
	public static String OpenAction_label;
	public static String OpenAction_tooltip;
	public static String OpenAction_description;
	public static String OpenWithMenu_label;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ActionMessages.class);
	}
}
