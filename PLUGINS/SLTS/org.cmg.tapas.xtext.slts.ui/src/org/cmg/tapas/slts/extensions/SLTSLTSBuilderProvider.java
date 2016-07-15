/**
 * 
 */
package org.cmg.tapas.slts.extensions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationChecker;
import org.cmg.tapas.extensions.TAPAsLTSBuilderProvider;
import org.cmg.tapas.pa.LTSGraph;
import org.cmg.tapas.slts.runtime.SltsAction;
import org.cmg.tapas.slts.runtime.SltsModule;
import org.cmg.tapas.slts.runtime.SltsState;
import org.cmg.tapas.views.LTSContentProvider;
import org.cmg.tapas.views.LTSLabelProvider;
import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
import org.cmg.tapas.views.TAPAsGraphElementLabelProvider;
import org.cmg.tapas.xtext.slts.simpleLts.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

/**
 * @author loreti
 *
 */
public class SLTSLTSBuilderProvider implements TAPAsLTSBuilderProvider {

	private SltsModule sltsModel;

	/**
	 * 
	 */
	public SLTSLTSBuilderProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsLTSBuilderProvider#getContentProvider()
	 */
	@Override
	public TAPAsGraphElementContentProvider getContentProvider() {
		return new LTSContentProvider<SltsState,SltsAction>();
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsLTSBuilderProvider#getLabelProvier()
	 */
	@Override
	public TAPAsGraphElementLabelProvider getLabelProvier() {
		return new LTSLabelProvider();
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsLTSBuilderProvider#getProcesses()
	 */
	@Override
	public String[] getProcesses() {
		if (sltsModel == null) {
			return new String[] {};
		}
		Set<String> states = sltsModel.getStates();
		return states.toArray(new String[ states.size() ]);
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsLTSBuilderProvider#setModel(org.eclipse.emf.ecore.EObject, org.eclipse.core.resources.IFile)
	 */
	@Override
	public void setModel(EObject o, IFile file) {
		if(!(o instanceof Model)){
			sltsModel = null;
		} else {
			try {
			Model model = (Model) o;
			sltsModel = (SltsModule)  TAPAsProjectHelper.loadClassFromProject(SltsModule.class.getClassLoader(), 
					model.eResource(), model.getName() , file.getProject()).newInstance() ;	
			} catch (Exception e) {
				sltsModel = null;
			}
		}	
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsLTSBuilderProvider#generateLTS(java.lang.String)
	 */
	@Override
	public LTSGraph<?, ?> generateLTS(String process) {
		return sltsModel.generateLts(process);
	}

	@Override
	public LTSGraph<?, ?> minimizeLTS(LTSGraph<?, ?> graph) {
		LTSGraph<SltsState, SltsAction> toReturn = new LTSGraph<SltsState, SltsAction>();
		LTSGraph<SltsState, SltsAction> g = (LTSGraph<SltsState, SltsAction>) graph;
		BisimulationChecker<SltsState, SltsAction> checker = new BisimulationChecker<SltsState, SltsAction>(g);
		checker.computeEquivalence(new SLTSEvaluator());
		HashMap<SltsState, ? extends HashSet<SltsState>> map =  checker.getPartitionMap();
		if(map == null){
			return graph;
		}
		
		Set<Set<SltsState>> states = new HashSet<Set<SltsState>>();
		HashMap<Set<SltsState>, SltsState> processes = new HashMap<Set<SltsState>, SltsState>();
		for(SltsState s : map.keySet()){
			states.add(map.get(s));
		}
		int i = 1;
		for(Set<SltsState> s : states){
			processes.put( s , new SltsState( "s" + String.valueOf(i) ) );
			i++;
		}
		for(Set<SltsState> current : states){
			toReturn.addState(processes.get(current));
			SltsState currentState = (SltsState)(current.toArray())[0];
			HashMap<SltsAction, Set<SltsState>> actMap = currentState.getNext();
			for(SltsAction action : actMap.keySet()){
				for(SltsState dest : actMap.get(action)){
					Set<SltsState> destSet = map.get(dest);
					SltsState dst = processes.get(destSet);
					toReturn.addState(dst);
					toReturn.addEdge(processes.get(current), action, dst);
					processes.get(current).addNext(action, dst);
				}
			}
		}
		return toReturn;
	}

}
