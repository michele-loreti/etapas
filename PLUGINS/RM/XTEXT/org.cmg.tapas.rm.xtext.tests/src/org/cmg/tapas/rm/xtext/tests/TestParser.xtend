package org.cmg.tapas.rm.xtext.tests

import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.util.ParseHelper
import org.cmg.tapas.rm.xtext.ModuleInjectorProvider
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import com.google.inject.Inject
import org.junit.Test
import org.cmg.tapas.rm.xtext.module.Model
import org.junit.Assert
import org.cmg.tapas.rm.xtext.module.ModulePackage
import org.cmg.tapas.rm.xtext.validation.ModuleValidator

@InjectWith(typeof(ModuleInjectorProvider))
@RunWith(typeof(XtextRunner))
class TestParser {
	
	@Inject extension ParseHelper<Model>
	
	@Inject extension ValidationTestHelper

		
	def checkModel(CharSequence prog) {
        val model = prog.parse
        Assert::assertNotNull(model)
        model.assertNoErrors
        model
    }

	@Test
	def void testPingPong() {
		'''
			specification PingPong:
			
			action ping, pong;

			module PingPong:
			variables:
				x: int(0..1);
			rules:
			[pong] x==0 -> { x <- 1 };
			[ping] x==1 -> { x <- 0 };
			endmodule
		'''.checkModel
	}
	
	@Test
	def void testMutualExclusion() {
			'''
			specification MutualExclusion:
			
			module Agent1:
			variables: 
				pc1: int(0..4); 
				y1: int(0..2); 
				cs1 : bool;
			rules:
				[] (pc1==0) -> { y1 <- y2+1 } & { pc1 <- pc1+1 };
				[] (pc1==1) 
					and (y2!=0) 
					and (y1<y2) -> { pc1 <- pc1+1 } & { cs1 <- true };
				[] (pc1==2) -> { pc1 <- pc1+1 } & { cs1 <- false };
				[] (pc1==2) -> { pc1 <- pc1+1 } & { y1 <- 0 };
			endmodule
			
			module Agent2:
			variables:
				pc2: int(0..4);
				y2: int(0..2);
				cs2 : bool;
			rules:
				Agent1[ pc1 -> pc2 , y1 -> y2 , cs1 -> cs2 ]
			endmodule
			
			
			system main:
				Agent1 || Agent2 
			endsystem
			'''.checkModel
	}
	
	@Test
	def void testRW() {
		'''
		specification ReadersWriters:
		
		
		const BUFFER_SIZE = 10;
		
		const SLEEP = 0;
		const WAIT = 1;
		const ACTIVE = 2;
		
		variable buffer: int(0..BUFFER_SIZE);

		function someOneIsReading = (r1==ACTIVE);
		
		function readerInQueue = (r1==WAIT);
		
		function someOneIsWriting = (w1==ACTIVE);
				
		function writerInQueue = (w1==WAIT);
				
		function readEnabled = !someOneIsWriting and !writerInQueue and (buffer>0);				
		
		function writeEnabled = !someOneIsReading and !readerInQueue and (buffer<BUFFER_SIZE);
		
		
				
		module R1:
		variables:
			r1: int(0..2);
		rules:
		[] (r1==SLEEP) and readEnabled -> { r1 <- ACTIVE };
		[] (r1==SLEEP) and !readEnabled -> { r1 <- WAIT };
		[] (r1==SLEEP) -> { r1 <- SLEEP };
		[] (r1==WAIT) and readEnabled -> { r1 <- ACTIVE };
		[] (r1==ACTIVE) -> { r1 <- SLEEP };
		[] (r1==ACTIVE) and readEnabled -> { r1 <- SLEEP };
		endmodule
		
		module R2:
		variables:
			r2: int(0..2);
		rules:
			R1[ r1 -> r2 ]
		endmodule

		module R3:
		variables:
			r3: int(0..2);
		rules:
			R1[ r1 -> r3 ]
		endmodule
		
		module W1:
		variables:
			w1: int(0..2);
		rules:
		[] (w1==SLEEP) and writeEnabled -> { w1 <- ACTIVE };
		[] (w1==SLEEP) and !writeEnabled -> { w1 <- WAIT };
		[] (w1==SLEEP) -> { w1 <- SLEEP };
		[] (w1==WAIT) and writeEnabled -> { w1 <- ACTIVE };
		[] (w1==ACTIVE) -> { w1 <- SLEEP };
		[] (w1==ACTIVE) and writeEnabled -> { w1 <- SLEEP };
		endmodule

		module W2:
		variables:
			w2: int(0..2);
		rules:
			W1[ w1 -> w2 ]
		endmodule

		module W3:
		variables:
			w3: int(0..2);
		rules:
			W1[ w1 -> w3 ]
		endmodule
		
		'''.checkModel
	}
	
	@Test
	def void testDF() {
		'''
		specification DiningPhilosophers:
		
		variable fork1: bool;
		variable fork2: bool;
		variable fork3: bool;
		variable fork4: bool;
		variable fork5: bool;
		
		module Philosopher1:
		variables:
			isHungry1: bool;
			holdsLeft: bool;
			holdsRight: bool;
		rules:
		
		endmodule
			
		'''.checkModel
	}
	
	
	
}