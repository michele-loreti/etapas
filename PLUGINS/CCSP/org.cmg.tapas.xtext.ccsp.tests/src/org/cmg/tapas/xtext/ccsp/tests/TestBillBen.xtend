package org.cmg.tapas.xtext.ccsp.tests

import com.google.inject.Inject
import org.cmg.tapas.ccsp.runtime.CCSPModel
import org.cmg.tapas.ccsp.runtime.CCSPUtil
import org.cmg.tapas.xtext.ccsp.CCSPSpecificationInjectorProvider
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Model
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.xbase.compiler.CompilationTestHelper
import org.eclipse.xtext.xbase.lib.util.ReflectExtensions
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.cmg.tapas.ccsp.runtime.CCSPAction.InputAction
import org.cmg.tapas.ccsp.runtime.CCSPAction.OutputAction

@InjectWith(typeof(CCSPSpecificationInjectorProvider))
@RunWith(typeof(XtextRunner))
class TestBillBen {
	
	@Inject
	ParseHelper<Model> parser
	
	@Inject extension ValidationTestHelper
	
	@Inject extension CompilationTestHelper

	@Inject extension ReflectExtensions
		
	
	@Test
	def void testBillBen() {
		'''
		model BillBen;
		
		channel work;
		channel meet;
		channel play;
		channel ciao;
		
		process Ben:
		x -> !work.Ben[x2];
		x2 -> !meet.nil;
		end
		
		process Bill:
		x -> ?play.Bill[x2];
		x2 -> ?meet.nil;
		end
		
		system Sys:
		Bill[x] | Ben[x]
		end	
		
		system Sys2:
		Bill[x2] | Ben[x2]
		end	
		
		hml formula testFormula:
			true
		end
		
			
		'''.compile[
			compiledClass.newInstance => [
				assertNotNull( it )
				assertTrue( it instanceof CCSPModel )
				var model = it as CCSPModel
				model.generateLts("Sys",9,13)
				model.generateLts("Sys2",4,5)
 				var ben = model.getState("Ben","x")
 				var nextOfBen = ben.next
 				assertNull( nextOfBen.get( new InputAction( model.getChannel("work") ) ) )
 				assertNotNull( nextOfBen.get( new OutputAction( model.getChannel("work") ) ) )
				var bill = model.getState("Bill","x") 				
 				var nextOfBill = bill.next
 				assertNotNull( nextOfBill.get( new InputAction( model.getChannel("play") ) ) )
 				assertNull( nextOfBill.get( new OutputAction( model.getChannel("play") ) ) )
 			]
		]		
	}
	
	@Test
	def void testParsing() {
		'''
		model PingPong;
		
		channel get;
		channel set;
		
		process GetSet:
			A -> ?set.GetSet[B];
			B -> !get.A;
		end
		
		hml formula no_deadlock:
			max X. < * > true & [ * ] X
		end
		
		process SetGet:
			X -> !set.Y;
			Y -> ?get.nil;
		end
		
		system Parallel:
			GetSet[A]|SetGet[X]
		end
		
		system Restriction:
			Parallel\{ set , get }
		end
		
		system Renaming:
			Parallel< set -> get , get -> set >
		end
		
		'''.compile[
			compiledClass.newInstance => [
				assertNotNull( it )
				assertTrue( it instanceof CCSPModel )
				var model = it as CCSPModel
				model.generateLts("GetSet","A",2,2)
				model.generateLts("GetSet","B",2,2)
				model.generateLts("SetGet","X",3,2)
				model.generateLts("SetGet","Y",2,1)
				model.generateLts("Parallel",6,12)
				model.generateLts("Restriction",3,2)
				model.generateLts("Renaming",6,12)
 				
 			]
		]		
	}
	
	def generateLts( CCSPModel model , String system , int states , int edges ) {
		var sys = model.getSystem( system )
		assertNotNull("System "+system,sys)
		var lts = CCSPUtil::getGraph(sys)
		assertEquals("Number of states in "+system,states,lts.numberOfStates)
		assertEquals("Number of edges in "+system,edges,lts.numberOfEdges)
	}

	def generateLts( CCSPModel model , String process , String state , int states , int edges ) {
		var sys = model.getState( process , state )
		assertNotNull("Process "+process+"["+state+"]",sys)
		var lts = CCSPUtil::getGraph(sys)
		assertEquals("Number of states in "+process+"["+state+"]",states,lts.numberOfStates)
		assertEquals("Number of edges in "+process+"["+state+"]",edges,lts.numberOfEdges)
	}
	
}