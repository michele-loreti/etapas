/**
 * 
 */
package org.cmg.tapas.ccsp.runtime;

import java.util.Set;
import java.util.TreeMap;

import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.formulae.actl.ActlFormula;
import org.cmg.tapas.formulae.hml.HmlFormula;

/**
 * @author loreti
 *
 */
public abstract class CCSPModel {

	protected TreeMap<String,CCSPProcess> systems;

	protected TreeMap<String,TreeMap<String,CCSPState>> processes;
	
	protected TreeMap<String,HmlFormula<CCSPProcess, CCSPAction>> hmlFormulae;

	protected TreeMap<String,HmlFormula<CCSPProcess, CCSPAction>> actlFormulae;

	public CCSPModel( ) {
		this.systems = new TreeMap<String, CCSPProcess>();
		this.processes = new TreeMap<String, TreeMap<String,CCSPState>>();
		this.hmlFormulae = new TreeMap<String, HmlFormula<CCSPProcess, CCSPAction>>();
		this.actlFormulae = new TreeMap<String, HmlFormula<CCSPProcess, CCSPAction>>();
		doInitFormulae();
		doInitBehaviour();
	}

	protected abstract void doInitFormulae();
		
	protected abstract void doInitBehaviour();

	/**
	 * @return the behaviours
	 */
	public Set<String> getSystems() {
		return systems.keySet();
	}

	/**
	 * @return the formulae
	 */
	public Set<String> getHmlFormulae() {
		return hmlFormulae.keySet();
	}

	/**
	 * @return the formulae
	 */
	public Set<String> getActlFormulae() {
		return actlFormulae.keySet();
	}

	public Set<String> getProcesses() {
		return processes.keySet();
	}
	
	public Set<String> getStates( String process ) {
		return processes.get(process).keySet();
	}
	
	protected void addSystem(String name , CCSPProcess process) {
		systems.put(name, process);
	}
	
	protected void addProcess(String name) {
		processes.put(name, new TreeMap<String, CCSPState>());
	}
	
	protected void addState( CCSPState state ) {
		processes.get(state.processName).put(state.stateName, state);
	}
	
	protected void addActlFormula(String name , HmlFormula<CCSPProcess, CCSPAction> f ) {
		actlFormulae.put(name, f);
	}

	protected void addHmlFormula(String name , HmlFormula<CCSPProcess, CCSPAction> f ) {
		hmlFormulae.put(name, f);
	}

	public CCSPProcess getSystem(String name) {
		return systems.get(name);
	}
	
	public CCSPState getState(String process , String state) {
		return processes.get(process).get(state);
	}
	
	public HmlFormula<CCSPProcess, CCSPAction> getFormula( String name ) {
		HmlFormula<CCSPProcess, CCSPAction> toReturn = null;
		toReturn = hmlFormulae.get(name);
		if (toReturn == null) {
			toReturn = actlFormulae.get(name);
		}
		return toReturn;
	}
	
	@SuppressWarnings("unchecked")
	public boolean check( CCSPProcess p , String formula ) {
		HmlFormula<CCSPProcess, CCSPAction> o = getFormula(formula);
		if (o != null) {
			return o.satisfies(p);
		}
		return false;
	}
	
	public abstract CCSPChannel getChannel( String name );
	
}
