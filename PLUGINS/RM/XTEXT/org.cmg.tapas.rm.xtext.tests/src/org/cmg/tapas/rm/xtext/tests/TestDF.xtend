package org.cmg.tapas.rm.xtext.tests

import com.google.inject.Inject
import org.cmg.tapas.rm.xtext.ModuleInjectorProvider
import org.cmg.tapas.rm.xtext.module.Model
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.xtext.xbase.compiler.CompilationTestHelper
import org.eclipse.xtext.xbase.lib.util.ReflectExtensions

import static org.junit.Assert.*
import org.cmg.tapas.rm.runtime.AbstractReactiveModule
import org.cmg.tapas.rm.runtime.State
import java.util.HashSet

@InjectWith(typeof(ModuleInjectorProvider))
@RunWith(typeof(XtextRunner))
class TestDF {
	
	@Inject extension ParseHelper<Model>
	
	@Inject extension ValidationTestHelper
	
	@Inject extension CompilationTestHelper

	@Inject extension ReflectExtensions
	

		
	def checkModel(CharSequence prog) {
        val model = prog.parse
        assertNotNull(model)
        model.assertNoErrors
        model
    }

	@Test
	def void testDF2() {
		'''
			specification DiningPhilosphers:
			
			variable f0: int(0..1);
			variable f1: int(0..1);
			
			module P1:
			variables:
				s1: int(0..3);
			rules:
			[] (s1==0) -> { s1 <- 1 };
			[] (s1==1) and (f0==1) -> { s1 <- 2 } & { f0 <- 0 };
			[] (s1==2) and (f1==1) -> { s1 <- 3 } & { f1 <- 0 };
			[] (s1==3) -> { s1 <- 0 } & { f1 <- 1 } & {f0 <- 1 };
			endmodule
			
			module P2:
			variables:
				s2: int(0..3);
			rules:
				P1[ s1 -> s2 , f0 -> f1 , f1 -> f0 ]
			endmodule
			
			
			system Main:
				P1 || P2
			init:
				(s1+s2==0) and (f0+f1==2) 
			endsystem
			
		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				var sys = it.getReactiveModule("Main")
				assertEquals( 64 , sys.stateSpaceSize )		
				sys.generateGraph( 64 , 96 )
			]
		]
	}
	
	@Test
	def void testDF3() {
		'''
			specification DiningPhilosphers:
			
			variable f0: int(0..1);
			variable f1: int(0..1);
			variable f2: int(0..1);
			
			module P1:
			variables:
				s1: int(0..3);
			rules:
			[] (s1==0) -> { s1 <- 1 };
			[] (s1==1) and (f0==1) -> { s1 <- 2 } & { f0 <- 0 };
			[] (s1==2) and (f1==1) -> { s1 <- 3 } & { f1 <- 0 };
			[] (s1==3) -> { s1 <- 0 } & { f1 <- 1 } & {f0 <- 1 };
			endmodule
			
			module P2:
			variables:
				s2: int(0..3);
			rules:
				P1[ s1 -> s2 , f0 -> f1 , f1 -> f2 ]
			endmodule

			module P3:
			variables:
				s3: int(0..3);
			rules:
				P1[ s1 -> s3 , f0 -> f2 , f1 -> f0 ]
			endmodule
			
			
			system Main:
				P1 || P2 || P3
			endsystem
			
		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				var sys = it.getReactiveModule("Main")
				assertEquals( 512 , sys.stateSpaceSize )		
				sys.generateGraph( 512 , 1152 )
			]
		]
	}
	
	@Test
	def void testDF4() {
		'''
			specification DiningPhilosphers:
			
			variable f0: int(0..1);
			variable f1: int(0..1);
			variable f2: int(0..1);
			variable f3: int(0..1);
			
			module P1:
			variables:
				s1: int(0..3);
			rules:
			[] (s1==0) -> { s1 <- 1 };
			[] (s1==1) and (f0==1) -> { s1 <- 2 } & { f0 <- 0 };
			[] (s1==2) and (f1==1) -> { s1 <- 3 } & { f1 <- 0 };
			[] (s1==3) -> { s1 <- 0 } & { f1 <- 1 } & {f0 <- 1 };
			endmodule
			
			module P2:
			variables:
				s2: int(0..3);
			rules:
				P1[ s1 -> s2 , f0 -> f1 , f1 -> f2 ]
			endmodule

			module P3:
			variables:
				s3: int(0..3);
			rules:
				P1[ s1 -> s3 , f0 -> f2 , f1 -> f3 ]
			endmodule

			module P4:
			variables:
				s4: int(0..3);
			rules:
				P1[ s1 -> s4 , f0 -> f3 , f1 -> f0 ]
			endmodule
			
			
			system Main:
				P1 || P2 || P3 || P4
			endsystem
			
		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				var sys = it.getReactiveModule("Main")
				assertEquals( 4096 , sys.stateSpaceSize )		
				sys.generateGraph( 4096 , 12288 )
			]
		]
	}
	
	@Test
	def void testDF5() {
		'''
			specification DiningPhilosphers:
			
			variable f0: int(0..1);
			variable f1: int(0..1);
			variable f2: int(0..1);
			variable f3: int(0..1);
			variable f4: int(0..1);
			
			module P1:
			variables:
				s1: int(0..3);
			rules:
			[] (s1==0) -> { s1 <- 1 };
			[] (s1==1) and (f0==1) -> { s1 <- 2 } & { f0 <- 0 };
			[] (s1==2) and (f1==1) -> { s1 <- 3 } & { f1 <- 0 };
			[] (s1==3) -> { s1 <- 0 } & { f1 <- 1 } & {f0 <- 1 };
			endmodule
			
			module P2:
			variables:
				s2: int(0..3);
			rules:
				P1[ s1 -> s2 , f0 -> f1 , f1 -> f2 ]
			endmodule

			module P3:
			variables:
				s3: int(0..3);
			rules:
				P1[ s1 -> s3 , f0 -> f2 , f1 -> f3 ]
			endmodule

			module P4:
			variables:
				s4: int(0..3);
			rules:
				P1[ s1 -> s4 , f0 -> f3 , f1 -> f4 ]
			endmodule
			
			module P5:
			variables:
				s5: int(0..3);
			rules:
				P1[ s1 -> s5 , f0 -> f4 , f1 -> f0 ]
			endmodule
			
			system Main:
				P1 || P2 || P3 || P4 || P5
			endsystem
			
		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				var sys = it.getReactiveModule("Main")
				assertEquals( 32768 , sys.stateSpaceSize )		
				sys.generateGraph( 32768 , 122880 )
			]
		]
	}
	
	@Test
	def void testDF6() {
		'''
			specification DiningPhilosphers:
			
			variable f0: int(0..1);
			variable f1: int(0..1);
			variable f2: int(0..1);
			variable f3: int(0..1);
			variable f4: int(0..1);
			variable f5: int(0..1);
			
			module P1:
			variables:
				s1: int(0..3);
			rules:
			[] (s1==0) -> { s1 <- 1 };
			[] (s1==1) and (f0==1) -> { s1 <- 2 } & { f0 <- 0 };
			[] (s1==2) and (f1==1) -> { s1 <- 3 } & { f1 <- 0 };
			[] (s1==3) -> { s1 <- 0 } & { f1 <- 1 } & {f0 <- 1 };
			endmodule
			
			module P2:
			variables:
				s2: int(0..3);
			rules:
				P1[ s1 -> s2 , f0 -> f1 , f1 -> f2 ]
			endmodule

			module P3:
			variables:
				s3: int(0..3);
			rules:
				P1[ s1 -> s3 , f0 -> f2 , f1 -> f3 ]
			endmodule

			module P4:
			variables:
				s4: int(0..3);
			rules:
				P1[ s1 -> s4 , f0 -> f3 , f1 -> f4 ]
			endmodule
			
			module P5:
			variables:
				s5: int(0..3);
			rules:
				P1[ s1 -> s5 , f0 -> f4 , f1 -> f5 ]
			endmodule

			module P6:
			variables:
				s6: int(0..3);
			rules:
				P1[ s1 -> s6 , f0 -> f5 , f1 -> f0 ]
			endmodule
			
			system Main:
				P1 || P2 || P3 || P4 || P5 || P6
			endsystem
			
		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				var sys = it.getReactiveModule("Main")
				assertEquals( 262144 , sys.stateSpaceSize )		
				assertEquals( 262144 , sys.iterateStates.size )
				assertEquals( 262144 , sys.iterateStates.size )
//				sys.generateGraph( 262144 , 1179648 )
			]
		]
	}	
	
	@Test
	def void testDF7() {
		'''
			specification DiningPhilosphers:
			
			variable f0: int(0..1);
			variable f1: int(0..1);
			variable f2: int(0..1);
			variable f3: int(0..1);
			variable f4: int(0..1);
			variable f5: int(0..1);
			variable f6: int(0..1);
			
			module P1:
			variables:
				s1: int(0..3);
			rules:
			[] (s1==0) -> { s1 <- 1 };
			[] (s1==1) and (f0==1) -> { s1 <- 2 } & { f0 <- 0 };
			[] (s1==2) and (f1==1) -> { s1 <- 3 } & { f1 <- 0 };
			[] (s1==3) -> { s1 <- 0 } & { f1 <- 1 } & {f0 <- 1 };
			endmodule
			
			module P2:
			variables:
				s2: int(0..3);
			rules:
				P1[ s1 -> s2 , f0 -> f1 , f1 -> f2 ]
			endmodule

			module P3:
			variables:
				s3: int(0..3);
			rules:
				P1[ s1 -> s3 , f0 -> f2 , f1 -> f3 ]
			endmodule

			module P4:
			variables:
				s4: int(0..3);
			rules:
				P1[ s1 -> s4 , f0 -> f3 , f1 -> f4 ]
			endmodule
			
			module P5:
			variables:
				s5: int(0..3);
			rules:
				P1[ s1 -> s5 , f0 -> f4 , f1 -> f5 ]
			endmodule

			module P6:
			variables:
				s6: int(0..3);
			rules:
				P1[ s1 -> s6 , f0 -> f5 , f1 -> f6 ]
			endmodule

			module P7:
			variables:
				s7: int(0..3);
			rules:
				P1[ s1 -> s7 , f0 -> f6 , f1 -> f0 ]
			endmodule
			
			system Main:
				P1 || P2 || P3 || P4 || P5 || P6 || P7
			endsystem
			
		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				var sys = it.getReactiveModule("Main")
				assertEquals( 2097152 , sys.stateSpaceSize )		
///				assertEquals( 2097152 , sys.iterateStates.size )
//				assertEquals( 2097152 , sys.iterateStates.size )
				sys.generateGraph( 262144 , 1179648 )
			]
		]
	}	

	@Test
	def void testDF8() {
		'''
			specification DiningPhilosphers:
			
			variable f0: int(0..1);
			variable f1: int(0..1);
			variable f2: int(0..1);
			variable f3: int(0..1);
			variable f4: int(0..1);
			variable f5: int(0..1);
			variable f6: int(0..1);
			variable f7: int(0..1);
			
			module P1:
			variables:
				s1: int(0..3);
			rules:
			[] (s1==0) -> { s1 <- 1 };
			[] (s1==1) and (f0==1) -> { s1 <- 2 } & { f0 <- 0 };
			[] (s1==2) and (f1==1) -> { s1 <- 3 } & { f1 <- 0 };
			[] (s1==3) -> { s1 <- 0 } & { f1 <- 1 } & {f0 <- 1 };
			endmodule
			
			module P2:
			variables:
				s2: int(0..3);
			rules:
				P1[ s1 -> s2 , f0 -> f1 , f1 -> f2 ]
			endmodule

			module P3:
			variables:
				s3: int(0..3);
			rules:
				P1[ s1 -> s3 , f0 -> f2 , f1 -> f3 ]
			endmodule

			module P4:
			variables:
				s4: int(0..3);
			rules:
				P1[ s1 -> s4 , f0 -> f3 , f1 -> f4 ]
			endmodule
			
			module P5:
			variables:
				s5: int(0..3);
			rules:
				P1[ s1 -> s5 , f0 -> f4 , f1 -> f5 ]
			endmodule

			module P6:
			variables:
				s6: int(0..3);
			rules:
				P1[ s1 -> s6 , f0 -> f5 , f1 -> f6 ]
			endmodule

			module P7:
			variables:
				s7: int(0..3);
			rules:
				P1[ s1 -> s7 , f0 -> f6 , f1 -> f7 ]
			endmodule

			module P8:
			variables:
				s8: int(0..3);
			rules:
				P1[ s1 -> s8 , f0 -> f7 , f1 -> f0 ]
			endmodule
			
			system Main:
				P1 || P2 || P3 || P4 || P5 || P6 || P7 || P8
			endsystem
			
		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				var sys = it.getReactiveModule("Main")
				assertEquals( 16777216 , sys.stateSpaceSize )		
///				assertEquals( 2097152 , sys.iterateStates.size )
//				assertEquals( 2097152 , sys.iterateStates.size )
//				sys.generateGraph( 262144 , 1179648 )
			]
		]
	}	
	
	def getReactiveModule( Object object , String name ) {
		var data = object.invoke( "getSystem" , name )
		assertNotNull( data )
		assertTrue( data instanceof AbstractReactiveModule )
		data as AbstractReactiveModule
	}

	
	def generateGraph( AbstractReactiveModule m , int states , int edges ) {
		var g = m.graph
		assertEquals( states , g.numberOfStates )
		assertEquals( edges , g.numberOfEdges )		
	}
	
	def iterateStates( AbstractReactiveModule m ) {
		var set = new HashSet<State>();
		for (s:m.states) {
			set.add(s);
		}
		set
	}
}