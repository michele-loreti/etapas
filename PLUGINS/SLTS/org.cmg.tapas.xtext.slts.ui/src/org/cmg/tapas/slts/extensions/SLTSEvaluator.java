package org.cmg.tapas.slts.extensions;

import java.util.Set;

import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.slts.runtime.SltsState;

public class SLTSEvaluator extends Evaluator<SltsState, Set<String>> {

	@Override
	public Set<String> eval(SltsState s) {
		return s.getLabels();
	}

}
