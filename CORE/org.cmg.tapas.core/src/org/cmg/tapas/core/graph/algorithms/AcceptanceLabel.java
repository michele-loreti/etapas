package org.cmg.tapas.core.graph.algorithms;

import org.cmg.tapas.core.graph.ActionInterface;

/**
 * La classe AcceptanceLabel � un tipo di etichetta
 * per gli stati di un grafo che tiene traccia
 * di varie caratteristiche dello stato:
 * accettazione, convergenza, deadlock.
 * Non sempre tutte le informazioni contenute
 * sono disponibili.
 * 
 * @author Guzman Tierno
 */
public class AcceptanceLabel<A extends ActionInterface> {	
	private AcceptanceSet<A> acceptance;
	private Boolean convergent;
	private Boolean finale;

	
	public AcceptanceLabel(
		AcceptanceSet<A> acceptance,
		Boolean convergent,		
		Boolean finale
	) {
		this.acceptance = acceptance;
		this.convergent = convergent;
		this.finale = finale;
	}
	
	/** 
	 * Rende l'insieme di accettazione o null se questo non � 
	 * disponibile.
	 **/
	public AcceptanceSet<A> getAcceptanceSet() {
		return acceptance;
	}
	
	/**
	 * Rende il flag di convergenza di questo stato o 
	 * null se questo non � disponibile.
	 **/
	public Boolean getConvergenceFlag() {
		return convergent;
	}

	/**
	 * Rende il flag di stato finale per questo stato o
	 * null se questo non � disponibile.
	 **/
	public Boolean getFinalFlag() {
		return finale;
	}
	
	private static <T> boolean checkEquality(T t1, T t2) {
		if( t1==null ) 
			return t2==null;
			
		return t1.equals(t2);
	}

	/** Dice se questa etichetta � equivalente a quella specificata. **/
	public boolean equivalent(AcceptanceLabel<A> label) {
		return
			label!=null &&
			checkEquality(convergent, label.convergent) &&
			checkEquality(finale, label.finale) &&
			checkEquality(acceptance, label.acceptance);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {		
		if( o==null || !(o instanceof AcceptanceLabel) )
			return false;
			
		// la prossima conversione causa un warning inevitabile,
		// � l'unica conversione usata nel pacchetto graph
		try{
			return equivalent((AcceptanceLabel<A>) o);
		} catch(ClassCastException unused) {
			System.err.println(
				"AcceptanceLabel<A>.equals(o): " +
				"Confronto tra etichette con tipo A delle azioni diverso. \n" +
				"Reso: 'false'. "
			);
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		if( acceptance!=null )		
			hash += acceptance.hashCode();
		if( convergent!=null )		
			hash += convergent.hashCode();
		if( finale!=null )		
			hash += finale.hashCode();
		return hash;
	}

	
	public String toString(){
		String res = "";
		
		if(acceptance != null)
			res += "acpt: "+acceptance.toString()+"-";
		else res += "acpt: N-";

		if(convergent == null)
			res += "conv N-";			
		else if(convergent)
			res += "conv t-";
		else res += "conv f-";
		
		if(finale == null)
			res += "fin N";			
		else if(finale)
			res += "fin t";
		else res += "fin f";
		
		return res;
	}
}



















