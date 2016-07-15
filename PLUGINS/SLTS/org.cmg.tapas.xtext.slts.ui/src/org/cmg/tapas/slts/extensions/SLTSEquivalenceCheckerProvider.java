/**
 * 
 */
package org.cmg.tapas.slts.extensions;

import java.util.Set;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationChecker;
import org.cmg.tapas.core.graph.algorithms.branching.gv.GVChecker;
import org.cmg.tapas.core.graph.algorithms.branching.gvks.GVKSChecker;
import org.cmg.tapas.core.graph.algorithms.branching.gvks2.GVKS2Checker;
import org.cmg.tapas.core.graph.algorithms.branching.gvs.GVSChecker;
import org.cmg.tapas.core.graph.algorithms.traces.DecoratedTraceChecker;
import org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider;
import org.cmg.tapas.pa.LTSGraph;
import org.cmg.tapas.slts.runtime.SltsAction;
import org.cmg.tapas.slts.runtime.SltsModule;
import org.cmg.tapas.slts.runtime.SltsState;
import org.cmg.tapas.views.LabeledElement;
import org.cmg.tapas.xtext.slts.simpleLts.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

/**
 * @author loreti
 *
 */
public class SLTSEquivalenceCheckerProvider implements
		TAPAsEquivalenceCheckerProvider {

	private SltsModule sltsModel;

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
	public SLTSEquivalenceCheckerProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#getProcesses()
	 */
	@Override
	public LabeledElement[] getProcesses() {
		if (sltsModel == null) {
			return new LabeledElement[] {};
		}
		Set<String> states = sltsModel.getStates();
		LabeledElement[] toReturn = new LabeledElement[states.size()];
		int count = 0;
		for (String s : states) {
			toReturn[count] = new LabeledElement(s, sltsModel.getState(s));
			count++;
		}
		return toReturn;
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#isCatecorized()
	 */
	@Override
	public boolean isCatecorized() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#getCategories()
	 */
	@Override
	public String[] getCategories() {
		return new String[] {};
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#getProcesses(java.lang.String)
	 */
	@Override
	public LabeledElement[] getProcesses(String category) {
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
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#check(int, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean check(int equivalence, Object p, Object q) {
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
	 * @see org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider#check(int, int, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean check(int equivalence, int algorithm, Object o1, Object o2) {
		if (!(o1 instanceof SltsState)||!(o2 instanceof SltsState) ) {
			return false;
		}
		SltsState p = (SltsState) o1;
		SltsState q = (SltsState) o2;
		LTSGraph<SltsState, SltsAction> lts = new LTSGraph<SltsState,SltsAction>();
		lts.addState(p);
		lts.addState(q);
		lts.expand();
		
		switch (equivalence) {
		case STRONG_BISIMULATION:
			return checkBisimulation(lts, algorithm, p, q);
		case WEAK_BISIMULATION:
			return false;
		case BRANCHING:
			return checkBranching( lts , algorithm , p , q );
		case TRACE:
			return checkTrace( lts , p , q );
		default:
			return false;
		}
	}
	
	private boolean checkTrace(GraphData<SltsState, SltsAction> graph,
			SltsState p, SltsState q) {
		DecoratedTraceChecker<SltsState, SltsAction> checker = new DecoratedTraceChecker<>(graph, DecoratedTraceChecker.TRACE);
		return checker.checkEquivalence(p, q);
	}


	public boolean checkBisimulation( GraphData<SltsState, SltsAction> graph , int algorithm , SltsState p , SltsState q ) {
		BisimulationChecker<SltsState, SltsAction> checker;
		switch (algorithm) {
		case KS:
			checker = new BisimulationChecker<SltsState, SltsAction>(graph,BisimulationChecker.KS);
			return checker.checkEquivalence(p, q);
		case KSOPT:
			checker = new BisimulationChecker<SltsState, SltsAction>(graph,BisimulationChecker.KS2);
			return checker.checkEquivalence(p, q);
		case MPT:
			checker = new BisimulationChecker<SltsState, SltsAction>(graph,BisimulationChecker.MPT);
			return checker.checkEquivalence(p, q);
		case PT:
			checker = new BisimulationChecker<SltsState, SltsAction>(graph,BisimulationChecker.PT);
			return checker.checkEquivalence(p, q);
		case RANKB:
			checker = new BisimulationChecker<SltsState, SltsAction>(graph,BisimulationChecker.RANK_BASED);
			return checker.checkEquivalence(p, q);
		default:
			return false;
		}
	}	
	
	public boolean checkBranching(
			GraphData<SltsState, SltsAction> graph,
			int algorithm, SltsState p,
			SltsState q) {
		switch (algorithm) {
		case GV:
			GVChecker<SltsState, SltsAction> gvChecker = new GVChecker<SltsState,SltsAction>(graph);
			return gvChecker.checkEquivalence(p, q);
		case GVKS:
			GVKSChecker<SltsState, SltsAction> gvksChecker = new GVKSChecker<SltsState,SltsAction>(graph);
			return gvksChecker.checkEquivalence(p, q);
		case GVKS2:
			GVKS2Checker<SltsState, SltsAction> gvks2Checker = new GVKS2Checker<SltsState,SltsAction>(graph);
			return gvks2Checker.checkEquivalence(p, q);
		case GVS:
			GVSChecker<SltsState, SltsAction> gvsChecker = new GVSChecker<SltsState,SltsAction>(graph);
			return gvsChecker.checkEquivalence(p, q);
		default:
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsModelCheckerProvider#setModel(org.eclipse.emf.ecore.EObject, org.eclipse.core.resources.IFile)
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
}
