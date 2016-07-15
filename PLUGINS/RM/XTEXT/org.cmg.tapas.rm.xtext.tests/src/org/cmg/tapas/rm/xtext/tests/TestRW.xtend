package org.cmg.tapas.rm.xtext.tests

import com.google.inject.Inject
import org.cmg.tapas.rm.runtime.StateEnumerator
import org.cmg.tapas.rm.xtext.ModuleInjectorProvider
import org.cmg.tapas.rm.xtext.module.Model
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.xbase.compiler.CompilationTestHelper
import org.eclipse.xtext.xbase.lib.util.ReflectExtensions
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.cmg.tapas.rm.runtime.AbstractReactiveModule

@InjectWith(typeof(ModuleInjectorProvider))
@RunWith(typeof(XtextRunner))
class TestRW {
	
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
	def void doTest() {
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
		
		'''.compile[ 
			compiledClass.newInstance => [
				assertNotNull( it )
				assertEquals( 10 , it.invoke( "getValueOf" , "BUFFER_SIZE") )			
				assertEquals( 0 , it.invoke( "getValueOf" , "SLEEP") )			
				assertEquals( 1 , it.invoke( "getValueOf" , "WAIT") )			
				assertEquals( 2 , it.invoke( "getValueOf" , "ACTIVE") )		
				it.checkVariableNames			
				it.checkStateEnumeration
			]
		]
	}
	
	def checkVariableNames( Object value ) {
		var o = value.invoke( "getVariableNames" )
		assertTrue(o instanceof String[])
		val variables = o as String[]
		print (variables.join(' '))
		assertEquals( 7 , variables.length )
		for ( i:0 .. 6) {
			var v = variables.get(i)
			assertEquals( i , value.invoke( "getIndexOf" ,  v ) )
		}
	} 
	
	def checkStateEnumeration( Object value ) {
		var o = value.invoke( "getEnumerator" )
		assertTrue(o instanceof StateEnumerator)
		var enumerator = o as StateEnumerator
		var state = enumerator.enumerate( 0 , 0 , 0 , 0 , 0 , 0 , 0 )
		var newState = enumerator.set( state , 0 , 5 )
		assertEquals( enumerator.enumerate( 5 , 0 , 0 , 0 , 0 , 0 , 0 ) , newState )
	}
	
	def getReactiveModule( Object object , String name ) {
		var data = object.invoke( "getSystem" , name )
		assertNotNull( data )
		assertTrue( data instanceof AbstractReactiveModule )
		data as AbstractReactiveModule
	}
		
	def generateGraph( AbstractReactiveModule m ) {
		var g = m.graph
		assertEquals( 2 , g.numberOfStates )
		assertEquals( 2 , g.numberOfEdges )		
	}
	
	
	
}