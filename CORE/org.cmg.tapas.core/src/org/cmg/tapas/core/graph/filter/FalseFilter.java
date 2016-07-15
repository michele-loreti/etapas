package org.cmg.tapas.core.graph.filter;


/**
 * Filtro nullo su oggetti di tipo T: accetta tutti gli
 * oggetti di tipo T, compreso quello nullo.
 *
 * @param <T> Tipo degli oggetti da filtrare.
 * 
 * @author Guzman Tierno
 */
public class FalseFilter<T> extends AbstractFilter<T> {	

	/** 
	 * Restituisce <code>true</code>.
	 **/
	public final boolean check(T unused) {
		return false;
	}
	
//  Implementazione alternativa
//	public static final NoFilter NOFILTER = new NoFilter();
//	
//	public static final <T> Filter<T> getNoFilter() {
//		return (Filter<T>) NOFILTER;	
//	}	
	
	public String toString() {
		return "X";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof FalseFilter) 
			return true;
		else return false;
	}
	
}
