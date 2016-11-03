/**
 * 
 */
package org.cmg.tapas.core.regular;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author loreti
 *
 */
public class Automaton<A> implements Cloneable {

	private Set<Integer> states;
	
	private Set<Integer> accepting;
	
	private Set<Integer> initial;
	
	private Map<Integer,Map<A,Set<Integer>>> function;
	
	public Automaton( ) {
		this.states = new HashSet<>();
		this.initial = new HashSet<>();
		this.accepting = new HashSet<>();
		this.function = new HashMap<>();
	}

	public Automaton(Automaton<A> automaton) {
		this();
		this.states.addAll(automaton.states);
		this.initial.addAll(automaton.initial);
		this.accepting.addAll(automaton.accepting);
		for (Entry<Integer, Map<A,Set<Integer>>> step : automaton.function.entrySet()) {
			this.function.put(step.getKey(), new HashMap<>(step.getValue()));
		}
	}

	public Automaton(Set<Integer> states, Map<Integer, Map<A, Set<Integer>>> function, Set<Integer> initial,
			Set<Integer> accepting) {
		this.states = states;
		this.function = function;
		this.initial = initial;
		this.accepting = accepting;
	}

	public int size() {
		return states.size();
	}
	
	/**
	 * Create an NFA that only recognizes the empty word. 
	 * 
	 * @param clazz Alphabet data type.
	 * @return an automaton recognising only the empty word.
	 */
	public static <A> Automaton<A> getEpsilon(Class<A> clazz) {
		Automaton<A> automaton = new Automaton<>();
		automaton.addState( true , true );
		return automaton;
	}
	
	/**
	 * Add a new state in the automaton.
	 * 
	 * @param isInitial states if the new state is an initial state or not.
	 * @param isAccepting states if the new state is a final state or not.
	 * @return the index of the new created state.
	 */
	private Integer addState(boolean isInitial, boolean isAccepting) {
		int newState = states.size();
		this.states.add(newState);
		this.function.put(newState,new HashMap<>());
		if (isInitial) {
			this.initial.add(newState);
		}
		if (isAccepting) {
			this.accepting.add(newState);
		}
		return newState;
	}

	/**
	 * Create an NFA that only recognizes the word composed by the single symbol 'c'. 
	 * 
	 * @param c the single symbol to recognize.
	 * @return an automaton recognizing only the word composed by the single symbol 'c'.
	 */
	public static <A> Automaton<A> getSingleton( A c ) {
		Automaton<A> automaton = new Automaton<>();
		Integer s0 = automaton.addInitialState();
		Integer s1 = automaton.addFinalState();
		automaton.addTransition( s0 , c , s1 );
		return automaton;
	}

	/**
	 * Add a transition to the automaton.
	 * 
	 * @param s0 source state
	 * @param c read symbol
	 * @param integers target states
	 */
	private void addTransition(Integer s0, A c, Integer ... integers ) {
		function.get(s0).put(c, Arrays.stream(integers).collect(Collectors.toSet()));
		
	}

	/**
	 * Add a final state to the automaton.
	 * 
	 * @return the index of created state.
	 */
	private Integer addFinalState() {
		return addState(false,true);
	}

	/**
	 * Add an initial state to the automaton. 
	 * 
	 * @return the index of created state.
	 */
	private Integer addInitialState() {
		return addState(true, false);
	}

	/**
	 * Checks if the automaton recognizes the work represtented via a linked list of symbols.
	 * 
	 * @param linkedList the word to recognize.
	 * @return true if the work is recognized, false otherwise.
	 */
	public boolean recognize(List<A> linkedList) {
		Set<Integer> current = this.initial;
		Iterator<A> cursor = linkedList.iterator();
		while ( cursor.hasNext() ) {
			A c = cursor.next();
			current = apply( current , c );
		}
		return isAccepting( current );
	}


	/**
	 * Computes the set of states reachable from current via the symbol c.
	 * 
	 * @param current current state
	 * @param c read symbol
	 * @return next states
	 */
	private Set<Integer> apply(Set<Integer> current, A c) {
		Set<Integer> next = new HashSet<>();
		for (Integer s : current) {
			next.addAll(
					function.getOrDefault(s,new HashMap<>()).
						getOrDefault(c, new HashSet<>()));
		}
		return next; 
	}


	/**
	 * Checks if the set current contains or not an accepting state.
	 * 
	 * @param current set of states.
	 * @return true if current contains an accepting state, false otherwise.
	 */
	private boolean isAccepting(Set<Integer> current) {
		HashSet<Integer> copy = new HashSet<>(current);
		copy.retainAll(current);
		for (Integer s : current) {
			if (this.accepting.contains(s)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Automaton<A> clone() throws CloneNotSupportedException {
		return new Automaton<A>( this );
	}

	/**
	 * Creates the NFA automaton recognizing the cocnatenation of L(a1) wiwth L(a2),
	 * where L(a) indicates the language recognized by a.
	 * 
	 * @param a1 NFA automaton
	 * @param a2 NFA automaton
	 * @return the NFA automaton recognizing the cocnatenation of L(a1) wiwth L(a2).
	 */
	public static <A> Automaton<A> concatenate(Automaton<A> a1, Automaton<A> a2) {
		Set<Integer> states = new HashSet<>( a1.states );
		Set<Integer> initial = new HashSet<>( a1.initial );
		Map<Integer,Map<A,Set<Integer>>> function = new HashMap<>();
		for (Entry<Integer, Map<A,Set<Integer>>> step : a1.function.entrySet()) {
			function.put(step.getKey(), new HashMap<>(step.getValue()));
		}
		states.addAll( shift( a2.states , a1.size() ));
		for (Entry<Integer, Map<A,Set<Integer>>> step : a2.function.entrySet()) {
			if (a2.initial.contains(step.getKey())) {
				for (Entry<A,Set<Integer>> stepCase: step.getValue().entrySet()) {
					Set<Integer> tmp = shift(stepCase.getValue(),a1.size());
					for (Integer s : a1.accepting) {
						function.put(s, merge(function.getOrDefault(s,new HashMap<>()), stepCase.getKey(), tmp));
					}
				}
			} else {
				HashMap<A,Set<Integer>> newStep = new HashMap<>(); 
				for (Entry<A,Set<Integer>> stepCase: step.getValue().entrySet()) {
					newStep.put(stepCase.getKey(), shift(stepCase.getValue(),a1.size()));
				}
				function.put(step.getKey()+a1.size(), newStep);
			}
		}
		Set<Integer> accepting = shift( a2.accepting , a1.size() );
		return new Automaton<A>(states,function,initial,accepting);
	}	
	
	public static Set<Integer> shift(Set<Integer> set, int v) {
		return set.stream().map(x -> x+v).collect(Collectors.toSet());
	}

	public static <A> Map<A, Set<Integer>> merge( Map<A,Set<Integer>> map , A c , Set<Integer> set) {
		Set<Integer> next = map.get(c);
		if (next == null) {
			map.put(c, set);
		} else {
			next.addAll(set);
		}
		return map;
	}

	@Override
	public String toString() {
		String toReturn = "";
		toReturn += "STATE "+states.size()+"\n";
		toReturn += "INITIAL: "+initial.toString()+"\n";
		toReturn += "ACCEPTING: "+accepting.toString()+"\n";
		toReturn += "TRANSITIONS\n";
		for (int i=0 ; i<size(); i++) {
			toReturn += (i+": "+function.get(i)+"\n");
		}
		return toReturn;
	}

	public static <A> Automaton<A> union(Automaton<A> a1, Automaton<A> a2) {
		Set<Integer> states = new HashSet<>( a1.states );
		Set<Integer> initial = new HashSet<>( a1.initial );
		Set<Integer> accepting = new HashSet<>( a1.accepting );
		Map<Integer,Map<A,Set<Integer>>> function = new HashMap<>();
		for (Entry<Integer, Map<A,Set<Integer>>> step : a1.function.entrySet()) {
			function.put(step.getKey(), new HashMap<>(step.getValue()));
		}
		states.addAll( shift( a2.states , a1.size() ));
		initial.addAll( shift(a2.initial, a1.size() ));
		accepting.addAll( shift( a2.accepting , a1.size() ));
		
		for (Entry<Integer, Map<A,Set<Integer>>> step : a2.function.entrySet()) {
			HashMap<A,Set<Integer>> newStep = new HashMap<>(); 
			for (Entry<A,Set<Integer>> stepCase: step.getValue().entrySet()) {
				newStep.put(stepCase.getKey(), shift(stepCase.getValue(),a1.size()));
			}
			function.put(step.getKey()+a1.size(), newStep);
		}
		return new Automaton<A>(states,function,initial,accepting);	
	}

	public static <A> Automaton<A> star(Automaton<A> a1) {
		Automaton<A> aStar = new Automaton<>(a1);
		if (!aStar.isAccepting(aStar.initial)) {
			aStar.addState(true, true);
		}
		for (Integer s1 : aStar.initial) {
			Map<A,Set<Integer>> initialStep = a1.function.get(s1);
			if (initialStep != null) {
				for (Entry<A,Set<Integer>> stepCase: initialStep.entrySet()) {
					for (Integer s2 : a1.accepting) {
						aStar.function.put(s2,  merge(aStar.function.get(s2), stepCase.getKey(), stepCase.getValue()));
					}
				}
			}
		}
		
		return aStar;	
	}

	public Map<A,Set<Integer>> getFunction( Integer s ) {
		return function.getOrDefault(s, new HashMap<>());
	}

	public Set<Integer> getInitial() {
		return initial;
	}

	public boolean isAccepting(Integer s) {
		return accepting.contains(s);
	}

	
}
