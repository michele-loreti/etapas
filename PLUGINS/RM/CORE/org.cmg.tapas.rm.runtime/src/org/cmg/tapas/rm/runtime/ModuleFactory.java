/**
 * 
 */
package org.cmg.tapas.rm.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author loreti
 *
 */
public class ModuleFactory {
	
	public final static String TAU_STRING = "TAU";
	
	private ModuleAction[] actions;
	
	private HashMap<String,ModuleAction> actionTable;

	private StateEnumerator enumerator;
	
	public ModuleFactory( StateEnumerator enumerator , ModuleAction ... actions ) {
		this.enumerator = enumerator;
		this.actions = new ModuleAction[ actions.length + 1];		
		this.actionTable = new HashMap<String,ModuleAction>();
		this.actions[0] = new ModuleAction( );
		for( int i=0 ; i<actions.length ; i++ ) {
			actions[i].setActionIndex( i+1 );
			this.actions[i+1] = actions[i];
			this.actionTable.put(actions[i].getName(), actions[i]);
		}
	}	

	public int size() {
		return actions.length;
	}
	
	public ModuleAction getAction( int i ) {
		return actions[i];
	}
	
	public ModuleAction getAction( String action ) {
		return actionTable.get(action);
	}


	public ModuleAction getTauAction() {
		return getAction( 0 );
	}


	public ModuleAction[] getActionArray() {
		return Arrays.copyOf(actions, actions.length);
	}


	public Iterator<ModuleAction> getActionIterator() {
		return new Iterator<ModuleAction>() {

			private int current = 0;
			
			@Override
			public boolean hasNext() {
				return current<actions.length;
			}

			@Override
			public ModuleAction next() {
				ModuleAction next = actions[current];
				current++;
				return next;
			}

			@Override
			public void remove() {
				
			}
			
		};
	}
	

	public ReactiveModule hide( ReactiveModule module , ModuleAction ... actions ) {
		return hide( null , module , actions );
	}

	public ReactiveModule hide( String name , ReactiveModule module , ModuleAction ... actions ) {
		HashSet<ModuleAction> aSet = new HashSet<ModuleAction>();
		for (ModuleAction act : actions) {
			aSet.add(act);
		}
		return hide( name , module , aSet );
	}

	
	public ReactiveModule hide( ReactiveModule module , Set<ModuleAction> actions ) {
		return hide( null , module , actions );
	}

	public ReactiveModule hide( String name , ReactiveModule module , Set<ModuleAction> actions ) {
		ReactiveModule newModule = new ReactiveModule( name );
		for (int i = 0; i < module.rules.size() ; i++) {
			if (actions.contains( this.actions[i] ) ) {
				newModule.addRules(0, module.rules.get(i) );
			} else {
				newModule.addRules(i, module.rules.get(i) );
			}
		}
		return newModule;
	}
	
	public ReactiveModule interleaving( ReactiveModule m1 , ReactiveModule m2 ) {
		return interleaving(null, m1, m2);
	}
	
	public ReactiveModule interleaving( String name , ReactiveModule m1 , ReactiveModule m2 ) {
		ReactiveModule newModule = new ReactiveModule( name );
		for (int i = 0; i < m1.rules.size() ; i++) {
			newModule.addRules(i, m1.rules.get(i));
			newModule.addRules(i, m2.rules.get(i));
		}		
		return newModule;
	}

	public ReactiveModule synchronization( ReactiveModule m1 , ReactiveModule m2 ) {
		return synchronization(null, m1, m2);
	}
	
	public ReactiveModule synchronization( String name , ReactiveModule m1 , ReactiveModule m2 ) {
		ReactiveModule newModule = new ReactiveModule( name );
		newModule.addRules(0,m1.rules.get(0));
		newModule.addRules(0,m2.rules.get(0));
		for (int i = 1; i < m1.rules.size() ; i++) {
			LinkedList<Rule> rList1 = m1.rules.get(i);
			LinkedList<Rule> rList2 = m2.rules.get(i);
			if ((rList1 != null)&&(rList2 != null)) {
				for (Rule r1 : rList1) {
					for (Rule r2 : rList2) {
						newModule.addRule(i, r1.combine(r2));
					}
				}
			}
		}		
		return newModule;
	}
	
	public ReactiveModule cooperation(ReactiveModule m1 , Set<ModuleAction> sSet , ReactiveModule m2 ) {
		return cooperation(null, m1, sSet, m2);
	}

	public ReactiveModule cooperation(ReactiveModule m1 , ReactiveModule m2 , ModuleAction ... actions  ) {
		return cooperation( null , m1 , m2 , actions );
	}
	
	public ReactiveModule cooperation(String name ,ReactiveModule m1 , ReactiveModule m2 , ModuleAction ... actions  ) {
		HashSet<ModuleAction> sSet = new HashSet<ModuleAction>();
		for (ModuleAction act : actions) {
			sSet.add(act);
		}		
		return cooperation(name, m1, sSet, m2);
	}
	
	public ReactiveModule renaming( String name , ReactiveModule m , HashMap<ModuleAction, ModuleAction> renaming ) {
		ReactiveModule newModule = new ReactiveModule( name );
		for (int i=0 ; i<actions.length ; i++ ) {
			ModuleAction trg = renaming.get(actions[i]);
			if (trg == null) {
				trg = actions[i];
			}
			newModule.addRules(trg.getActionIndex(), m.rules.get(i));
		}
		return newModule;
	}
	
	public ReactiveModule renaming( ReactiveModule m , HashMap<ModuleAction, ModuleAction> renaming ) {
		return renaming( null , m , renaming );
	}
	
	public ReactiveModule renaming( String name , ReactiveModule m , ModuleAction[] src , ModuleAction[] trg ) {
		if (src.length != trg.length) {
			throw new IllegalArgumentException();
		}
		HashMap<ModuleAction,ModuleAction> map = new HashMap<ModuleAction, ModuleAction>();
		for( int i=0 ; i<src.length ; i++ ) {
			map.put(src[i], trg[i]);
		}		
		return renaming( name , m , map );
	}

	public ReactiveModule cooperation(String name ,ReactiveModule m1 , Set<ModuleAction> sSet , ReactiveModule m2 ) {
		ReactiveModule newModule = new ReactiveModule( name );
		newModule.addRules(0,m1.rules.get(0));
		newModule.addRules(0,m2.rules.get(0));
		for (int i = 0; i < m1.rules.size() ; i++) {
			LinkedList<Rule> rList1 = m1.rules.get(i);
			LinkedList<Rule> rList2 = m1.rules.get(i);
			if (sSet.contains( this.actions[i] ) ) {
				if ((rList1 != null)&&(rList2 != null)) {
					for (Rule r1 : rList1) {
						for (Rule r2 : rList2) {
							newModule.addRule(i, r1.combine(r2));
						}
					}
				}
			} else {
				newModule.addRules(i, rList1);
				newModule.addRules(i, rList2);
			}
		}		
		return newModule;
	}
	
	public ReactiveModule createReactiveModule( String name , Statement ... statements ) {		
		ReactiveModule module = new ReactiveModule( name );
		for (Statement statement : statements) {
			module.addRule(statement.getAct().getActionIndex(), new Rule( statement.getGuard() , statement.getCommand() ));
		}
		return module;
	}
	
	public class ReactiveModule extends AbstractReactiveModule {
		
		private ArrayList<LinkedList<Rule>> rules;
		
			
		public ReactiveModule( ) {
			this( null );
		}
		
		public ReactiveModule( String name ) {
			super( name , enumerator );
			initRuleStructure( );
		}

		private void initRuleStructure( ) {
			this.rules = new ArrayList<LinkedList<Rule>>( actions.length );
			for(  int i=0 ; i<actions.length ; i++ ) {
				rules.add(null);
			}
		}

		protected void addRule( int actionIndex , Rule r) {
			LinkedList<Rule> elements = this.rules.get(actionIndex);
			if (elements == null) {
				elements = new LinkedList<Rule>();
				this.rules.set(actionIndex, elements);
			}
			elements.add(r);
		}
		
		protected void addRules( int actionIndex , LinkedList<Rule> rules) {
			if (rules == null) {
				return ;
			}
			LinkedList<Rule> elements = this.rules.get(actionIndex);
			if (elements == null) {
				elements = new LinkedList<Rule>();
				this.rules.set(actionIndex, elements);
			}
			elements.addAll(rules);
		}

		@Override
		public HashMap<ModuleAction, Set<State>> getNext(State state) {
			HashMap<ModuleAction,Set<State>> next = new HashMap<ModuleAction, Set<State>>();
			for( int i = 0 ; i<actions.length ; i++ ) {
				LinkedList<Rule> actionRules = rules.get(i); 
				if (actionRules != null) {
					Set<State> states = new HashSet<State>();
					boolean flag = false;
					for (Rule r : actionRules) {
						if (r.getGuard().eval(state)) {
							states.add( r.getCommand().apply( state ) );
							flag = true;
						}
					}
					if (flag) {
						next.put(actions[i], states);
					}
				}			
			}
			return next;
		}

		@Override
		public Set<State> getNext(State state, ModuleAction act) {
			LinkedList<Rule> actionRules = rules.get(act.getActionIndex());
			Set<State> result = new HashSet<State>();
			if (actionRules == null) {
				return result;
			}
			for (Rule r : actionRules) {
				if (r.getGuard().eval(state)) {
					result.add(r.getCommand().apply(state));
				}
			}
			return result;
		}

	}
	

}
