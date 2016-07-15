/**
 * 
 */
package org.cmg.tapas.xtext.slts.ui.wizard;

import java.util.List;

import org.cmg.tapas.TAPAsProjectHelper;

/**
 * @author loreti
 *
 */
public class CustomSimpleLtsProjectCreator extends SimpleLtsProjectCreator {

	protected static final String SLTS_RUNTIME = "org.cmg.tapas.slts.runtime";
	protected static final String SLTS_EXTENSIONS = "org.cmg.tapas.slts.extensions";
	protected static final String SLTS_XTEXT = "org.cmg.tapas.xtext.slts";
	
	@Override
	protected List<String> getRequiredBundles() {
		List<String> result = super.getRequiredBundles();
		result.addAll(TAPAsProjectHelper.addTapasBudles(result));
		result.add(SLTS_RUNTIME);
		result.add(SLTS_EXTENSIONS);
		result.add(SLTS_XTEXT);
		return result;
	}
	
	
}
