package org.cmg.tapas.xtext.clts.ui.wizard;

import java.util.List;

import org.cmg.tapas.TAPAsProjectHelper;

public class CustomComposedLtsProjectCreator extends ComposedLtsProjectCreator{
	
	protected static final String CLTS_RUNTIME = "org.cmg.tapas.clts.runtime";
	protected static final String CLTS_EXTENSIONS = "org.cmg.tapas.clts.extensions";
	
	@Override
	protected List<String> getRequiredBundles() {
		List<String> result = super.getRequiredBundles();
		result.addAll(TAPAsProjectHelper.addTapasBudles(result));
		result.add(CLTS_RUNTIME);
//		result.add(CLTS_EXTENSIONS);
		return result;
	}

}
