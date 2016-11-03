package org.cmg.tapas.clts.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.core.regular.Automaton;
import org.cmg.tapas.formulae.hml.HmlFormula;
import org.cmg.tapas.formulae.ltl.LtlFormula;

public abstract class CltsModule {
	protected TreeMap<String, TreeMap<String, CltsState>> ltsList;
	
	protected TreeMap<String, CltsProcess> ltsComposition;
	
	protected HashMap<String,HmlFormula<CltsProcess, CltsAction>> formulae;
	
	protected HashMap<String,LtlFormula<CltsProcess>> ltlFormulae;

	protected HashMap<String,Automaton<Predicate<CltsProcess>>> srProperties;
	
	private HashMap<String, Set<CltsState>> labels;
	
	private HashMap<String,CltsAction> actions;
	
	public CltsModule( ) {
		this.ltsList = new TreeMap<String, TreeMap<String, CltsState>>();
		this.ltsComposition = new TreeMap<String, CltsProcess>();
		this.formulae = new HashMap<String,HmlFormula<CltsProcess, CltsAction>>();
		this.ltlFormulae = new HashMap<String,LtlFormula<CltsProcess>>();
		this.srProperties = new HashMap<String,Automaton<Predicate<CltsProcess>>>();
		this.labels = new HashMap<String, Set<CltsState>>();
		this.actions = new HashMap<String, CltsAction>();
		initActions();
		initStateSpace();
		initFormulae();
	}
	
	protected abstract void initActions();
	
	protected abstract void initFormulae();

	protected abstract void initStateSpace();
	
	protected void addLtsToLtsList( String name ){
		ltsList.put(name , new TreeMap<String, CltsState>());
	}
	
	public Set<String> getLtsList(){
		return ltsList.keySet();
	}
	
	public Set<String> getStates( String lts ){
		return ltsList.get(lts).keySet();
	}
	
	public CltsState getState( String lts , String state ) {
		return ltsList.get(lts).get(state);
	}
	
	protected void addState( CltsState state ){
		ltsList.get(state.ltsName).put( state.name , state );
	}
	
	protected void addAction( CltsAction action ){
		this.actions.put(action.getName(), action);
	}
	
	public CltsAction getAction( String name ) {
		return actions.get(name);
	}
	
	public Set<String> getActions() {
		return actions.keySet();
	}
	
	protected void addSystem( String name , CltsProcess process){
		ltsComposition.put(name, process);
	}
	
	public CltsProcess getSystem( String name ){
		return ltsComposition.get(name);
	}
	
	public Set<String> getSystems(){
		return ltsComposition.keySet();
	}
	
	protected void addFormula( String name , HmlFormula<CltsProcess, CltsAction> formula ) {
		this.formulae.put(name, formula);
	}
	
	protected void addFormula( String name , LtlFormula<CltsProcess> formula){
		this.ltlFormulae.put(name, formula);
	}
	
	protected void addFormula( String name , Automaton<Predicate<CltsProcess>> formula){
		this.srProperties.put(name, formula);
	}
	
	public Set<String> getHmlFormulae() {
		return formulae.keySet();
	}
	
	public Set<String> getLtlFormulae() {
		return ltlFormulae.keySet();
	}
	
	public HmlFormula<CltsProcess, CltsAction> getHmlFormula( String name ) {
		return formulae.get( name );
	}
	
	public LtlFormula<CltsProcess> getLtlFormula( String name ) {
		return ltlFormulae.get( name );
	}
	
	public Set<String> getLabels( ) {
		return labels.keySet();
	}
	
	protected void addLabel( String label , String ltsName , String ... states ) {
		HashSet<CltsState> set = new HashSet<CltsState>();
		for (String s : states) {
			CltsState state = getState(ltsName , s);
			state.addLabel(label);
			if (state != null) {
				set.add(state);
			}
		}
		if(labels.get(label) == null){
			labels.put(label, set);
		}else{
			labels.get(label).addAll(set);
		}
	}
	
	public Filter<CltsProcess> getLabel( String name ) {
		final Set<CltsState> set = labels.get(name);
		return new Filter<CltsProcess>(){

			@Override
			public boolean check(CltsProcess t) {
				return t.sat(set);
			}
			
		};
		
	}

	public Set<String> getRegularSafetyProperties() {
		return srProperties.keySet();
	}
	
	public Automaton<Predicate<CltsProcess>> getRegularSafetyProperty( String name ) {
		return srProperties.get(name);
	}
	

}
