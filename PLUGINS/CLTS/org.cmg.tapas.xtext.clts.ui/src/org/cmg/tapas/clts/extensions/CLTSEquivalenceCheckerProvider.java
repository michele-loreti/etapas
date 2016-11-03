package org.cmg.tapas.clts.extensions;

import java.util.LinkedList;
import java.util.Set;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.clts.runtime.CltsAction;
import org.cmg.tapas.clts.runtime.CltsModule;
import org.cmg.tapas.clts.runtime.CltsProcess;
import org.cmg.tapas.core.graph.GraphClosure;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationChecker;
import org.cmg.tapas.core.graph.algorithms.branching.gv.GVChecker;
import org.cmg.tapas.core.graph.algorithms.branching.gvks.GVKSChecker;
import org.cmg.tapas.core.graph.algorithms.branching.gvks2.GVKS2Checker;
import org.cmg.tapas.core.graph.algorithms.branching.gvs.GVSChecker;
import org.cmg.tapas.core.graph.algorithms.traces.DecoratedTraceChecker;
import org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider;
import org.cmg.tapas.pa.LTSGraph;
import org.cmg.tapas.views.LabeledElement;
import org.cmg.tapas.xtext.clts.composedLts.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

public class CLTSEquivalenceCheckerProvider implements
		TAPAsEquivalenceCheckerProvider {
	
	private CltsModule cltsModel;
	
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
//	private static final int WEAK_BISIMULATION = 1;
	private static final int BRANCHING = 2;
	private static final int TRACE = 3;
	
	public CLTSEquivalenceCheckerProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public LabeledElement[] getProcesses() {
		if (cltsModel == null) {
			return new LabeledElement[] {};
		}
		LinkedList<LabeledElement> processes = new LinkedList<LabeledElement>();
		Set<String> processNames = cltsModel.getLtsList();
		for (String p : processNames){
			for (String s : cltsModel.getStates(p)){
				processes.add(new LabeledElement(p+"["+s+"]", cltsModel.getState(p, s)));
			}
		}
		Set<String> systemNames = cltsModel.getSystems();
		for (String sys : systemNames){
			processes.add(new LabeledElement( sys , cltsModel.getSystem(sys) ));
		}
		return processes.toArray(new LabeledElement[processes.size()]);
	}

	@Override
	public boolean isCatecorized() {
		return true;
	}

	@Override
	public String[] getCategories() {
		return new String[] {"Lts" , "Composed Lts"};
	}

	@Override
	public LabeledElement[] getProcesses(String category) {
		if("Lts".equals(category)){
			LinkedList<LabeledElement> processes = new LinkedList<LabeledElement>();
			Set<String> processNames = cltsModel.getLtsList();
			for (String p : processNames){
				for(String s : cltsModel.getStates(p)){
					processes.add( new LabeledElement(p+"["+s+"]", cltsModel.getState(p, s)) );
				}
			}
			return processes.toArray(new LabeledElement[processes.size()]);
		}
		if("Composed Lts".equals(category)){
			LinkedList<LabeledElement> processes = new LinkedList<LabeledElement>();
			Set<String> systemNames = cltsModel.getSystems();
			for (String sys : systemNames){
				processes.add(new LabeledElement( sys , cltsModel.getSystem(sys) ));
			}
			return processes.toArray(new LabeledElement[processes.size()]);
		}
		return new LabeledElement[] {};
	}

	@Override
	public String[] getEquivalences() {
		return new String[] { "Strong Bisimulation" , "Branching" , "Trace" };
	}

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

	@Override
	public boolean check(int equivalence, Object p, Object q) {
		switch (equivalence) {
		case STRONG_BISIMULATION:
			return check(equivalence, DEFAULT_BISIMULATION, p, q);
//		case WEAK_BISIMULATION:
//			return check(equivalence, DEFAULT_BISIMULATION, p, q);
		case BRANCHING:
			return check(equivalence, DEFAULT_BRANCHING, p, q);
		case TRACE:
			return check(equivalence, DEFAULT_TRACE , p, q);
		default:
			return false;
		}
	}

	@Override
	public boolean check(int equivalence, int algorithm, Object o1, Object o2) {
		if (!(o1 instanceof CltsProcess)||!(o2 instanceof CltsProcess)) {
			return false;
		}
		CltsProcess p = (CltsProcess) o1;
		CltsProcess q = (CltsProcess) o2;
		LTSGraph<CltsProcess, CltsAction> graph = new LTSGraph<CltsProcess, CltsAction>();
		graph.addState(p);
		graph.addState(q);
		graph.expand();
		switch (equivalence) {
		//TODO IMPLEMENTARE ALTRI
		case STRONG_BISIMULATION:
			return checkBisimulation(graph, algorithm, p, q);
//		case WEAK_BISIMULATION:
//			return checkBisimulation(new GraphClosure<CltsProcess,CltsAction>(graph,CCSPAction.TAU), algorithm, p, q);
		case BRANCHING:
			return checkBranching( graph , algorithm , p , q );
		case TRACE:
			return checkTrace( graph , p , q );
		default:
			return false;
		}
	}
	
	private boolean checkTrace(GraphData<CltsProcess, CltsAction> graph, CltsProcess p, CltsProcess q){
		DecoratedTraceChecker<CltsProcess, CltsAction> checker = new DecoratedTraceChecker<>(graph, DecoratedTraceChecker.TRACE);
		return checker.checkEquivalence(p, q);
	}
	
	public boolean checkBranching(GraphData<CltsProcess, CltsAction>graph, int algorithm, CltsProcess p, CltsProcess q){
		switch (algorithm) {
		case GV:
			GVChecker<CltsProcess, CltsAction> gvChecker = new GVChecker<CltsProcess,CltsAction>(graph);
			return gvChecker.checkEquivalence(p, q);
		case GVKS:
			GVKSChecker<CltsProcess, CltsAction> gvksChecker = new GVKSChecker<CltsProcess,CltsAction>(graph);
			return gvksChecker.checkEquivalence(p, q);
		case GVKS2:
			GVKS2Checker<CltsProcess, CltsAction> gvks2Checker = new GVKS2Checker<CltsProcess,CltsAction>(graph);
			return gvks2Checker.checkEquivalence(p, q);
		case GVS:
			GVSChecker<CltsProcess, CltsAction> gvsChecker = new GVSChecker<CltsProcess,CltsAction>(graph);
			return gvsChecker.checkEquivalence(p, q);
		default:
			return false;
		}
	}
	
	public boolean checkBisimulation( GraphData<CltsProcess, CltsAction> graph, int algorithm, CltsProcess p, CltsProcess q ){
		BisimulationChecker<CltsProcess, CltsAction> checker;
		switch (algorithm) {
		case KS:
			checker = new BisimulationChecker<CltsProcess, CltsAction>(graph,BisimulationChecker.KS);
			return checker.checkEquivalence(p, q);
		case KSOPT:
			checker = new BisimulationChecker<CltsProcess, CltsAction>(graph,BisimulationChecker.KS2);
			return checker.checkEquivalence(p, q);
		case MPT:
			checker = new BisimulationChecker<CltsProcess, CltsAction>(graph,BisimulationChecker.MPT);
			return checker.checkEquivalence(p, q);
		case PT:
			checker = new BisimulationChecker<CltsProcess, CltsAction>(graph,BisimulationChecker.PT);
			return checker.checkEquivalence(p, q);
		case RANKB:
			checker = new BisimulationChecker<CltsProcess, CltsAction>(graph,BisimulationChecker.RANK_BASED);
			return checker.checkEquivalence(p, q);
		default:
			return false;
		}
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
				e.printStackTrace();
				cltsModel = null;
			}
		}
	}

}
