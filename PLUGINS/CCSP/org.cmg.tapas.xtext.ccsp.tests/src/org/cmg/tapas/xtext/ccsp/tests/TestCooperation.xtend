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
class TestCooperation {
	
	@Inject
	ParseHelper<Model> parser
	
	@Inject extension ValidationTestHelper
	
	@Inject extension CompilationTestHelper

	@Inject extension ReflectExtensions
		
	
	@Test
	def void testCooperation() {
		'''
		model Test;
		
		channel a;
		channel b;
		
		process P:
		x -> !a.y + ?b.z;
		y -> !b.x;
		z -> ?a.x;
		end
		
		system Main1:
			P[x] |*| P[x]
		end

		system Main2:
			P[x] || P[x]
		end

		system Main3:
			P[x] |{ a }| P[x]
		end

			
		'''.compile[
			compiledClass.newInstance => [
				assertNotNull( it )
				assertTrue( it instanceof CCSPModel )
				var model = it as CCSPModel
//				model.generateLts("Main1",3,4)
//				model.generateLts("Main2",9,24)
				model.generateLts("Main3",9,24)
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