package org.cmg.tapas.clts.extensions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.clts.runtime.CltsAction;
import org.cmg.tapas.clts.runtime.CltsModule;
import org.cmg.tapas.clts.runtime.CltsProcess;
import org.cmg.tapas.clts.runtime.CltsState;
import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.mc.ModelCheckingResult;
import org.cmg.tapas.core.mc.regular.SafetyRegularModelChecker;
import org.cmg.tapas.core.regular.Automaton;
import org.cmg.tapas.extensions.TAPAsModelCheckerProvider;
import org.cmg.tapas.formulae.hml.HmlFormula;
import org.cmg.tapas.formulae.ltl.LtlFormula;
import org.cmg.tapas.formulae.ltl.LtlModelChecker;
import org.cmg.tapas.pa.LTSGraph;
import org.cmg.tapas.views.LabeledElement;
import org.cmg.tapas.xtext.clts.composedLts.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

public class CLTSModelCheckerProvider implements TAPAsModelCheckerProvider {
	
	private CltsModule cltsModel;
	
	public CLTSModelCheckerProvider() {
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
		if (cltsModel == null) {
			return new LabeledElement[] {};
		}
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
	public String[] getFormulae() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getFormulaeCategories() {
		return new String[] {"HML", "LTL" , "REGULAR SAFETY"};
	}

	@Override
	public String[] getFormulae(String category) {
		if("HML".equals(category)){
			if(cltsModel.getHmlFormulae()!= null){
				String[] formulae = cltsModel.getHmlFormulae().toArray(new String[cltsModel.getHmlFormulae().size()]);
				return formulae;
			}
		}
		if("LTL".equals(category)){
			if(cltsModel.getLtlFormulae()!= null){
				String[] formulae = cltsModel.getLtlFormulae().toArray(new String[cltsModel.getLtlFormulae().size()]);
				return formulae;
			}
		}
		if("REGULAR SAFETY".equals(category)){
			if(cltsModel.getRegularSafetyProperties()!= null){
				String[] formulae = cltsModel.getRegularSafetyProperties().toArray(new String[cltsModel.getRegularSafetyProperties().size()]);
				return formulae;
			}
		}
		return new String[] {};
	}

	@Override
	public boolean check(Object p, String formula) {
		if( !(p instanceof CltsProcess) ){
			return false;
		}
		if(cltsModel.getHmlFormulae().contains(formula)){
			HmlFormula<CltsProcess, CltsAction> f = cltsModel.getHmlFormula(formula);
			if( f == null ){
				return false;
			}
			return f.satisfies((CltsProcess) p);
		}
		if(cltsModel.getLtlFormulae().contains(formula)){
			LtlFormula<CltsProcess> f = cltsModel.getLtlFormula(formula);
			
			LTSGraph<CltsProcess, CltsAction> lts = new LTSGraph<CltsProcess, CltsAction>();
			lts.addState( (CltsProcess) p );
			lts.expand();

			//Se esistono stati di deadlock aggiunge un self loop
			Graph<CltsProcess, CltsAction> graph = lts.getGraph();
			for(CltsProcess s : graph.getStates()){
				HashMap<CltsAction, Set<CltsProcess>> map = s.getNext();
				if(map.keySet().isEmpty()){
					graph.addEdge(s, new CltsAction(" "), s);
				}
			}
			
			Set<CltsProcess> initialStates = new HashSet<CltsProcess>();
			initialStates.add( (CltsProcess) p );
			LtlModelChecker<CltsProcess> mc = new LtlModelChecker<CltsProcess>(graph, initialStates);
			return mc.check(f);
		}
		if (cltsModel.getRegularSafetyProperties().contains(formula)) {
			SafetyRegularModelChecker<CltsProcess> mc = new SafetyRegularModelChecker<>();
			LTSGraph<CltsProcess, CltsAction> graph = new LTSGraph<CltsProcess, CltsAction>() ;
			graph.addState((CltsProcess) p);
			graph.expand();
			Automaton<Predicate<CltsProcess>> automaton = cltsModel.getRegularSafetyProperty(formula);			
			mc.setAutomaton(automaton);
			mc.setTransitionSystem(graph.getGraph()); 
			Set<CltsProcess> set = new HashSet<>();
			set.add((CltsProcess) p);
			mc.setInitialStates(set);
			ModelCheckingResult<LinkedList<CltsProcess>> result = mc.check();
			return result.isResult();
		}
		return false;
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

