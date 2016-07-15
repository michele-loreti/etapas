package org.cmg.tapas.core.omega;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.cmg.tapas.core.graph.Graph;

/**
 * @author Andrea Ercoli
 *
 * @param <S>
 * @param <A>
 */

//CANCELLARE DOPO AVER CONTROLLATO CHE NON CI SIANO DIPENDENZE ATTIVE
public class NBAutomaton<S,A> extends Graph<S,A> {

	private Set<S> initialStates = new HashSet<S>();
	private Set<S> acceptanceSet = new HashSet<S>();
	
	/**
	 * @param s
	 */
	public void addInitialState(S s){
		initialStates.add(s);
	}
	
	/**
	 * @param s
	 */
	public void addFinalState(S s){
		acceptanceSet.add(s);
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty(){
		boolean isEmpty = false;
		
		//controllare se break è la soluzione migliore
		for(S s: initialStates){
			isEmpty = reachableCycle(s);
			if(isEmpty == true)
				break;
		}
		
//		for(S s : initialStates){
//			isEmpty = cycleCheck(s);
//		}
		return isEmpty;
	}
	
	
	/**
	 * @param s
	 * @return
	 */
	private boolean reachableCycle(S s){
		boolean cycleFound = false;
		Set<S> setOfStates = new HashSet<S>();
		Stack<S> stackOfStates = new Stack<S>();
		stackOfStates.push(s);
		setOfStates.add(s);
		do{
			S s1 = stackOfStates.lastElement();
			Set<S> notVisited = getPostset(s1);
			notVisited.removeAll(setOfStates);
			if(!notVisited.isEmpty()){
				for(S s2 : notVisited){
					stackOfStates.push(s2);
					setOfStates.add(s2);
				}
			}else{
				stackOfStates.pop();
				if(acceptanceSet.contains(s1))
					cycleFound = cycleCheck(s1);
			}
		}while(!(stackOfStates.isEmpty() || cycleFound));
		return cycleFound;
	}
	
	/**
	 * @param s
	 * @return
	 */
	private boolean cycleCheck(S s){
		boolean cycle_found = false;
		Set<S> toVisit = new HashSet<S>();
		Stack<S> stackOfStates = new Stack<S>();

		stackOfStates.push(s);
		toVisit.add(s);
		do{
			S s1 = stackOfStates.lastElement();
			if(getPostset(s1).contains(s)){
				cycle_found = true;
				//stackOfStates.push(s);
			}else{
				Set<S> notVisited = getPostset(s1);
				notVisited.removeAll(toVisit);
				if(!notVisited.isEmpty()){
					for(S s2 : notVisited){
						stackOfStates.push(s2);
						toVisit.add(s2);
					}
				}else{
					stackOfStates.pop();
				}
			}
		}while(!(stackOfStates.isEmpty() || cycle_found));
		
		return cycle_found;
	}
	
}
