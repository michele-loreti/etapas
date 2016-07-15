package org.cmg.tapas.xtext.ccsp.ui.wizard;

import java.util.List;
import org.cmg.tapas.TAPAsProjectHelper;

public class CustomCCSPSpecificationProjectCreator extends CCSPSpecificationProjectCreator {
	protected static final String CCSP_RUNTIME = "org.cmg.tapas.ccsp.runtime";
	
	@Override
	protected List<String> getRequiredBundles() {
		List<String> result = super.getRequiredBundles();
		result.addAll(TAPAsProjectHelper.addTapasBudles(result));
		result.add(CCSP_RUNTIME);
		return result;
	}

}
