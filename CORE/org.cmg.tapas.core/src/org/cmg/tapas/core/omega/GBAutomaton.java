/**
 * 
 */
package org.cmg.tapas.core.omega;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.graph.filter.Filter;

/**
 * @author loreti
 *
 */
public class GBAutomaton<T,S> extends Graph<T,Filter<S>>{

	private Set<T> initialStates = new HashSet<T>();

	private ArrayList<Set<T>> acceptingSets = new ArrayList<Set<T>>();
	
	/**
	 * @param s
	 */
	public void addInitialState(T s){
		initialStates.add(s);
	}
	
	/**
	 * @param s
	 */
	public void addFinalState(Set<T> s){
		acceptingSets.add(s);
	}
	
	public ArrayList<Set<T>> getFinalStates(){
		return acceptingSets;
	}
	
	public Set<T> getInitialStates(){
		return initialStates;
	}
	
	public <A> Graph<SyncProductState<T,S>,A> syncProduct( Graph<S, A> graph ) {
		Graph<SyncProductState<T,S>,A> toReturn = new Graph<SyncProductState<T,S>,A>();
		

		for (T gnbaState: super.states()) {
			Map<Filter<S>,? extends Set<T>> nextGNBA = getPostsetMapping(gnbaState);
			for( S graphState: graph.states()) {
				for( int i=0 ; i<acceptingSets.size() ; i++ ) {
					SyncProductState<T, S> src = new SyncProductState<T, S>(gnbaState, graphState, i);
					toReturn.addState(src);
					if (graph.getPostsetMapping(graphState) == null) {
						System.out.println("Porca trota...: "+graphState);
					}
					Map<A,? extends Set<S>> nextGraph = graph.getPostsetMapping(graphState);
					for (A action: nextGraph.keySet()) {
						for (S s : nextGraph.get(action)) {
							for (Filter<S> filter: nextGNBA.keySet()) {
								if (filter.check(s)) {
									if (acceptingSets.get(i).contains(gnbaState)) {
										for (T q : nextGNBA.get(filter)) {
											toReturn.addEdge(src, action, new SyncProductState<T, S>(q, s, (i+1)%acceptingSets.size()));										
										}
									} else {
										for (T q : nextGNBA.get(filter)) {
											toReturn.addEdge(src, action, new SyncProductState<T, S>(q, s, i));										
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return toReturn;
	}

	
	public Filter<SyncProductState<T,S>> getFinalStateFilter( ) {

		return new Filter<SyncProductState<T,S>>() {

			@Override
			public boolean check(SyncProductState<T, S> state) {
				return (state.index==0)&&(acceptingSets.get(0).contains(state.gnbaState));
			}
			
		};
		
	}

	public Set<SyncProductState<T,S>> getFinalStates( Set<S> graphFinalStates ) {
		Set<SyncProductState<T,S>> toReturn = new HashSet<SyncProductState<T,S>>();
		
			for (T gnbaFinalState : acceptingSets.get(0)) {
				for (S graphFinalState : graphFinalStates) {
					toReturn.add(new SyncProductState<T, S>(gnbaFinalState, graphFinalState, 0));
				}
			}
		return toReturn;
	}
	
	public Set<SyncProductState<T,S>> getInitialStates( Set<S> graphInitialStates ) {
		Set<SyncProductState<T,S>> toReturn = new HashSet<SyncProductState<T,S>>();

		for (T gnbaInitiaState : initialStates) {
			Map<Filter<S>,? extends Set<T>> gnbaNext = getPostsetMapping(gnbaInitiaState);
			for (Filter<S> filter : gnbaNext.keySet()) {
				for (S graphFinalState : graphInitialStates) {
					if (filter.check(graphFinalState)) {
						for (T q : gnbaNext.get(filter)) {
							toReturn.add(new SyncProductState<T, S>(q, graphFinalState, 0));
						}
					}
				}
			}
		}
		
		return toReturn;
	}
	
	
	public <A> boolean checkEmptyness( Graph<S,A> graph , Set<S> initialStates ) {
		Graph<SyncProductState<T,S>,A> productGraph = syncProduct( graph );
		System.out.println("***** TS X A ****");
		for(SyncProductState<T,S> s : productGraph.getStates()){
			System.out.println();
			System.out.println("State "+s+" :");
			for(SyncProductState<T,S> next : productGraph.getPostset(s)){
				System.out.println(" -> " +next);
			}
		
		}
		Set<SyncProductState<T, S>> productGraphInitialStates = getInitialStates( initialStates );
		System.out.println("TSxA initial states: "+productGraphInitialStates);
		Set<SyncProductState<T, S>> productGraphFinalStates = getFinalStates( graph.getStates() );
		System.out.println("TSxA final states: "+productGraphFinalStates);
		NBAEmptiness<A> gbae = new NBAEmptiness<A>(productGraph, productGraphInitialStates, productGraphFinalStates);
		
		return gbae.isEmpty();
	}
	
	
	
	public static class SyncProductState<T,S> {
		
		private T gnbaState;
		
		private S graphState;
		
		private int index;
		
		public SyncProductState( T gnbaState , S graphState , int index )  {
			this.gnbaState = gnbaState;
			this.graphState = graphState;
			this.index = index;
		}

		public T getGnbaState() {
			return gnbaState;
		}

		public S getGraphState() {
			return graphState;
		}

		public int getIndex() {
			return index;
		}

		@Override
		public int hashCode() {
			return (index+(graphState.hashCode()<<8)+(gnbaState.hashCode()<<20));
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SyncProductState<?, ?>) {
				SyncProductState<?,?> element = (SyncProductState<?, ?>) obj;
				return (this.index==element.index)&&gnbaState.equals(element.gnbaState) && graphState.equals(element.graphState);
			}
			return false;
		}

		@Override
		public String toString() {
			return "<"+graphState+":"+gnbaState+":"+index+">";
		}
		
	}

	/**
	 * Classe che calcola l'emptiness del grafo risultante 
	 * dal prodotto di un TS con un GBA.
	 * 
	 * @author Ercoli Andrea
	 *
	 * @param <A> azioni del ts
	 */
	public class NBAEmptiness<A>{
		private Graph<SyncProductState<T,S>,A> productGraph;
		private Set<SyncProductState<T, S>> productGraphInitialStates;
		private Set<SyncProductState<T, S>> productGraphFinalStates;
		
		NBAEmptiness (Graph<SyncProductState<T,S>,A> productGraph, 
				Set<SyncProductState<T, S>> productGraphInitialStates, Set<SyncProductState<T, S>> productGraphFinalStates){
			this.productGraph = productGraph;
			this.productGraphInitialStates = productGraphInitialStates;
			this.productGraphFinalStates = productGraphFinalStates;
			System.out.println("*** NBAEmptiness ***");
			System.out.println("TS x A Initial States: "+productGraphInitialStates);
			System.out.println("TS x A Final States: "+productGraphFinalStates);
			
		}
		
		public boolean isEmpty(){
			boolean isEmpty = false;
			
			System.out.println("*** Check Emptyness ***");
			
			for(SyncProductState<T, S> s: productGraphInitialStates){
				System.out.println("Stato iniziale : "+s);
				isEmpty = reachableCycle(s);
				if(isEmpty == true)
					return !isEmpty;
			}
			
//			for(S s : initialStates){
//				isEmpty = cycleCheck(s);
//			}
			return true;
			//return !productGraph.infinitelyOften( progructGraphFinalStates , progructGraphFinalStates)
			//return false;

		}
		
		/**
		 * @param s
		 * @param productGraph
		 * @return
		 */
		private boolean reachableCycle(SyncProductState<T, S> s){
			boolean cycleFound = false;
			Set<SyncProductState<T, S>> setOfStates = new HashSet<SyncProductState<T, S>>();
			Stack<SyncProductState<T, S>> stackOfStates = new Stack<SyncProductState<T, S>>();
			stackOfStates.push(s);
			setOfStates.add(s);
			do{
				SyncProductState<T, S> s1 = stackOfStates.lastElement();
				Set<SyncProductState<T, S>> notVisited = productGraph.getPostset(s1);
				notVisited.removeAll(setOfStates);
				if(!notVisited.isEmpty()){
					for(SyncProductState<T, S> s2 : notVisited){
						stackOfStates.push(s2);
						setOfStates.add(s2);
					}
				}else{
					stackOfStates.pop();
					if(productGraphFinalStates.contains(s1) || productGraphFinalStates.isEmpty())
						cycleFound = cycleCheck(s1);
				}
			}while(!(stackOfStates.isEmpty() || cycleFound));
			return cycleFound;
		}
		
		/**
		 * @param s
		 * @return
		 */
		private boolean cycleCheck(SyncProductState<T, S> s){
			boolean cycle_found = false;
			Set<SyncProductState<T, S>> toVisit = new HashSet<SyncProductState<T, S>>();
			Stack<SyncProductState<T, S>> stackOfStates = new Stack<SyncProductState<T, S>>();

			stackOfStates.push(s);
			toVisit.add(s);
			do{
				SyncProductState<T, S> s1 = stackOfStates.lastElement();
				if(productGraph.getPostset(s1).contains(s)){
					cycle_found = true;
					//stackOfStates.push(s);
				}else{
					Set<SyncProductState<T, S>> notVisited = productGraph.getPostset(s1);
					notVisited.removeAll(toVisit);
					if(!notVisited.isEmpty()){
						for(SyncProductState<T, S> s2 : notVisited){
							stackOfStates.push(s2);
							toVisit.add(s2);
						}
					}else{
						stackOfStates.pop();
					}
				}
			}while(!(stackOfStates.isEmpty() || cycle_found));
			System.out.println("Cycle found = "+cycle_found);
			return cycle_found;
		}

	}
}
