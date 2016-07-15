package org.cmg.tapas.clts.extensions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.xtext.clts.composedLts.Model;
import org.cmg.tapas.clts.runtime.CltsAction;
import org.cmg.tapas.clts.runtime.CltsModule;
import org.cmg.tapas.clts.runtime.CltsProcess;
import org.cmg.tapas.clts.runtime.CltsState;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationChecker;
import org.cmg.tapas.extensions.TAPAsLTSBuilderProvider;
import org.cmg.tapas.pa.LTSGraph;
import org.cmg.tapas.views.LTSContentProvider;
import org.cmg.tapas.views.LTSLabelProvider;
import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
import org.cmg.tapas.views.TAPAsGraphElementLabelProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;


public class CLTSLTSBuilderProvider implements TAPAsLTSBuilderProvider {
	
	private CltsModule cltsModel;
	
	public CLTSLTSBuilderProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public TAPAsGraphElementContentProvider getContentProvider() {
		return new LTSContentProvider<CltsProcess, CltsAction>();
	}

	@Override
	public TAPAsGraphElementLabelProvider getLabelProvier() {
		return new LTSLabelProvider();
	}

	@Override
	public String[] getProcesses() {
		if( cltsModel != null ){
			return cltsModel.getSystems().toArray(new String[cltsModel.getSystems().size()]);
		}
		return new String[] {};
	}

	@Override
	public void setModel(EObject o, IFile file) {
		if(!(o instanceof Model)){
			cltsModel = null;
		} else {
			try {
			Model module = (Model) o;
			cltsModel = (CltsModule)  TAPAsProjectHelper.loadClassFromProject(CltsModule.class.getClassLoader(), 
					module.eResource(), module.getName() , file.getProject()).newInstance() ;	
			} catch (Exception e) {
				System.out.println("Exception: "+ e);
				cltsModel = null;
			}
		}
	}

	@Override
	public LTSGraph<?, ?> generateLTS(String process) {
		if( cltsModel == null){
			return null;
		}
		CltsProcess lts = cltsModel.getSystem(process);
		if(lts != null){
			LTSGraph<CltsProcess, CltsAction> graph = new LTSGraph<CltsProcess, CltsAction>() ;
			graph.addState(lts);
			graph.expand();
			return graph;
		}
		return null;
	}

	@Override
	public LTSGraph<?, ?> minimizeLTS(LTSGraph<?, ?> graph) {
		LTSGraph<CltsProcess, CltsAction> toReturn = new LTSGraph<CltsProcess, CltsAction>();
		LTSGraph<CltsProcess, CltsAction> g = (LTSGraph<CltsProcess, CltsAction>) graph;
		BisimulationChecker<CltsProcess, CltsAction> checker = new BisimulationChecker<CltsProcess, CltsAction>(g);
		checker.computeEquivalence(new CLTSEvaluator());
		HashMap<CltsProcess, ? extends HashSet<CltsProcess>> map =  checker.getPartitionMap();
		if(map == null){
			return graph;
		}
		
		Set<Set<CltsProcess>> states = new HashSet<Set<CltsProcess>>();
		HashMap<Set<CltsProcess>, CltsState> processes = new HashMap<Set<CltsProcess>, CltsState>();
		for(CltsProcess s : map.keySet()){
			states.add(map.get(s));
		}
		int i = 1;
		for(Set<CltsProcess> s : states){
			processes.put( s , new CltsState( "s" + String.valueOf(i), " ", i ) );
			i++;
		}
		for(Set<CltsProcess> current : states){
			toReturn.addState(processes.get(current));
			CltsProcess currentState = (CltsProcess)(current.toArray())[0];
			HashMap<CltsAction, Set<CltsProcess>> actMap = currentState.getNext();
			for(CltsAction action : actMap.keySet()){
				for(CltsProcess dest : actMap.get(action)){
					Set<CltsProcess> destSet = map.get(dest);
					CltsState dst = processes.get(destSet);
					toReturn.addState(dst);
					toReturn.addEdge(processes.get(current), action, dst);
					processes.get(current).addNext(action, dst);
				}
			}
		}
		return toReturn;
	}

}
