/**
 * 
 */
package org.cmg.tapas.slts.extensions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.extensions.TAPAsModelCheckerProvider;
import org.cmg.tapas.formulae.hml.HmlFormula;
import org.cmg.tapas.formulae.ltl.LtlFormula;
import org.cmg.tapas.formulae.ltl.LtlModelChecker;
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
public class SLTSModelCheckerProvider implements TAPAsModelCheckerProvider {

	private SltsModule sltsModel;

	/**
	 * 
	 */
	public SLTSModelCheckerProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsModelCheckerProvider#getProcesses()
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
	 * @see org.cmg.tapas.extensions.TAPAsModelCheckerProvider#isCatecorized()
	 */
	@Override
	public boolean isCatecorized() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsModelCheckerProvider#getCategories()
	 */
	@Override
	public String[] getCategories() {
		return new String[] {};
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsModelCheckerProvider#getProcesses(java.lang.String)
	 */
	@Override
	public LabeledElement[] getProcesses(String category) {
		return new LabeledElement[] {};
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsModelCheckerProvider#getFormulae()
	 */
	@Override
	public String[] getFormulae() {
		if (sltsModel == null) {
			return new String[] {};
		}
		Set<String> formulae = sltsModel.getHmlFormulae();
		return formulae.toArray(new String[formulae.size()]);
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsModelCheckerProvider#getFormulaeCategories()
	 */
	@Override
	public String[] getFormulaeCategories() {
		return new String[] {"HML", "LTL"};
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsModelCheckerProvider#getFormulae(java.lang.String)
	 */
	@Override
	public String[] getFormulae(String category) {
		if("HML".equals(category)){
			if(sltsModel.getHmlFormulae()!= null){
				String[] formulae = sltsModel.getHmlFormulae().toArray(new String[sltsModel.getHmlFormulae().size()]);
				return formulae;
			}
		}
		if("LTL".equals(category)){
			if(sltsModel.getLtlFormulae()!= null){
				String[] formulae = sltsModel.getLtlFormulae().toArray(new String[sltsModel.getLtlFormulae().size()]);
				return formulae;
			}
		}
		return new String[] {};
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsModelCheckerProvider#check(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean check(Object p, String formula) {
		if (!(p instanceof SltsState)) {
			return false;
		}
		if(sltsModel.getHmlFormulae().contains(formula)){
			HmlFormula<SltsState, SltsAction> f = sltsModel.getHmlFormula(formula);
			if (f == null) {
				return false;
			}
			return f.satisfies((SltsState) p);
		}
		if(sltsModel.getLtlFormulae().contains(formula)){
			LtlFormula<SltsState> f = sltsModel.getLtlFormula(formula);
			LTSGraph<SltsState, SltsAction> lts = new LTSGraph<>();
			lts.addState((SltsState) p);
			lts.expand();
			
			//Se esistono stati di deadlock aggiunge un self loop
			Graph<SltsState, SltsAction> graph = lts.getGraph();
			for(SltsState s : graph.getStates()){
				HashMap<SltsAction, Set<SltsState>> map = s.getNext();
				if(map.keySet().isEmpty()){
					graph.addEdge(s, new SltsAction(" "), s);
				}
			}
			
			Set<SltsState> initialStates = new HashSet<SltsState>();
			initialStates.add((SltsState)p);
			LtlModelChecker<SltsState> mc = new LtlModelChecker<SltsState>(graph, initialStates);
			return mc.check(f);
		}
		return false;
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
