/**
 * 
 */
package org.cmg.tapas.core.mc.regular;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.mc.ModelCheckingResult;
import org.cmg.tapas.core.regular.Automaton;
import org.cmg.tapas.core.util.Pair;


/**
 * @author loreti
 *
 */
public class SafetyRegularModelChecker<S> {
	
	private Graph<S,?> transitionSystem;
	
	private Automaton<Predicate<S>> automaton;
	
	private Set<Pair<S,Integer>> visited;

	private Set<S> initialStates;
	
	private LinkedList<StackData> stack;
	
	public SafetyRegularModelChecker( ) {
	}
	
	public ModelCheckingResult<LinkedList<S>> check( ) {
		if ((this.transitionSystem==null)||(automaton==null)||(initialStates==null)) {
			throw new IllegalArgumentException();
		}
		this.visited = new HashSet<>();
		this.stack = new LinkedList<>();
		this.stack.push( new StackData( init() ) );
		boolean found = false;
		while (!this.stack.isEmpty()&&!found) {
			StackData sd = this.stack.peek();
			if (sd.toExpand.hasNext()) {
				sd.current = sd.toExpand.next();
				if (this.automaton.isAccepting( sd.current.getSecond() ) ) {
					found = true;
				} else {
					this.visited.add( sd.current );
					this.stack.push( 
						new StackData( 
							next(sd.current)
								.stream()
								.filter( p -> !visited.contains(p) )
								.collect(Collectors.toSet())
						) 
					);
				}
			} else {
				this.stack.pop();
			}
		}
		if (found) {
			return new ModelCheckingResult<>(false,getCounterExample());
		} else {
			return new ModelCheckingResult<>(true);
		}
	}
	
	private LinkedList<S> getCounterExample() {
		LinkedList<S> ce = new LinkedList<>();
		for (StackData s : this.stack) {
			ce.addFirst( s.current.getFirst() );
		}
		return ce;
	}

	private Set<Pair<S,Integer>> next( Pair<S,Integer> s ) {
		HashSet<Pair<S,Integer>> toReturn = new HashSet<>();
		Set<S> tsNext = transitionSystem.getPostset(s.getFirst());
		Map<Predicate<S>, Set<Integer>> automatonNext = automaton.getFunction( s.getSecond() );
		for (Entry<Predicate<S>,Set<Integer>> step : automatonNext.entrySet()) {
			toReturn.addAll( 
				tsNext.stream()
					.filter(step.getKey())
					.map(s2 -> 
						step.getValue()
							.stream()
							.map(i -> new Pair<>(s2,i))
							.collect(Collectors.toSet())
				).flatMap(Set::stream).collect(Collectors.toSet())
			)
			;
		}
		return toReturn;
	}
	
	private Set<Pair<S,Integer>> init( ) {
		HashSet<Pair<S,Integer>> toReturn = new HashSet<>();
		for( Integer s: automaton.getInitial() ) {
			Map<Predicate<S>,Set<Integer>> automatonNext = automaton.getFunction( s );
			for (Entry<Predicate<S>,Set<Integer>> step : automatonNext.entrySet()) {				
				toReturn.addAll( 
					initialStates
						.stream()
						.filter(step.getKey())
						.map( s2 -> combine( s2 , step.getValue() ))
						.flatMap(Set::stream)
						.collect(Collectors.toSet())
				);
			}
		}
		return toReturn;
	}
	
	private Set<Pair<S,Integer>> combine( S tsState , Set<Integer> automatonState ) {
		return automatonState.stream().map(i -> new Pair<S,Integer>(tsState,i)).collect(Collectors.toSet());
	}

	public Graph<S, ?> getTransitionSystem() {
		return transitionSystem;
	}

	public void setTransitionSystem(Graph<S, ?> transitionSystem) {
		this.transitionSystem = transitionSystem;
	}

	public Automaton<Predicate<S>> getAutomaton() {
		return automaton;
	}

	public void setAutomaton(Automaton<Predicate<S>> automaton) {
		this.automaton = automaton;
	}

	public Set<S> getInitialStates() {
		return initialStates;
	}

	public void setInitialStates(Set<S> initialStates) {
		this.initialStates = initialStates;
	}
	
	class StackData {
		
		Pair<S,Integer> current;
		
		Iterator<Pair<S,Integer>> toExpand;

		public StackData(Set<Pair<S, Integer>> init) {
			this.toExpand = init.iterator();
		}


	}
}
