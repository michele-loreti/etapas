/**
 * 
 */
package org.cmg.tapas.ccsp.extensions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.ccsp.runtime.CCSPAction;
import org.cmg.tapas.ccsp.runtime.CCSPModel;
import org.cmg.tapas.ccsp.runtime.CCSPProcess;
import org.cmg.tapas.ccsp.runtime.CCSPState;
import org.cmg.tapas.ccsp.runtime.CCSPUtil;
import org.cmg.tapas.ccsp.runtime.CCSPProcess.ProcessType;
import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationChecker;
import org.cmg.tapas.extensions.TAPAsLTSBuilderProvider;
import org.cmg.tapas.pa.LTSGraph;
import org.cmg.tapas.views.LTSContentProvider;
import org.cmg.tapas.views.LTSLabelProvider;
import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
import org.cmg.tapas.views.TAPAsGraphElementLabelProvider;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

/**
 * @author loreti
 *
 */
public class CCSPLTSBuilderProvider implements TAPAsLTSBuilderProvider {

	private CCSPModel ccspModel;

	/**
	 * 
	 */
	public CCSPLTSBuilderProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsLTSBuilderProvider#getContentProvider()
	 */
	@Override
	public TAPAsGraphElementContentProvider getContentProvider() {
		return new LTSContentProvider<CCSPProcess,CCSPAction>();
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
		if (ccspModel != null) {
			return ccspModel.getSystems().toArray(new String[ccspModel.getSystems().size()]);
		}
		return new String[] {};
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsLTSBuilderProvider#setModel(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void setModel(EObject o , IFile file ) {
		if(!(o instanceof Model)){
			ccspModel = null;
		} else {
			try {
			Model model = (Model) o;
			ccspModel = (CCSPModel)  TAPAsProjectHelper.loadClassFromProject(CCSPModel.class.getClassLoader(), 
					model.eResource(), model.getName() , file.getProject()).newInstance() ;	
			} catch (Exception e) {
				ccspModel = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsLTSBuilderProvider#generateLTS(java.lang.String)
	 */
	@Override
	public LTSGraph<?, ?> generateLTS(String process) {
		if (ccspModel == null) {
			return null;
		}
		CCSPProcess system = ccspModel.getSystem(process);
		if (system != null) {
			return CCSPUtil.getGraph(system);
		}
		return null;
	}

	@Override
	public LTSGraph<?, ?> minimizeLTS(LTSGraph<?, ?> graph) {
		LTSGraph<CCSPProcess, CCSPAction> toReturn = new LTSGraph<CCSPProcess, CCSPAction>();
		LTSGraph<CCSPProcess, CCSPAction> g = (LTSGraph<CCSPProcess, CCSPAction>) graph;	
		BisimulationChecker<CCSPProcess, CCSPAction> checker = new BisimulationChecker<CCSPProcess, CCSPAction>(g);
		checker.computeEquivalence();
		
		HashMap<CCSPProcess, ? extends HashSet<CCSPProcess>> map =  checker.getPartitionMap();
		if(map == null){
			return graph;
		}
		Set<Set<CCSPProcess>> states = new HashSet<Set<CCSPProcess>>();
		HashMap<Set<CCSPProcess>, CCSPProcess> processes = new HashMap<Set<CCSPProcess>, CCSPProcess>();
		for(CCSPProcess s : map.keySet()){
			states.add(map.get(s));
		}
		int i = 1;
		for(Set<CCSPProcess> s : states){
			processes.put(s, new CCSPState("X", String.valueOf(i), i) {
				
				@Override
				protected void initializeTransistionTable() {
				
					
				}
			});
			i++;
		}
		for(Set<CCSPProcess> current : states){
			toReturn.addState(processes.get(current));
			CCSPProcess currentState = (CCSPProcess)(current.toArray())[0];
			HashMap<CCSPAction, Set<CCSPProcess>> actMap = currentState.getNext();
			for(CCSPAction action : actMap.keySet()){
				for(CCSPProcess dest : actMap.get(action)){
					Set<CCSPProcess> destSet = map.get(dest);
					CCSPProcess dst = processes.get(destSet);
					toReturn.addState(dst);
					toReturn.addEdge(processes.get(current), action, dst);
					((CCSPState) processes.get(current)).addNext(action, dst);
				}
			}
		}
		return toReturn;
	}

}
