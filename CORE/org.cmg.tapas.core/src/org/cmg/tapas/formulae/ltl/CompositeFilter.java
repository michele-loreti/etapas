package org.cmg.tapas.formulae.ltl;

import java.util.LinkedList;
import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;

/**
 * Filtro composto dall'and logico dei filtri in esso contenuti
 * 
 * @author Andrea Ercoli
 *
 * @param <S> Tipo degli oggetti da filtrare
 */
public class CompositeFilter<S> implements Filter<S>{
	
	LinkedList<Filter<S>> filters = new LinkedList<Filter<S>>();
	
	public void add(Filter<S> f){
		filters.add(f);
	}
	
	public void addAll(Set<Filter<S>> f){
		filters.addAll(f);
	}
	
	@Override
	public boolean check(S t) {
		boolean toReturn = true;
		for(Filter<S> f : filters){
			toReturn = toReturn && f.check(t);
		}
		return toReturn;
	}

	@Override
	public String toString() {
		String toReturn = "{ ";
		for(Filter<S> f : filters){
			toReturn = toReturn + f.toString() + " , ";
		}
		toReturn = toReturn + " }";
		return toReturn;
	}
	
	
}
