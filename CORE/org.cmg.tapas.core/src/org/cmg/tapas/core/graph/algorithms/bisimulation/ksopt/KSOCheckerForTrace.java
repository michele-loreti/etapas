package org.cmg.tapas.core.graph.algorithms.bisimulation.ksopt;


import java.util.HashMap;
import java.util.List;

import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.GraphData;


public class KSOCheckerForTrace< S ,A extends ActionInterface> extends KSOChecker<S, A> {

	
	public KSOCheckerForTrace(GraphData<S, A> graph) {
		super(graph);
	}

	protected <V> HashMap<S, KSOClazz<S>> computeEquivalence(
			Evaluator<S,V> initPartition, S state1, S state2
		) {		
			// reset maps
			partitionMap = new HashMap<S, KSOClazz<S>>(
				(int) (graph.getNumberOfStates()/0.75) + 1
			);
			
			// initialize queue, decorated clazzes and partition map
			KSOQueue<S> queue;
			if( initPartition==null ) 
				queue = initQueueAndPartitionMap(Evaluator.trivialEvaluator(state1));
			else		
				queue = initQueueAndPartitionMap(initPartition);
			
			// main looop	
			List<S> elements;
			List<KSOClazz<S>> clazzes;
			KSOClazz<S> splitter;
			while( !queue.isEmpty() ){
				splitter = queue.get();							 
				elements = splitter.toList();			 
				for( A action: actions ) {
					clazzes = split(action, elements); 			 
					updateSplitters(queue, clazzes);			 
				}
			}

			return partitionMap;
		}
}
