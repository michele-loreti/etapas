package org.cmg.tapas.core.graph.algorithms.bisimulation;


import java.util.LinkedList;
import java.util.List;

/** 
 * Splitter rappresenta un insieme di stati di un grafo atto a essere usato
 * come splitter negli algoritmi di Kannelakis-Smolka e Paige-Tarjan.
 * <p>
 * @param <S> tipo degli stati
 * 
 * @author Guzman Tierno
 **/
public class Splitter<S> {
	/** Numero di elementi (contenuti nelle foglie). **/
	private int size;		
	
	/** Primo figlio. **/
	private Splitter<S> child1;
	
	/** Secondo figlio. **/
	private Splitter<S> child2;
	
	/** Padre. **/
	private Splitter<S> father;

	/** Eventuale classe posseduta da questo splitter. **/
	private DecoratedClazz<S> clazz;
	
	/** Eventuale coda di appartenenza di questo splitter. **/
	private SplitterQueue<S> queue;

	/** Costruisce uno splitter vuoto. **/
	public Splitter() {		
        // empty splitter
	}

	/** Costruisce uno splitter con associata la classe specificata. **/
	public Splitter(DecoratedClazz<S> clazz) {
		setClazz(clazz);		
	}
	
	/**
	 * Setta il primo figlio.
	 **/
	private void setChild1(Splitter<S> newChild1) {
		if( child1!=null )
			child1.setFather(null);
		child1 = newChild1; 
		if( child1!=null )
			child1.setFather(this);
	}

	/**
	 * Setta il secondo figlio.
	 **/
	private void setChild2(Splitter<S> newChild2) {
		if( child2!=null )
			child2.setFather(null);
		child2 = newChild2; 
		if( child2!=null )
			child2.setFather(this);
	}

	/**
	 * Setta i figli (e, come conseguenza, la dimensione).
	 **/
	public void setChildren(
		Splitter<S> child1,
		Splitter<S> child2
	) {
		setChild1(child1);
		setChild2(child2);
		if( child1!=null ) // in this case, also child2 is non null
			size = child1.getSize() + child2.getSize();
	}

	/**
	 * Setta il padre.
	 **/
	public void setFather(Splitter<S> father) {
		this.father = father; 
	}

	/**
	 * Setta la classe posseduta da questo splitter 
	 * e, se la classe settata non � nulla, viene
	 * aggiornata la dimensione.
	 **/
	public void setClazz(DecoratedClazz<S> newClazz) {
		if( clazz!=null ) {
			clazz.setSplitter(null);
		}
		this.clazz = newClazz; 
		if( clazz!=null ) {
			clazz.setSplitter(this);
			size = clazz.size();		
			// size gets altered only by new nonempty clazzes
		}
	}

	/**
	 * Setta la coda di appartenenza per questo splitter.
	 **/
	public void setQueue(SplitterQueue<S> queue) {
		this.queue = queue; 
	}

	/**
	 * Rende il primo figlio.
	 **/
	public Splitter<S> getChild1() {
		return child1; 
	}

	/**
	 * Rende il secondo figlio.
	 **/
	public Splitter<S> getChild2() {
		return child2; 
	}

	/**
	 * Rende il figlio pi� piccolo.
	 * Assume che entrambe i figli siano non nulli.
	 **/
	public Splitter<S> getSmallestChild() {
		return child1.getSize() <= child2.getSize() ? child1 : child2;
	}

	/**
	 * Rende il figlio pi� grande.
	 * Assume che entrambe i figli siano non nulli.
	 **/
	public Splitter<S> getBiggestChild() {
		return child1.getSize() >  child2.getSize() ? child1 : child2;
	}

	/**
	 * Rende il padre.
	 **/
	public Splitter<S> getFather() {
		return father; 
	}

	/**
	 * Rende la classe posseduta da questo splitter.
	 **/
	public DecoratedClazz<S> getClazz() {
		return clazz; 
	}

	/**
	 * Rende la dimensione dello splitter, ossia il numero di
	 * elementi contenuti nelle foglie.
	 * (L'operazione ha costo costante).
	 **/
	public int getSize() {
		return size;
	}
	
	/**
	 * Rende la coda di appartenenza di questo splitter.
	 **/
	public SplitterQueue<S> getQueue() {
		return queue;
	}
	
	/**
	 * Rende una lista degli elementi contenuti nelle foglie dello
	 * splitter.
	 **/
	public List<S> listElements() {
		List<S> list = new LinkedList<S>();
		collectElements(list);
		return list;
	}
	
	/**
	 * Inserisce in una lista gli elementi contenuti nelle foglie dello
	 * splitter.
	 **/
	public void collectElements(List<S> list){
		if( clazz!=null ) {
			for( S state: clazz )
				list.add(state); 
		}
		if( child1!=null )
			child1.collectElements(list);
		if( child2!=null )
			child2.collectElements(list);
	}
	
	/** 
	 * Dice se lo splitter ha dei figli.
	 **/
	public boolean hasSons() {
		return child1!=null || child2!=null;
	}
	
	public String toString(int i){
		String res = "";
		List<S> elements;
		
		String tabs = "";
		for (int j = 0; j < i; j++) {
			tabs += "\t";
		}
		
		if(!hasSons()){

			elements = listElements();	
//			res += tabs+"Splitter semplice :\n";
			res += tabs+"\tSplitter elements ("+elements.size()+"): "+elements+"\n\n";
		}
		else{
			elements = getSmallestChild().listElements();	
			res += tabs+"Splitter composto:\n";
			res += tabs+"\tSplitter elements ("+elements.size()+"): "+elements+"\n";
			res += tabs+"\tChild1 ("
					+ getChild1().listElements().size()+"): \n"
					+ getChild1().toString(i+1);
			res += tabs+"\tChild2 ("
					+ getChild2().listElements().size()+"): \n"
					+ getChild2().toString(i+1);
		}
		
		return res;
	}
	
}

 
