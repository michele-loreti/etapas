package org.cmg.tapas.clts.extensions;

import java.util.Set;

import org.cmg.tapas.clts.runtime.CltsProcess;
import org.cmg.tapas.core.graph.algorithms.Evaluator;

public class CLTSEvaluator extends Evaluator<CltsProcess, Set<String>>{

	@Override
	public Set<String> eval(CltsProcess s) {
		return s.getLabels();
	}

}
