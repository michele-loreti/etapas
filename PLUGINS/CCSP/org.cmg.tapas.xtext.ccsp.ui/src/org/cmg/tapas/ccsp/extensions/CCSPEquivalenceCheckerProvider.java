/**
 * 
 */
package org.cmg.tapas.ccsp.extensions;

import java.util.LinkedList;
import java.util.Set;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.ccsp.runtime.CCSPAction;
import org.cmg.tapas.ccsp.runtime.CCSPModel;
import org.cmg.tapas.ccsp.runtime.CCSPProcess;
import org.cmg.tapas.ccsp.runtime.CCSPUtil;
import org.cmg.tapas.core.graph.GraphClosure;
import org.cmg.tapas.core.graph.GraphComposition;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.GraphInterface;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationChecker;
import org.cmg.tapas.core.graph.algorithms.branching.gv.GVChecker;
import org.cmg.tapas.core.graph.algorithms.branching.gvks.GVKSChecker;
import org.cmg.tapas.core.graph.algorithms.branching.gvks2.GVKS2Checker;
import org.cmg.tapas.core.graph.algorithms.branching.gvs.GVSChecker;
import org.cmg.tapas.core.graph.algorithms.traces.DecoratedTraceChecker;
import org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider;
import org.cmg.tapas.pa.LTSGraph;
import org.cmg.tapas.views.LabeledElement;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

/**
 * @author loreti
 *
 */
public class CCSPEquivalenceCheckerProvider implements
		TAPAsEquivalenceCheckerProvider {

	private CCSPModel ccspModel;

	private static final int KS = 0;
	private static final int KSOPT = 1;
	private static final int MPT = 2;
	private static final int PT = 3;
	private static final int RANKB = 4;
	
	private static final int DEFAULT_BISIMULATION = 1;
	
	private String[] bisimulationAlgorithms = {"KS" , "KSOPT" , "MPT" , "PT" , "RANKB" };

	private static final int DEFAULT_BRANCHING = 1;

	private static final int GV = 0;
	private static final int GVKS = 1;
	private static final int GVKS2  = 2;
	private static final int GVS = 3;
	
	private String[] branchingAlgorithms = {"GV" , "GVKS" , "GVKS2" , "GVS" };
	
	private static final int DEFAULT_TRACE = 0;
	
	private static final int STRONG_BISIMULATION = 0;
	private static final int WEAK_BISIMULATION = 1;
	private static final int BRANCHING = 2;
	private static final int TRACE = 3;

	/**
	 * 
	 */
	public CCSPEquivalenceCheckerProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#getProcesses()
	 */
	@Override
	public LabeledElement[] getProcesses() {
		LinkedList<LabeledElement> processes = new LinkedList<LabeledElement>();
		Set<String> processNames = ccspModel.getProcesses();
		for (String p : processNames) {
			for (String s : ccspModel.getStates(p) ) {
				processes.add(new LabeledElement(p+"["+s+"]", ccspModel.getState(p, s) ));
			}
		}
		Set<String> systemNames = ccspModel.getSystems();
		for (String sys : systemNames) {
			processes.add(new LabeledElement( sys , ccspModel.getSystem(sys) ));
		}
		return processes.toArray(new LabeledElement[processes.size()]);
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#isCatecorized()
	 */
	@Override
	public boolean isCatecorized() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#getCategories()
	 */
	@Override
	public String[] getCategories() {
		return new String[] { "Processes" , "Systems" };
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#getProcesses(java.lang.String)
	 */
	@Override
	public LabeledElement[] getProcesses(String category) {
		if ("Processes".equals(category)) {
			LinkedList<LabeledElement> processes = new LinkedList<LabeledElement>();
			Set<String> processNames = ccspModel.getProcesses();
			for (String p : processNames) {
				for (String s : ccspModel.getStates(p) ) {
					processes.add(new LabeledElement(p+"["+s+"]", ccspModel.getState(p, s) ));
				}
			}
			return processes.toArray(new LabeledElement[processes.size()]);
		}
		if ("Systems".equals(category)) {
			LinkedList<LabeledElement> processes = new LinkedList<LabeledElement>();
			Set<String> systemNames = ccspModel.getSystems();
			for (String sys : systemNames) {
				processes.add(new LabeledElement( sys , ccspModel.getSystem(sys) ));
			}
			return processes.toArray(new LabeledElement[processes.size()]);
		}
		return new LabeledElement[] {};
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#getEquivalences()
	 */
	@Override
	public String[] getEquivalences() {
		return new String[] { "Strong Bisimulation" , "Weak Bisimulation" , "Branching" , "Trace" };
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#getAlgorithms(int)
	 */
	@Override
	public String[] getAlgorithms(int equivalence) {
		if ((equivalence == 0)||(equivalence == 1)) {
			return bisimulationAlgorithms;
		}
		if (equivalence == 2) {
			return branchingAlgorithms;
		}
		return new String[] {};
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#check(int, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean check(int equivalence, Object p , Object q ) {
		switch (equivalence) {
		case STRONG_BISIMULATION:
			return check(equivalence, DEFAULT_BISIMULATION, p, q);
		case WEAK_BISIMULATION:
			return check(equivalence, DEFAULT_BISIMULATION, p, q);
		case BRANCHING:
			return check(equivalence, DEFAULT_BRANCHING, p, q);
		case TRACE:
			return check(equivalence, DEFAULT_TRACE , p, q);
		default:
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#check(int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean check(int equivalence, int algorithm, Object o1 ,
			Object o2 ) {
		if (!(o1 instanceof CCSPProcess)||!(o2 instanceof CCSPProcess) ) {
			return false;
		}
		CCSPProcess p = (CCSPProcess) o1;
		CCSPProcess q = (CCSPProcess) o2;
//		LTSGraph<CCSPProcess, CCSPAction> lts1 = CCSPUtil.getGraph(p);
//		LTSGraph<CCSPProcess, CCSPAction> lts2 = CCSPUtil.getGraph(q);
		
//		GraphComposition<CCSPProcess, CCSPAction> graph = new GraphComposition<CCSPProcess,CCSPAction>(lts1, lts2);
		LTSGraph<CCSPProcess,CCSPAction> graph = new LTSGraph<CCSPProcess,CCSPAction>();
		graph.addState(p);
		graph.addState(q);
		graph.expand();
		switch (equivalence) {
		case STRONG_BISIMULATION:
			return checkBisimulation(graph, algorithm, p, q);
		case WEAK_BISIMULATION:
			return checkBisimulation(new GraphClosure<CCSPProcess,CCSPAction>(graph,CCSPAction.TAU), algorithm, p, q);
		case BRANCHING:
			return checkBranching( graph , algorithm , p , q );
		case TRACE:
			return checkTrace( graph , p , q );
		default:
			return false;
		}
	}

	private boolean checkTrace(GraphData<CCSPProcess, CCSPAction> graph,
			CCSPProcess p, CCSPProcess q) {
		DecoratedTraceChecker<CCSPProcess, CCSPAction> checker = new DecoratedTraceChecker<>(graph, DecoratedTraceChecker.TRACE);
		return checker.checkEquivalence(p, q);
	}

	public boolean checkBranching(
			GraphData<CCSPProcess, CCSPAction> graph,
			int algorithm, CCSPProcess p,
			CCSPProcess q) {
		switch (algorithm) {
		case GV:
			GVChecker<CCSPProcess, CCSPAction> gvChecker = new GVChecker<CCSPProcess,CCSPAction>(graph);
			return gvChecker.checkEquivalence(p, q);
		case GVKS:
			GVKSChecker<CCSPProcess, CCSPAction> gvksChecker = new GVKSChecker<CCSPProcess,CCSPAction>(graph);
			return gvksChecker.checkEquivalence(p, q);
		case GVKS2:
			GVKS2Checker<CCSPProcess, CCSPAction> gvks2Checker = new GVKS2Checker<CCSPProcess,CCSPAction>(graph);
			return gvks2Checker.checkEquivalence(p, q);
		case GVS:
			GVSChecker<CCSPProcess, CCSPAction> gvsChecker = new GVSChecker<CCSPProcess,CCSPAction>(graph);
			return gvsChecker.checkEquivalence(p, q);
		default:
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#setModel(org.eclipse.emf.ecore.EObject, org.eclipse.core.resources.IFile)
	 */
	@Override
	public void setModel(EObject o, IFile file) {
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
	
	public boolean checkBisimulation( GraphData<CCSPProcess, CCSPAction> graph , int algorithm , CCSPProcess p , CCSPProcess q ) {
		BisimulationChecker<CCSPProcess, CCSPAction> checker;
		switch (algorithm) {
		case KS:
			checker = new BisimulationChecker<CCSPProcess, CCSPAction>(graph,BisimulationChecker.KS);
			return checker.checkEquivalence(p, q);
		case KSOPT:
			checker = new BisimulationChecker<CCSPProcess, CCSPAction>(graph,BisimulationChecker.KS2);
			return checker.checkEquivalence(p, q);
		case MPT:
			checker = new BisimulationChecker<CCSPProcess, CCSPAction>(graph,BisimulationChecker.MPT);
			return checker.checkEquivalence(p, q);
		case PT:
			checker = new BisimulationChecker<CCSPProcess, CCSPAction>(graph,BisimulationChecker.PT);
			return checker.checkEquivalence(p, q);
		case RANKB:
			checker = new BisimulationChecker<CCSPProcess, CCSPAction>(graph,BisimulationChecker.RANK_BASED);
			return checker.checkEquivalence(p, q);
		default:
			return false;
		}
	}

	public boolean checkWeakBisimulation( int algorithm , CCSPProcess p , CCSPProcess q ) {
		LTSGraph<CCSPProcess, CCSPAction> lts1 = CCSPUtil.getGraph(p);
		LTSGraph<CCSPProcess, CCSPAction> lts2 = CCSPUtil.getGraph(q);
		
		BisimulationChecker<CCSPProcess, CCSPAction> checker;
		GraphComposition<CCSPProcess, CCSPAction> graph = new GraphComposition<CCSPProcess,CCSPAction>(lts1, lts2);		
		switch (algorithm) {
		case KS:
			checker = new BisimulationChecker<CCSPProcess, CCSPAction>(graph,BisimulationChecker.KS);
			return checker.checkEquivalence(p, q);
		case KSOPT:
			checker = new BisimulationChecker<CCSPProcess, CCSPAction>(graph,BisimulationChecker.KS2);
			return checker.checkEquivalence(p, q);
		case MPT:
			checker = new BisimulationChecker<CCSPProcess, CCSPAction>(graph,BisimulationChecker.MPT);
			return checker.checkEquivalence(p, q);
		case PT:
			checker = new BisimulationChecker<CCSPProcess, CCSPAction>(graph,BisimulationChecker.PT);
			return checker.checkEquivalence(p, q);
		case RANKB:
			checker = new BisimulationChecker<CCSPProcess, CCSPAction>(graph,BisimulationChecker.RANK_BASED);
			return checker.checkEquivalence(p, q);
		default:
			return false;
		}
	}
}
