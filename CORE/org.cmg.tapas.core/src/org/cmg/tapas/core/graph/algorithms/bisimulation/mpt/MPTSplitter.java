package org.cmg.tapas.core.graph.algorithms.bisimulation.mpt;


import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


/** 
 * MPTSplitter rappresenta un insieme di stati di un grafo atto a essere usato
 * come splitter nell'algoritmo Paige-Tarjan multiplo.
 * <p>
 * @param <S> tipo degli stati
 * 
 * @author Guzman Tierno
 **/
class MPTSplitter<S> {    
    
	/** Numero di elementi (contenuti nelle foglie). **/
    private int size;		
	
	/** Numero massimo di figli. **/
    private int maxChildren;
	
	/** Figli. **/
	private Vector<MPTSplitter<S>> children;
	
	/** Padre. **/
	private MPTSplitter<S> father;

	/** Eventuale classe posseduta da questo splitter. **/
	private MPTClazz<S> clazz;
	
	/** Eventuale coda di appartenenza di questo splitter. **/
	private MPTQueue<S> queue;

	/** Costruisce uno splitter vuoto. **/
	public MPTSplitter(int maxChildren) {		
		this.maxChildren = maxChildren;
        children = new Vector<MPTSplitter<S>>(this.maxChildren); 
	}

	/** Costruisce uno splitter con associata la classe specificata. **/
	public MPTSplitter(int maxChildren, MPTClazz<S> clazz) {
        this(maxChildren);
		setClazz(clazz);		
	}

    /**
     * Setta la classe posseduta da questo splitter 
     * e, se la classe settata non � nulla, viene
     * aggiornata la dimensione.
     **/
    public void setClazz(MPTClazz<S> newClazz) {
        if( clazz!=null ) {
            clazz.setSplitter(null);
        }
        size = 0;
        clazz = newClazz; 
        if( clazz!=null ) {
            clazz.setSplitter(this);
            size = clazz.size();        
        }
    }


	/**
	 * Rende il numero di figli.
	 **/	
	public int getChildCount() {
		return children.size();	
	}

	/**
	 * Rende l'i-esimo figlio.
	 **/	
	public MPTSplitter<S> getChild(int i) {
		return children.get(i);
	}

    /**
     * Rimuove i figli.
     **/
    public void removeChildren() {
        for( MPTSplitter<S> child: children )
            child.setFather(null);
        children.clear();
        size = 0;        
    }

    /**
     * Aggiunge un figlio.
     **/
    public void addChild(MPTSplitter<S> newChild) {
        children.add(newChild);
        newChild.setFather(this);
        size += newChild.size;
    }
    
    /**
     * Rende il figlio pi� grande.
     **/
    public MPTSplitter<S> getLastChild() {
    	return children.lastElement();
    }
    
    
    /** 
     * Aggiunge il valore specificato alla dimensione dello splitter. 
     * Il metodo ha volutamente visibilit� pacchetto.
     **/
    void addToSize(int amount) {
    	size += amount;
    }

	/**
	 * Setta il padre.
	 **/
	public void setFather(MPTSplitter<S> father) {
		this.father = father; 
	}

	/**
	 * Setta la coda di appartenenza per questo splitter.
	 **/
	public void setQueue(MPTQueue<S> queue) {
		this.queue = queue; 
	}

    /**
     * Rende i figli.
     **/
    public List<MPTSplitter<S>> getChildren() {
        return children; 
    }

	/**
	 * Rende il padre.
	 **/
	public MPTSplitter<S> getFather() {
		return father; 
	}

	/**
	 * Rende la classe posseduta da questo splitter.
	 **/
	public MPTClazz<S> getClazz() {
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
	public MPTQueue<S> getQueue() {
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
        for( MPTSplitter<S> child: children )
            child.collectElements(list);
	}
	
	/** 
	 * Dice se lo splitter ha dei figli.
	 **/
	public boolean hasSons() {
		return !children.isEmpty();
	}
	
}

 
