package org.cmg.tapas.core.graph;

/** 
 * GraphItemFactoryI � 
 * l'interfaccia per le fabbriche di nodi, di azioni e di grafi.
 * <p>
 * 
 * @author Guzman Tierno, Michele Loreti
 **/
public interface GraphItemFactoryI<
	S ,
	A extends ActionInterface
> {
	
	/** Rende un nuovo nodo (stato). **/
	S newState(String name);
	
	/** Rende una copia nodo <tt>s</tt> (stato). **/
	S newState(S s);

	/** Rende una nuova azione. **/
	A newAction(String name);
	
	/** Rende l'azione silente. **/
	A getTauAction();
	
	/** Rende un nuovo grafo. **/
	GraphInterface<S,A> newGraph();
}
