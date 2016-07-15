package org.cmg.tapas.formulae.ltl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.omega.GBAutomaton;


public class LtlModelChecker<S> {
	
	private Graph<S, ?> lts;
	private Set<S> initialStates;
	LtlFormula<S> formula = null;
	Set<LtlFormula<S>> closure = null;
	GBAutomaton<Set<LtlFormula<S>>,S> gbaToReturn;
	Set<LtlFormula.Next<S>> fX = new HashSet<LtlFormula.Next<S>>();
	Set<LtlFormula<S>> phiX = new HashSet<LtlFormula<S>>();
	Set<LtlFormula.Until<S>> fU = new HashSet<LtlFormula.Until<S>>();
	
	public LtlModelChecker( Graph<S, ?> lts , Set<S> initialStates){
		this.lts = lts;
		this.initialStates = initialStates;
	}
	
	// TODO far passare direttamente la formula negata e levare tutti i this.formula?
	public boolean check( LtlFormula<S> formula){
		this.formula = (new LtlFormula.Not<S>(formula)).simplyNot();
		System.out.println("Formula Selected: "+ this.formula);
		closure = this.formula.acceptVisitor(new LtlFormulaClosure<S>());
		LinkedList<Set<LtlFormula<S>>> elementarySet = buildElementarySets();
		System.out.println();
		System.out.println("Formula Elementary Set: "+elementarySet);
		GBAutomaton<Set<LtlFormula<S>>, S> automaton = buildGBA(elementarySet);
		System.out.println("GBA Final states : "+automaton.getFinalStates().size());
		for( Set<Set<LtlFormula<S>>> s: automaton.getFinalStates()) {
			System.out.println(s.toString());
		}		
		System.out.println("=====================");
		System.out.println("GBA Initial states: "+automaton.getInitialStates());
		System.out.println("TS Initial states: "+ initialStates);
		boolean isEmpty = automaton.checkEmptyness(lts, initialStates);
		
		return isEmpty;
	}
	
	private LinkedList<Set<LtlFormula<S>>> buildElementarySets( ) {
		LinkedList<Set<LtlFormula<S>>> toReturn = new LinkedList<Set<LtlFormula<S>>>();
		toReturn.add(new HashSet<LtlFormula<S>>());
		Set<LtlFormula<S>> positive = new TreeSet<LtlFormula<S>>(LtlFormulaeUtil.getComparator());
		for(LtlFormula<S> s : closure){
			if(!(s instanceof LtlFormula.Not<?>)){
				positive.add(s);
			}
		}
		for (LtlFormula<S> f : positive) {
			LinkedList<Set<LtlFormula<S>>> foo = toReturn;
			toReturn = new LinkedList<Set<LtlFormula<S>>>();
			for (Set<LtlFormula<S>> set : foo) {
//				if (f.isElementaryIn( set , true ) ) {
//					HashSet<LtlFormula<S>> newSet = new HashSet<LtlFormula<S>>(set);
//					newSet.add(f);
//					toReturn.add(newSet);
//				} 
//				if (f.isElementaryIn( set , false ) ) {
//					HashSet<LtlFormula<S>> newSet = new HashSet<LtlFormula<S>>(set);
//					newSet.add(new LtlFormula.Not<S>(f));
//					toReturn.add(newSet);
//				}
				int x = f.computeElementaryExtension( set );
				if (x >= 0) {
					HashSet<LtlFormula<S>> newSet = new HashSet<LtlFormula<S>>(set);
					newSet.add(f);
					toReturn.add(newSet);				
				}
				if (x <= 0) {
					HashSet<LtlFormula<S>> newSet = new HashSet<LtlFormula<S>>(set);
					newSet.add(new LtlFormula.Not<S>(f));
					toReturn.add(newSet);
				}
			}
		}	
		return toReturn;
	}
	
	
	/**
	 * 
	 * 
	 * @param elementarySet
	 * @return
	 */
	private GBAutomaton<Set<LtlFormula<S>>, S> buildGBA(LinkedList<Set<LtlFormula<S>>> elementarySet ){
		gbaToReturn = new GBAutomaton<Set<LtlFormula<S>>, S>();
		
		// calcolo insiemi contenenti i next e gli until
		for(LtlFormula<S> c : closure){
			if(c instanceof LtlFormula.Next){
				fX.add((LtlFormula.Next<S>)c);
			} else if(c instanceof LtlFormula.Until){
				fU.add((LtlFormula.Until<S>)c);
			}
		}
		
		// Ricavo gli stati finali
		for(LtlFormula<S> f : fU){
			Set<Set<LtlFormula<S>>> acceptingSet = new HashSet<Set<LtlFormula<S>>>();
			for(Set<LtlFormula<S>> b : elementarySet){
				LtlFormula.Until<S> until = (LtlFormula.Until<S>) f; 
				if( !b.contains(until) || b.contains(until.getRightArgument()))
					acceptingSet.add(b);
			}
			gbaToReturn.addFinalState(acceptingSet);
		}
		if(gbaToReturn.getFinalStates().isEmpty())
			gbaToReturn.addFinalState(new HashSet<Set<LtlFormula<S>>>());
		
		// Ricavo gli stati iniziali
		for(Set<LtlFormula<S>> b : elementarySet){
			if(b.contains(formula))
				gbaToReturn.addInitialState(b);
		}
		
		// costruisco gli archi
		for(Set<LtlFormula<S>> e : elementarySet){
			Set<LtlFormula<S>> b = e;
			LinkedList<Set<LtlFormula<S>>> toIterate = new LinkedList<Set<LtlFormula<S>>>(elementarySet);
			
			for (Set<LtlFormula<S>> b1 : toIterate) {
				
				if(evolves(b , b1)) {
					System.out.println(b + "--->" + b1);
					addGBAEdge(b, b1);
				}
			}
		}

		return gbaToReturn;
	}
	
	/**
	 * verifica se esiste un arco tra l'insieme b e l'insieme b1
	 * @param b
	 * @param b1
	 * @return
	 */
	private boolean evolves(Set<LtlFormula<S>> b, Set<LtlFormula<S>> b1){
		for(LtlFormula.Next<S> x : fX){
			if( b.contains(x) ){
				if( !b1.contains(x.getArgument()) )
					return false;
			} else {
				if( b1.contains(x.getArgument()) )
					return false;
			}
		}
		
		for(LtlFormula.Until<S> u : fU){
			if( b.contains(u) ){
				
				if( !(b.contains(u.getRightArgument()) ||
						(b.contains(u.getLeftArgument()) && (b1.contains(u)) )) ){
					return false;
				}
				
			} else {
				
				if ((b.contains(u.getLeftArgument())) && (b1.contains(u))) {
					return false;
				}
				
			}
		}
		
		return true;
	}
	

	/**
	 * @param src
	 * @param dest
	 */
	private void addGBAEdge(Set<LtlFormula<S>> src, Set<LtlFormula<S>> dest){
		gbaToReturn.addState(src);
		gbaToReturn.addState(dest);

		CompositeFilter<S> filter = new CompositeFilter<S>();
		
		for(LtlFormula<S> l : src){
			if(l.getFilter() != null){	
				filter.add(l.getFilter());
			}
		}

		gbaToReturn.addEdge(src, filter , dest);

	}
}

