package org.cmg.tapas.rm.runtime.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Set;

import org.cmg.tapas.rm.runtime.Command;
import org.cmg.tapas.rm.runtime.Guard;
import org.cmg.tapas.rm.runtime.ModuleAction;
import org.cmg.tapas.rm.runtime.ModuleFactory;
import org.cmg.tapas.rm.runtime.ModuleFactory.ReactiveModule;
import org.cmg.tapas.rm.runtime.Rule;
import org.cmg.tapas.rm.runtime.State;
import org.cmg.tapas.rm.runtime.Statement;
import org.junit.Test;

public class TestSimpleModule {

	
//	private ModuleFactory factory = new ModuleFactory("a", "b");
//	
//	private ModuleAction TAU = factory.getAction(0);
//	
//	private ModuleAction ACTION_A = factory.getAction(1);
//	
//	private ModuleAction ACTION_B = factory.getAction(2);
//	
//	private ReactiveModule module = factory.createReactiveModule(
//			new Statement( 
//					TAU ,
//					new Guard( ) {
//
//						@Override
//						public boolean eval(State state) {
//							return state.get(0)==0;						}						
//					} ,
//					new Command() {
//						
//					}
//			) 			
//	);
////	new BasicModule(
////			"test", 
////			this.factory , 
////			new Rule( TAU ) {
////
////				@Override
////				protected State update(State state) {
////					state.set( 0 , 1 );
////					return state;
////				}
////
////				@Override
////				public boolean isEnabled(State state) {					
////					return state.get(0)==0;
////				}
////				
////			} ,
////			new Rule( TAU ) {
////
////				@Override
////				protected State update(State state) {
////					state.set(0, 0);
////					return state;
////				}
////
////				@Override
////				public boolean isEnabled(State state) {
////					return state.get(0)==1;
////				}
////				
////			}
////	);
//	
//	
//	@Test
//	public void test() {
//		State state = new State( null , module, 0 );
//		HashMap<ModuleAction, Set<State>> next = state.getNext();
//		assertNull(next.get(ACTION_A));
//		assertNull(next.get(ACTION_B));
//		Set<State> nextStates = next.get(TAU);
//		assertNotNull(nextStates);
//		assertEquals(1, nextStates.size());
//		State otherState = (State) nextStates.toArray()[0];
//		assertEquals( new State(null , module, 1 ) , otherState );
//		next = otherState.getNext();
//		assertNull(next.get(ACTION_A));
//		assertNull(next.get(ACTION_B));
//		nextStates = next.get(TAU);
//		assertNotNull(nextStates);
//		assertEquals(1, nextStates.size());		
//		assertEquals( state , nextStates.toArray()[0] );
//	}
	
	
}
