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

@InjectWith(typeof(ModuleInjectorProvider))
@RunWith(typeof(XtextRunner))
class TestPingPong {
	
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
	def void testPingPongRenaming() {
		'''
			specification PingPong:
			
			module Ping:
			variables:
				x: int(0..1);
			rules:
			[] x==0 -> { x <- 1 };
			[] x==1 -> { x <- 0 };
			endmodule
			
			module Pong:
			variables:
				y: int(0..1);
			rules:
				Ping[ x -> y ]
			endmodule
			
			system Main:
				Ping||Pong
			endsystem
		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				var sys = it.getReactiveModule("Main")
				assertEquals( 4 , sys.stateSpaceSize )		
				sys.generateGraph( 4 , 8 )
			]
		]
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
			
			system Main:
				PingPong
			endsystem
		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				it.checkVariableNames	
				var sys = it.getReactiveModule("Main")
				assertEquals( 2 , sys.stateSpaceSize )		
				sys.generateGraph( 2 , 2 )
			]
		]
	}
	
	@Test
	def void testMulti() {
		'''
			specification PingPong:
			
			action a, b;

			module A:
			variables:
				x: int(0..1);
			rules:
			[a] x==0 -> { x <- 0 };
			[b] x==0 -> { x <- 1 };
			[a] x==1 -> { x <- 1 };
			[b] x==1 -> { x <- 0 };
			endmodule
			
			module B:
			variables:
				y: int(0..1);
			rules:
			[a] y==0 -> { y <- 1 };
			[b] y==1 -> { y <- 0 };
			endmodule
			
			system MainA:
				A
			endsystem
			
			system MainB:
				B
			endsystem
			
			system MainInterleaving:
				A || B
			endsystem 

			system MainCooperation:
				A |*| B
			endsystem 

			system MainSyncOnA:
				A |a| B
			endsystem 

			system MainSyncOnB:
				A |b| B
			endsystem 

		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				var sys = it.getReactiveModule("MainA")
				assertEquals( 4 , sys.stateSpaceSize )		
				sys.generateGraph( 4 , 8 )
				sys = it.getReactiveModule("MainB")
				assertEquals( 4 , sys.stateSpaceSize )		
				sys.generateGraph( 4 , 4 )
				sys = it.getReactiveModule("MainInterleaving")
				assertEquals( 4 , sys.stateSpaceSize )		
				sys.generateGraph( 4 , 12 )
				sys = it.getReactiveModule("MainCooperation")
				assertEquals( 4 , sys.stateSpaceSize )		
				sys.generateGraph( 4 , 4 )
				sys = it.getReactiveModule("MainSyncOnA")
				assertEquals( 4 , sys.stateSpaceSize )		
				sys.generateGraph( 4 , 8 )
				sys = it.getReactiveModule("MainSyncOnB")
				assertEquals( 4 , sys.stateSpaceSize )		
				sys.generateGraph( 4 , 8 )
			]
		]
	}
	
	
	def getReactiveModule( Object object , String name ) {
		var data = object.invoke( "getSystem" , name )
		assertNotNull( data )
		assertTrue( data instanceof AbstractReactiveModule )
		data as AbstractReactiveModule
	}
	
	def checkVariableNames( Object value ) {
		var o = value.invoke( "getVariableNames" )
		assertTrue(o instanceof String[])
		val variables = o as String[]
		print (variables.join(' '))
		assertEquals( 1 , variables.length )
		var v = variables.get(0)
		assertEquals( "x" , v )
	} 
	
	def generateGraph( AbstractReactiveModule m , int states , int edges ) {
		var g = m.graph
		assertEquals( states , g.numberOfStates )
		assertEquals( edges , g.numberOfEdges )		
	}
}


