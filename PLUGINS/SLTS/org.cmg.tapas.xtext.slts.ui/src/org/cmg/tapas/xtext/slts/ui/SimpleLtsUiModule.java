/*
 * generated by Xtext
 */
package org.cmg.tapas.xtext.slts.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.wizard.IProjectCreator;

/**
 * Use this class to register components to be used within the IDE.
 */
public class SimpleLtsUiModule extends org.cmg.tapas.xtext.slts.ui.AbstractSimpleLtsUiModule {
	public SimpleLtsUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
	
	@Override
	public Class<? extends IProjectCreator> bindIProjectCreator() {
		return org.cmg.tapas.xtext.slts.ui.wizard.CustomSimpleLtsProjectCreator.class;
	}
}