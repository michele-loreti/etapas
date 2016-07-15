package org.cmg.tapas.core.graph.algorithms;


import java.util.Map;

import org.cmg.tapas.core.graph.LabelledStateI;

/** 
 * Gli oggetti di tipo Evaluator<S,V> valutano
 * oggetti di tipo S con oggetti di tipo V. <p>
 * La classe offre i metodi per costruire valutatori a partire 
 * da mappe (<code>fromMap(map)</code>) e valutatori per stati etichettati
 * (<code>fromLabelledState(state)</code>).
 * <p>
 * @param <S> tipo delle variabili
 * @param <V> tipo dei valori
 * 
 * @author Guzman Tierno
 **/
public abstract class Evaluator<S,V> {
	
	/**
	 * Valuta l'oggetto <tt>s</tt> associandogli un valore
	 * di tipo V.
	 **/	
	public abstract V eval(S s);
	
	/** 
	 * Crea un Evaluator che associa ad ogni oggetto 
	 * di tipo S un unico oggetto di tipo Object. 
	 * In altre parole tutti gli oggetti S vengono
	 * valutati allo stesso modo dal valutatore prodotto 
	 * da questa chiamata.
	 * Il parametro � usato solo per riconoscere il tipo S.
	 **/
	public static <S> Evaluator<S,Object> trivialEvaluator(S dummy) {
		return new Evaluator<S,Object>() {
			@Override 
			public Object eval(S s) {
				return this;
			}
		};
	}

	/** Crea un Evaluator a partire da una Map. **/	
	public static <S,V> Evaluator<S,V> fromMap(final Map<S,V> map) {
		return new Evaluator<S,V>() {
			@Override 
			public V eval(S s) {
				return map.get(s);
			}
		};
	}
	
	/** 
	 * Crea un Evaluator che associa ad ogni stato il valore 
	 * della sua etichetta. Il parametro � usato solo
	 * per riconoscere il tipo S.
	 **/
	public static <S extends LabelledStateI<L>, L> 
	Evaluator<S,L> fromLabelledState(S dummy) {
		return new Evaluator<S,L>() {
			@Override 
			public L eval(S s) {
				return s.getLabel();
			}
		};
	}
	
}














