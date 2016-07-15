/**
 * 
 */
package org.cmg.tapas.slts.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.formulae.hml.HmlFormula;
import org.cmg.tapas.formulae.ltl.LtlFormula;
import org.cmg.tapas.pa.LTSGraph;

/**
 * @author loreti
 *
 */
public abstract class SltsModule {

	private HashMap<String,SltsState> states;
	
	private HashMap<String,SltsAction> actions;
	
	private HashMap<String, Set<SltsState>> labels;
	
	private HashMap<String,HmlFormula<SltsState, SltsAction>> formulae;
	
	private HashMap<String,LtlFormula<SltsState>> ltlFormulae;
	
	public SltsModule( ) {
		this.states = new HashMap<String,SltsState>();
		this.actions = new HashMap<String,SltsAction>();
		this.labels = new HashMap<String,Set<SltsState>>();
		this.formulae = new HashMap<String,HmlFormula<SltsState, SltsAction>>();
		this.ltlFormulae = new HashMap<String,LtlFormula<SltsState>>();
		initActions();
		initStateSpace();
		initLabels();
		initFormulae();
	}
	
	protected abstract void initActions();
	
	protected abstract void initFormulae();

	protected abstract void initLabels();

	protected abstract void initStateSpace();

	public Set<String> getStates( ) {
		return this.states.keySet();
	}
		
	public SltsState getState( String name ) {
		return states.get(name);
	}
	
	protected void addState( SltsState state ) {
		this.states.put(state.getName(), state);
	}
	
	protected void addAction( SltsAction action ) {
		this.actions.put(action.getName(), action);
	}
	
	protected void addFormula( String name , HmlFormula<SltsState, SltsAction> formula ) {
		this.formulae.put(name, formula);
	}
	
	protected void addFormula( String name , LtlFormula<SltsState> formula){
		this.ltlFormulae.put(name, formula);
	}
	
	public Set<String> getHmlFormulae() {
		return formulae.keySet();
	}
	
	public Set<String> getLtlFormulae() {
		return ltlFormulae.keySet();
	}
	
	public HmlFormula<SltsState, SltsAction> getHmlFormula( String name ) {
		return formulae.get( name );
	}
	
	public LtlFormula<SltsState> getLtlFormula( String name ) {
		return ltlFormulae.get( name );
	}
	
	public Set<String> getActions() {
		return actions.keySet();
	}
	
	public SltsAction getAction( String name ) {
		return actions.get(name);
	}
	
	public Set<String> getLabels( ) {
		return labels.keySet();
	}
	
	protected void addLabel( String label , String ... states ) {
		HashSet<SltsState> set = new HashSet<SltsState>();
		for (String s : states) {
			SltsState state = getState(s);
			state.addLabel(label);
			if (state != null) {
				set.add(state);
			}
		}
		labels.put(label, set);
	}
	
	public Filter<SltsState> getLabel( String name ) {
		final Set<SltsState> set = labels.get(name);
		return new Filter<SltsState>(){

			@Override
			public boolean check(SltsState t) {
				return (set != null)&&(set.contains(t));
			}
			
		};
		
	}
	
	
	public LTSGraph<SltsState, SltsAction> generateLts( SltsState s ) {
		LTSGraph<SltsState, SltsAction> toReturn = new LTSGraph<SltsState, SltsAction>();
		toReturn.addState(s);
		toReturn.expand();
		return toReturn;
	}

	public LTSGraph<?, ?> generateLts(String process) {
		SltsState state = getState(process);
		if (state != null) {
			return generateLts(state);
		}
		return null;
	}

	
	
}
