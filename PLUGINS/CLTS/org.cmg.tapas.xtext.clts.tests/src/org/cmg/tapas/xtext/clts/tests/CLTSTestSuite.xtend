package org.cmg.tapas.xtext.clts.tests

import org.eclipse.xtext.junit4.InjectWith
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.XtextRunner
import org.cmg.tapas.xtext.clts.ComposedLtsInjectorProvider
import com.google.inject.Inject
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.junit4.util.ParseHelper
import org.cmg.tapas.xtext.clts.composedLts.Model
import org.junit.Test
import org.eclipse.xtext.xbase.compiler.CompilationTestHelper
import org.eclipse.xtext.xbase.lib.util.ReflectExtensions

import static org.junit.Assert.*
import org.cmg.tapas.clts.runtime.CltsModule
import org.cmg.tapas.clts.runtime.CltsProcess
import org.cmg.tapas.formulae.ltl.LtlFormula
import org.cmg.tapas.formulae.ltl.LtlModelChecker
import org.cmg.tapas.clts.runtime.CLTSUtil

@InjectWith(typeof(ComposedLtsInjectorProvider))
@RunWith(typeof(XtextRunner))
class CLTSTestSuite {
	
	@Inject extension ValidationTestHelper

	@Inject extension ParseHelper<Model>
	
	@Inject extension CompilationTestHelper

	@Inject extension ReflectExtensions
	
	@Test
	def testDF() {
		'''
model DF;

action tic;
action get0;
action rel0;
action get1;
action rel1;
action get2;
action rel2;

label hl0;
label hr0;
label hl1;
label hr1;
label hl2;
label hr2;
label e0;
label e1;
label e2;


lts P0 {	
	states: T, H, HL, HR, E, RL, RR;
	
	init: T;
	
	rules:
	T - tic -> H;
	H - get0 -> HL;
	H - get1 -> HR;
	HL - get1 -> E;
	HR - get0 -> E;
	E - rel0 -> RR;
	E - rel1 -> RL;
	RR - rel1 -> T;
	RL - rel0 -> T;	
	endrules	
	
	labels:
	hl0: HL,E,RL;
	hr0: HR,E,RR;
	e0: E;
	endlabels
	
}

lts P1 {	
	states: T, H, HL, HR, E, RL, RR;
	
	init: T;
	
	rules:
	T - tic -> H;
	H - get1 -> HL;
	H - get2 -> HR;
	HL - get2 -> E;
	HR - get1 -> E;
	E - rel1 -> RR;
	E - rel2 -> RL;
	RR - rel2 -> T;
	RL - rel1 -> T;	
	endrules
	
	labels:
	hl1: HL,E,RL;
	hr1: HR,E,RR;
	e1: E;
	endlabels
	
}

lts P2 {	
	states: T, H, HL, HR, E, RL, RR;
	
	init: T;
	
	rules:
	T - tic -> H;
	H - get2 -> HL;
	H - get0 -> HR;
	HL - get0 -> E;
	HR - get2 -> E;
	E - rel2 -> RR;
	E - rel0 -> RL;
	RR - rel0 -> T;
	RL - rel2 -> T;	
	endrules
	
	labels:
	hl2: HL,E,RL;
	hr2: HR,E,RR;
	e2: E;
	endlabels
	
}



lts F0 {
	states: A, O;
	init: A;
	rules:
	A - get0 -> O;
	O - rel0 -> A;
	endrules	
}

lts F1 = F0{ get0 -> get1 , rel0 -> rel1 };

lts F2 = F0{ get0 -> get2 , rel0 -> rel2 };

lts SysP = P0||P1||P2;

lts SysF = F0||F1||F2;

lts Sys = SysP |get0,rel0,get1,rel1,get2,rel2| SysF;

ltl formula CorrectUse0 = !\F (hl0 & hr1); 

ltl formula EventuallyEat0 = \G \F e0;
		'''.compile[ 
					compiledClass.newInstance => [
						assertNotNull( it )
						var sys = it.getReactiveModule("Sys")
						var f = it.getLTLFormula("CorrectUse0")
						assertTrue( CLTSUtil::check(sys,f) )
//						assertEquals( 64 , sys.stateSpaceSize )		
//						sys.generateGraph( 64 , 96 )
					]
					
		]
	}
	
	def getReactiveModule( Object object , String name ) {
		var data = object.invoke( "getSystem" , name )
		assertNotNull( data )
		assertTrue( data instanceof CltsProcess )
		data as CltsProcess
	}
	
	def getLTLFormula( Object object , String name ) {
		var data = object.invoke( "getLtlFormula" , name )
		assertNotNull( data )
		assertTrue( data instanceof LtlFormula<?> )
		data as LtlFormula<CltsProcess>
		
		
		
	}

	
	
	
}