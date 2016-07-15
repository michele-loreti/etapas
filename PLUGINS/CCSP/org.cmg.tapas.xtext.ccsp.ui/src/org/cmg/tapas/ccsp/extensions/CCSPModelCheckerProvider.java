/**
 * 
 */
package org.cmg.tapas.ccsp.extensions;

import java.util.HashSet;
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
import org.cmg.tapas.extensions.TAPAsModelCheckerProvider;
import org.cmg.tapas.pa.LTSGraph;
import org.cmg.tapas.views.LabeledElement;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

/**
 * @author loreti
 *
 */
public class CCSPModelCheckerProvider implements
		TAPAsModelCheckerProvider {

	private CCSPModel ccspModel;


	/**
	 * 
	 */
	public CCSPModelCheckerProvider() {
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

	@Override
	public String[] getFormulae() {
		Set<String> formulae = ccspModel.getHmlFormulae();
		formulae.addAll(ccspModel.getActlFormulae());
		return formulae.toArray(new String[formulae.size()]);
	}

	@Override
	public boolean check(Object p, String formula) {
		if (p instanceof CCSPProcess) {
			return ccspModel.check((CCSPProcess) p , formula);
		}
		return false;
	}

	@Override
	public String[] getFormulaeCategories() {
		return new String[] { "HML" , "ACTL" };
	}

	@Override
	public String[] getFormulae(String category) {
		Set<String> formulae = new HashSet<String>();
		if ("HML".equals(category)) {
			formulae = ccspModel.getHmlFormulae();
		} else {
			formulae = ccspModel.getActlFormulae();
		}
		return formulae.toArray(new String[ formulae.size()] );
	}
}
