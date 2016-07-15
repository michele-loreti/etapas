package org.cmg.tapas.core.graph;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.core.util.Entry;


/** 
 * Graph rappresenta un grafo etichettato sugli archi.
 * A differenza di <code>MultiGraph</code> non vengono considerate
 * le molteplicit&agrave; degli archi.<p>
 * Le etichette sugli archi sono dette anche azioni. <p>
 * La classe &egrave; parametrica rispetto al tipo <code>S<code> degli stati e
 * e rispetto al tipo <code>A</code> delle azioni. <p>
 * E' possibile inserire e rimuovere stati o archi, 
 * avere informazioni sugli stati e gli archi presenti e 
 * conoscere l'immagine (postset) o la retroimmagine (preset) 
 * di uno stato rispetto a una azione. <p>
 * E' anche possibile avere informazioni sulla molteplicit� di
 * un arco tra due nodi.
 * <p>
 * <p>
 * Invariante:											<p>
 *  - i nomi degli stati devono essere tutti diversi	<p>
 * <p>
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 * @author Michele Loreti
 * 
 **/
public class Graph<
	S,// extends StateInterface,
	A// extends ActionInterface
> implements GraphInterface<S,A>, Cloneable {
	
	/** Mappa stati-nomi **/
	//private HashMap< String, S > states = new HashMap< String, S >();
	private HashSet<S> states = new HashSet<S>();
	/** Mappa dei presets: stato->azione->preset **/
	private HashMap< S, HashMap<A, HashSet<S> >> inEdges =
		new HashMap< S, HashMap<A, HashSet<S> >>();	
	/** Mappa dei postsets: stato->azione->postset **/
	private HashMap< S, HashMap<A, HashSet<S> >> outEdges =
		new HashMap< S, HashMap<A, HashSet<S> >>();	
	/** Mappa del numero di volte con cui � usata ogni azione. **/
	private HashMap< A, Integer> actionMap = new HashMap<A, Integer>();

	/** Numero di archi (senza ripetizioni) **/
	private int edgeCount = 0;
	
	/** 
	 * Azione tau, null se non presente. 
	 * Si assume esista una sola azione tau di tipo A.
	 **/	
    // OPTION: getTauAction()
	private A tauAction;
	
	
	
	public Graph() {
		this.states = new HashSet<S>();
		this.inEdges = new HashMap<S, HashMap<A,HashSet<S>>>();
		this.outEdges = new HashMap<S, HashMap<A,HashSet<S>>>();
		this.actionMap = new HashMap<A, Integer>();
		this.edgeCount = 0;
	}
	
	@SuppressWarnings("unchecked")
	public Graph( Graph<S,A> graph ) {
		states = (HashSet<S>) graph.states.clone();
//		inEdges = (HashMap<S, HashMap<A, HashSet<S>>>) graph.inEdges.clone();
//		outEdges = (HashMap<S, HashMap<A, HashSet<S>>>) graph.outEdges.clone();
		
		//********** PARTE AGIUNTA IL 22/06/08 *************
		inEdges = new HashMap<S, HashMap<A,HashSet<S>>>();
		Set<S> keySet = graph.inEdges.keySet();
		for (S s : keySet) {
//			inEdges.put(s, (HashMap<A,HashSet<S>>)graph.inEdges.get(s).clone());
			HashMap<A,HashSet<S>> newMap = new HashMap<A, HashSet<S>>();
			HashMap<A,HashSet<S>> map = graph.inEdges.get(s);
			Set<A> HashSet = map.keySet();
			for (A a : HashSet) {
				newMap.put(a, (HashSet<S>) map.get(a).clone());
			}
			inEdges.put(s, newMap);
		}
		
		outEdges = new HashMap<S, HashMap<A,HashSet<S>>>();
		keySet = graph.outEdges.keySet();
		for (S s : keySet) {
//			outEdges.put(s, (HashMap<A,HashSet<S>>)graph.outEdges.get(s).clone());	
			HashMap<A,HashSet<S>> newMap = new HashMap<A, HashSet<S>>();
			HashMap<A,HashSet<S>> map = graph.outEdges.get(s);
			Set<A> HashSet = map.keySet();
			for (A a : HashSet) {
				newMap.put(a, (HashSet<S>) map.get(a).clone());
			}
			outEdges.put(s, newMap);
		}
		//********** FINE PARTE AGIUNTA IL 22/06/08 *********
		
		actionMap = (HashMap<A, Integer>) graph.actionMap.clone();
		edgeCount = graph.edgeCount;
	}
	
// _____________________________________________________________________________
// States: get
		
	/**
	 * Rende il numero di stati del grafo.
     * 
     * @return Numero di stati del grafo.
	 * @see org.cmg.tapas.core.graph.GraphData#getNumberOfStates()
	 */
	public int getNumberOfStates() {
		return states.size();
	}
	
	/**
	 * Rende un nuovo nome non ancora usato da nessuno stato
	 * del grafo.
     * 
     * @return Nome non usato da nessuno stato del grafo.
	 * @see org.cmg.tapas.core.graph.GraphInterface#getNewStateName()
	 */
//	public String getNewStateName() {
//		int count = states.size();
//  		String s = Integer.toString(count);
//  		while( states.containsKey(s) ) {
//  			++count;	
//  			s = Integer.toString(count);
//  		}  		
//  		return s;
//	}
	
	/**
	 * Rende lo stato il cui nome coincide 
	 * con la stringa passata come parametro 
	 * oppure rende <code>null</code> se un tale stato non 
     * � presente nel grafo.
     * 
     * @param name Nome dello stato da ricercare.
     * @return Lo stato col nome specificato.
	 * @see org.cmg.tapas.core.graph.GraphInterface#getState(java.lang.String)
	 */
//	public S getState(String name){
//		return states.get(name);
//	}

	/**
	 * Rende un iteratore per l'insieme degli stati del grafo.
	 * Il metodo � da preferire a <tt>getAllStates().iterator()</tt>
	 * in quanto evita di dover generare la collezione 
	 * degli stati e scorre invece direttamente la struttura interna
	 * che memorizza gli stati.
     * 
     * @return Un iteratore per gli stati del grafo.
	 * @see org.cmg.tapas.core.graph.GraphData#getStatesIterator()
	 */
	public Iterator<S> getStatesIterator(){
		return states.iterator();
//		return new Iterator<S>(){
//			Iterator<S> iterator = 
//                states.iterator();
//			S current = null;				
//			public boolean hasNext() {
//				return iterator.hasNext();				
//			}			
//			public S next() {
//				current = iterator.next().getValue();
//				return current;
//			}
//			public void remove() {
//				iterator.remove();
//				removeAllEdge(current);
//			}
//		};
	}	

	/**
	 * Rende una lista con gli stati del grafo che soddisfano
	 * il filtro passato come parametro. 
	 * Qualora possibile si consiglia l'uso di getStatesIterator()
	 * che risulta pi� efficiente.
     * 
     * @param stateFilter Filtro per gli stati.
     * @return Lista con gli stati del grafo che soddisfano il 
     * filtro.
	 * @see org.cmg.tapas.core.graph.GraphData#getStates(org.cmg.tapas.core.graph.filter.Filter)
	 */
	public Set<S> getStates(Filter<S> stateFilter){
		HashSet<S> list = new HashSet<S>(); 
		for( S state : states ) {
			if( stateFilter.check(state) ) 
				list.add(state);
		}
		return list;
	}	
	
// _____________________________________________________________________________
// States: is in

	/**
	 * Dice se lo stato passato come parametro
	 * appartiene al grafo.
	 **/	
	public boolean contains(Object state){
		return states.contains(state);
	}
	
// _____________________________________________________________________________
// States: add-remove

	/**
	 * Aggiunge un nuovo stato al grafo. 
	 * Il metodo fallisce se uno stato col nome
	 * dello stato passato come argomento � gi� presente.
	 **/
	public boolean addState(S state) {
		return internalAdd(state);
	}

	private boolean internalAdd(S state) {
		return states.add(state);
	}
	
	/**
	 * Rimuove lo stato passato come parametro.
	 * Rende true se l'operazione ha successo.
	 **/
	public boolean removeState(S state){
		if( !states.remove(state) )
			return false;

		removeAllEdge(state);
		return true;
	}

// _____________________________________________________________________________
// Edges: get
	
	/**
	 * Rende il numero di archi del grafo.
	 **/	
	public int getNumberOfEdges() {
		return edgeCount;
	}
	
	/** 
	 * Rende il numero di archi etichettati con l'azione
	 * specificata. 
	 **/
	public int getNumberOfEdges(A action) {
		Integer count = actionMap.get(action);
		return count==null ? 0 : count;
	}
	
	/**
	 * Rende la molteplicit� di un arco.
	 **/
	public int getNumberOfEdges(S src, A action, S dest) {
		Set<S> preset = getPreset( dest, action );
		
		if( preset!=null && preset.contains( src ) ) {
			return 1;
		} 
		return 0;
	}

	
// _____________________________________________________________________________
// Edges: is in

	/**
	 * Dice se nel grafo vi � un arco tra gli stati passati
	 * come parametri etichettato con l'azione passata come parametro.
	 **/
	public boolean contains(S from, A action, S to) {
		Set<S> preset = getPreset( to, action );
		
		return preset!=null && preset.contains( from );
	}

	/**
	 * Dice se nel grafo vi � un arco tra gli stati passati
	 * come parametri.
	 **/
	public boolean contains(S from, S to) {
		Map<A, ? extends Set<S>> postsetMap = outEdges.get(from);
		if( postsetMap == null ) {
			return false;			
		}
		
		Set<A> actions = postsetMap.keySet();
		Set<S> postset;
		for (A a : actions) {
			postset = postsetMap.get(a);
			if (postset != null) {
				if (postset.contains(to)) {
					return true;
				}
			}
		}
		return false;
	}


// _____________________________________________________________________________
// Edges: add-remove


	/**
	 * Aggiunge un nuovo arco al grafo.
	 * Se uno o entrambe gli estremi dell'arco non fanno
	 * parte del grafo, questi vengono aggiunti al grafo stesso.
	 * Il metodo fallisce se l'arco c'� gi� o se
	 * non � stato possibile aggiungerlo perch� uno dei suoi
	 * estremi non fa parte del grafo e un altro stato
	 * con lo stesso nome � gi� presente.
	 **/	
	public boolean addEdge(S src, A action, S dest){
		return internalAdd(src, action, dest);
	}
	
	private boolean internalAdd(S src, A action, S dest){
		if( contains(src, action, dest) ) {
			return true;
		}
		
		if( !contains(src) )
			if( !internalAdd(src) )
				return false;		

		if( !contains(dest) )
			if( !internalAdd(dest) )
				return false;		
		
		createPreset(dest, action).add(src);	// created, not empty
		createPostset(src, action).add(dest);	// created, not empty
		Integer count = actionMap.get(action);
		actionMap.put( action, count==null ? 1 : count+1 );
		edgeCount++;
		return true;
	}
	
		
	/**
	 * Rimuove l'arco solo dalla mappa della sorgente.
	 * Per mantenere l'informazione corretta il chiamante
	 * deve rimuovere l'arco dalla mappa della destinazione.
	 **/
	private boolean removeHalfOutgoingEdge(S src, A action, S dest) {
		Set<S> postset = getPostset(src, action);
		if( postset==null )
			return false;		
		
		if( postset.remove(dest) ) {
			if( postset.isEmpty() )
				outEdges.get(src).remove(action);
			return true;
		}

		return false;
	}
	
	/**
	 * Rimuove l'arco solo dalla mappa della destinazione.
	 * Per mantenere l'informazione corretta il chiamante
	 * deve rimuovere l'arco dalla mappa della sorgente.
	 **/
	private boolean removeHalfIncomingEdge(S src, A action, S dest) {
		Set<S> preset = getPreset(dest, action);
		if( preset==null )
			return false;		
		
		if( preset.remove(src) ) {
			if( preset.isEmpty() )
				inEdges.get(dest).remove(action);
			return true;
		}

		return false;
	}
	
	/**
	 * Rimuove un arco dal grafo. Rende true se l'operazione ha successo.
	 **/	
	public boolean removeEdge(S src, A action, S dest){
		boolean res = true;
		res &= removeHalfIncomingEdge(src, action, dest);
		res &= removeHalfOutgoingEdge(src, action, dest);
		if( res ) {
			edgeCount--;
			int actionCount = actionMap.get(action);
			if( actionCount==1 ) {
				actionMap.remove(action);				
			} else {
				actionMap.put( action, actionCount-1 );				
			}
		}
		
		return res;
	}
	
	
	/**
	 * Rimuove tutti gli archi entranti nello stato passato come parametro. 
	 * Rende il numero di archi rimossi.
	 **/
	public int removeIncomingEdge(S dest){
		Map<A, ? extends Set<S>> presetMap = inEdges.get(dest);
		if( presetMap==null ) 
			return 0;			
	
		int actionCount;		
		int count = 0;
		Set<S> preset;		
		for( A action: presetMap.keySet() ) {
			preset = presetMap.get(action);
			count += preset.size();
			for( S src: preset ) {			// remove from src map
				removeHalfOutgoingEdge(src, action, dest);
				actionCount = actionMap.get(action);
				if( actionCount==1 ) {
					actionMap.remove(action);
				} else {
					actionMap.put( action, actionCount-1 );
				}
			}	
		}
		inEdges.remove(dest);				// removeAll from dest map
		
		edgeCount -= count;
		return count;
	}
	
	/**
	 * Rimuove tutti gli archi uscenti dallo stato passato come parametro. 
	 * Rende il numero di archi rimossi.
	 **/
	public int removeOutgoingEdge(S src){
		Map<A, ? extends Set<S>> postsetMap = outEdges.get(src);
		if( postsetMap==null ) 
			return 0;
			
		int actionCount;		
		int count = 0;
		Set<S> postset;		
		for( A action: postsetMap.keySet() ) {
			postset = postsetMap.get(action);
			count += postset.size();
			for( S dest: postset ) {	// remove from dest map				
				removeHalfIncomingEdge(src, action, dest); //TODO: Eseguire i test sulle operazioni del grafo
				actionCount = actionMap.get(action);
				if( actionCount==1 ) {
					actionMap.remove(action);
				} else {
					actionMap.put( action, actionCount-1 );
				}
			}	
		}
		outEdges.remove(src);			// removeAll from src map
		
		edgeCount -= count;
		return count;
	}
	
	/**
	 * Rimuove tutti gli archi uscenti ed entranti nello
	 * stato passato come parametro.
	 * Rende il numero di archi rimossi.
	 **/
	public int removeAllEdge(S state){
		return removeIncomingEdge(state) + removeOutgoingEdge(state);
	}

	
// _____________________________________________________________________________
// Actions

	/**
     * Rende un insieme con le azioni del grafo. 
     **/
    public Set<A> getActions() {
    	return actionMap.keySet();
    }

   
// _____________________________________________________________________________
// Pre/post
	
	/**
	 * Rende la mappa dei preset di uno stato 'state': cio�
	 * la mappa che associa ad ogni azione 'a'
	 * la retroimmagine di 'state' sotto l'azione 'a'.
	 * Nel caso che 'state' non abbia nessun arco entrante
	 * il metodo rende null.
	 **/
	public Map<A, ? extends Set<S>> getPresetMapping(S state) {
		return inEdges.get( state );
	}

	/**
	 * Rende la mappa dei postset di uno stato 'state': cio�
	 * la mappa che associa ad ogni azione 'a'
	 * l'immagine di 'state' sotto l'azione 'a'.
	 * Nel caso che 'state' non abbia nessun arco uscente
	 * il metodo rende null.
	 **/
	public Map<A, ? extends Set<S>> getPostsetMapping(Object state) {
		return outEdges.get( state );
	}
	
	/** Iteratore per le transizioni di uno stato. **/
	private class ImageIterator implements Iterator<Map.Entry<A,S>> {
		private Iterator<Map.Entry<A, HashSet<S>>> imageIter = null; 
		private Iterator<S> postIter = null; 
		private S state = null;
		private A nextAction = null;
		private S nextState = null;
		private EdgeFilter<S,A> filter;

		/** 
		 * Costruisce un ImageIterator per le transizioni
		 * dello stato specifiacto. In uscita se 'post' vale true,
		 * in entrata altrimenti
		 **/
		ImageIterator(S state, boolean post, EdgeFilter<S,A> filter) {	
			this.filter = filter;
			this.state = state;
			Map<A, HashSet<S>> map = 
				post ? outEdges.get( state ) : inEdges.get( state );
				
			if( map!=null )
				imageIter = map.entrySet().iterator();
				
			if( imageIter!=null ) {
				nextAction();
				internalNext();
			}
		}				
		
		private void nextAction() {
			Map.Entry<A,? extends Set<S>> t = imageIter.next();
			postIter = t.getValue().iterator();
			nextAction = t.getKey();
		}
		
		private void internalNext() {			
			if( imageIter==null )
				return;
						
			while( true ) {
				while( postIter.hasNext() ) {
					nextState = postIter.next();
					if( filter.check(state, nextAction, nextState) )
						return;
				}
				nextState = null;
				nextAction = null;
				
				if( !imageIter.hasNext() )
					return;
				
				nextAction();					
			}
		}
		
		public boolean hasNext() {				
			return nextState!=null;				
		}			

		public Map.Entry<A,S> next() {
			if( !hasNext() )
				throw new NoSuchElementException("ImageIterator.next()");
				
			Entry<A,S> entry =  new Entry<A,S>(nextAction,nextState);
			internalNext();					
			return entry;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	

	/**
     * Rende un iteratore per le transizioni dello stato specificato.
     * Le transizioni considerate sono quelle uscenti se 'post'
     * � true, quelle entranti altrimenti.
     **/	
    public Iterator<Map.Entry<A,S>> getImageIterator(
    	S state, boolean post, EdgeFilter<S,A> filter
    ) {
		return new ImageIterator(state, post, filter);
    }

	/**
	 * Rende il preset di uno stato sotto una azione.
	 * Se lo stato non ha archi entranti con tali azione
	 * pu� rendere null.
	 **/
	public Set<S> getPreset(S state, A action) {
		Map<A, ? extends Set<S>> presetMap = inEdges.get(state);		
		if( presetMap == null )
			return null;
			
		return presetMap.get(action);
	}
	
	public Set<S> getPreset(S state, Filter<A> filter) {
		HashSet<S> toReturn = new HashSet<S>();
		Map<A, ? extends Set<S>> presetMap = inEdges.get(state);
		if( presetMap == null )
			return toReturn;
		Set<A> actions = presetMap.keySet();		
		Set<S> preset;
		for (A a : actions) {
			if (filter.check(a)) {
				preset = presetMap.get(a);
				if (preset != null) {
					toReturn.addAll(preset);
				}
			}
		}
				
		return toReturn;
	}


	/**
	 * Rende il postset di uno stato sotto una azione.
	 * Se lo stato non ha archi uscenti con tali azione
	 * pu� rendere null.
	 **/
	public Set<S> getPostset(S state, A action) {
		Map<A, ? extends Set<S>> postsetMap = outEdges.get(state);
		if( postsetMap == null )
			return null;
			
		return postsetMap.get( action );
	}


	/** Crea, se necessario, e rende la mappa dei preset di uno stato **/
	private HashMap<A, HashSet<S>> createPresetMap(S state) {
		HashMap<A, HashSet<S>> presetMap = inEdges.get(state);
		if( presetMap==null ) {
			presetMap = new HashMap<A, HashSet<S>>();
			inEdges.put( state, presetMap );
		}
		return presetMap;
	}
	

	/** Crea, se necessario, e rende il preset di uno stato per una azione **/
	private HashSet<S> createPreset(S state, A action) {
		HashMap<A, HashSet<S>> presetMap = createPresetMap(state);
		HashSet<S> preset = presetMap.get(action);
		if( preset==null ) {
			preset = new HashSet<S>();
			presetMap.put( action, preset );
		}
		return preset;
	}

	/** Crea, se necessario, e rende la mappa dei postset di uno stato **/
	private HashMap<A, HashSet<S>> createPostsetMap(S state) {
		HashMap<A, HashSet<S>> postsetMap = outEdges.get(state);
		if( postsetMap==null ) {
			postsetMap = new HashMap<A, HashSet<S>>();
			outEdges.put( state, postsetMap );
		}
		return postsetMap;
	}

	/** Crea, se necessario, e rende il postset di uno stato per una azione **/
	private HashSet<S> createPostset(S state, A action) {
		HashMap<A, HashSet<S>> postsetMap = createPostsetMap(state);
		HashSet<S> postset = postsetMap.get(action);
		if( postset==null ) {
			postset = new HashSet<S>();
			postsetMap.put( action, postset );
		}
		return postset;
	}

	/** Rende true se lo stato specificato non ha archi uscenti. **/
	public boolean isFinal(S state) {
		return outEdges.get(state) == null;
	}


// _____________________________________________________________________________
// print, toString

	/**
	 * Stampa gli stati del grafo.
	 **/	
	public void printStates() {
		System.out.println( "Stati: [ \n" );		
		for( S s : states ) {
			System.out.println( " " + s.toString() );					
		}
		System.out.println("]");
	}
	
	
	/**
	 * D� una descrizione del grafo.
	 **/
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append( "Stati: " + getNumberOfStates() + "\n" );
		s.append( "Archi: " + getNumberOfEdges() + "\n"  );
		Iterator<S> iterator1 = getStatesIterator();		
		Iterator<S> iterator2;		
		while( iterator1.hasNext() ) {
			S state1 = iterator1.next();
			iterator2 = getStatesIterator();		
			while( iterator2.hasNext() ) {
				S state2 = iterator2.next();
				for( A action: getActions() ) {
					if( contains(state1, action, state2) )
						s.append( state1 + " " + action + " " + state2 + "\n" );
				}
			}
		}
		
		return s.toString();
	}

	public Set<S> getPostset(Object state) {
		HashSet<S> toReturn = new HashSet<S>();
		Map<A, ? extends Set<S>> postsetMap = outEdges.get(state);
		if( postsetMap == null )
			return toReturn;
		
		Set<A> actions = postsetMap.keySet();
		Set<S> postset;
		for (A a : actions) {
			postset = postsetMap.get(a);
			if (postset != null) {
				//toReturn.addAll(postset);
				for (S s : postset) {
					toReturn.add(s);
				}
			}
		}
		
		return toReturn;
	}

	public Set<S> getPostset(S state, Filter<A> filter) {
		HashSet<S> toReturn = new HashSet<S>();
		Map<A, ? extends Set<S>> postsetMap = outEdges.get(state);
		if( postsetMap == null )
			return toReturn;
		Set<A> actions = postsetMap.keySet();		
		Set<S> postset;
		for (A a : actions) {
			if (filter.check(a)) {
				postset = postsetMap.get(a);
				if (postset != null) {
					toReturn.addAll(postset);
				}
			}
		}
				
		return toReturn;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Graph<S,A> clone() {
		return new Graph<S, A>(this);
	}

	public int getNumberOfEdges(boolean multiplicity) {
		return getNumberOfEdges();
	}
	
	public int[][] getAdiacenceMatrix(Map<S,Integer> map) {
		Set<S> states = map.keySet();
		int size = states.size();
		int[][] toReturn = new int[size][size];
		for( int i=0 ; i<size ; i++ ) {
			toReturn[i][i] = 0;
			for( int j=0 ; j<i ; j++) {
				toReturn[i][j] = Integer.MAX_VALUE;
				toReturn[j][i] = Integer.MAX_VALUE;
			}
		}
		int i=0;
		int j=0;
		for (S s : states) {
			i = getStateIndex(map, s);
				Map<A, ? extends Set<S>> next = getPostsetMapping(s);
				if (next != null) {
					for (A a : next.keySet()) {
						Set<S> step = next.get(a);
						if (step != null) {
							for (S s2 : step) {
								j = getStateIndex(map, s2);
								toReturn[i][j] = 1;
								toReturn[j][i] = 1;
							}
						}
					}
				}	
		}
		return toReturn;
	}
	
	private int getStateIndex( Map<S,Integer> map , S s ) {
		Integer i = map.get(s);
		if (i != null) {
			return i;
		} else {
			throw new IllegalArgumentException("Unknown state index!");
		}

	}
	
	@SuppressWarnings("unchecked")
	public Set<S> getStates() {
		return (Set<S>) states.clone();
	}

	public Iterable<S> states() {
		return states;
	}
} 
