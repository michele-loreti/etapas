package org.cmg.tapas.formulae.ltl;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.core.omega.GBAutomaton;

/**
 * @author Ercoli
 *
 */
public class LtlFormulaeUtil {
	
	//ELIMINARE PERCHE' PRESENTE IN LTLMODELCHECKER
	public static <S> LinkedList<Set<LtlFormula<S>>> buildElementarySets( Set<LtlFormula<S>> closure ) {
		LinkedList<Set<LtlFormula<S>>> toReturn = new LinkedList<Set<LtlFormula<S>>>();
		toReturn.add(new HashSet<LtlFormula<S>>());
		Set<LtlFormula<S>> positive = new TreeSet<LtlFormula<S>>(getComparator());
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
		System.out.println(toReturn);
		return toReturn;
	}
	
	//ELIMINARE PERCHE' RIFATTO NELLA CLASSE LTLMODELCHECKER
	public static <S,A> GBAutomaton<Set<LtlFormula<S>>, A> getGBA(LinkedList<Set<LtlFormula<S>>> elementarySet, Set<LtlFormula<S>> closure ){
		GBAutomaton<Set<LtlFormula<S>>,A> toReturn = new GBAutomaton<Set<LtlFormula<S>>, A>();
		Set<LtlFormula<S>> fX = new HashSet<LtlFormula<S>>();
		Set<LtlFormula<S>> phiX = new HashSet<LtlFormula<S>>();
		Set<LtlFormula<S>> fU = new HashSet<LtlFormula<S>>();
		
		for(LtlFormula<S> c : closure){
			if(c instanceof LtlFormula.Next){
				fX.add(c);
				phiX.add(((LtlFormula.Next<S>) c).getArgument());
			}else if(c instanceof LtlFormula.Until){
				fU.add(c);
			}
		}
		
		// Ricavo gli stati finali
		Set<Set<LtlFormula<S>>> acceptingSet = new HashSet<Set<LtlFormula<S>>>();
		for(LtlFormula<S> f : fU){
			for(Set<LtlFormula<S>> b : elementarySet){
				LtlFormula.Until<S> until = (LtlFormula.Until<S>) f; 
				if( !b.contains(until) || b.contains(until.getRightArgument()))
					acceptingSet.add(b);
			}
		}
		toReturn.addFinalState(acceptingSet);
		
		for(Set<LtlFormula<S>> e : elementarySet){
			Set<LtlFormula<S>> current = e;
			LinkedList<Set<LtlFormula<S>>> toIterate = new LinkedList<Set<LtlFormula<S>>>(elementarySet);
			toIterate.remove(current);
			
			for (Set<LtlFormula<S>> s : toIterate) {
				// Controllo Next
				HashSet<LtlFormula<S>> first = new HashSet<LtlFormula<S>>(current);
				first.retainAll(fX);
				for(LtlFormula<S> f : first){
					first.add(((LtlFormula.Next<S>)f).getArgument());
					first.remove(f);
				}
				HashSet<LtlFormula<S>> second = new HashSet<LtlFormula<S>>(s);
				second.retainAll(phiX);
				first.retainAll(second);
				
				// Controllo Until
				if(first.isEmpty()){
					first = new HashSet<LtlFormula<S>>(current);
					first.retainAll(fU);
					
					for(LtlFormula<S> f : first){
						LtlFormula.Until<S> formula = (LtlFormula.Until<S>) f;
						
						if( current.contains(formula.getRightArgument()) ||
								( current.contains(formula.getLeftArgument()) && s.contains(formula.getRightArgument()) ) )
						{
							// I due controlli sono andati a buon fine, aggiungo stato
							toReturn.addState(current);
							toReturn.addState(s);
							//toReturn.addEdge(current, , s);
						}
								
					}
				}
			}
		}
		
		
		return toReturn;
	}
	
	/**
	 * Returns a comparator for ltl formulae.
	 * 
	 * @return the comparator
	 */
	public static LtlFormulaeUtil.LtlComparator getComparator(){
		return new LtlFormulaeUtil.LtlComparator();
	}
	

	/**
	 * Comparator class for ltl formulae.
	 * 
	 * @author Ercoli
	 *
	 */
	private static class LtlComparator implements Comparator<LtlFormula<?>>{

		@Override
		public int compare(LtlFormula<?> o1, LtlFormula<?> o2) {
			return o1.compareTo(o2);
		}

	}
}
