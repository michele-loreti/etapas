package org.cmg.tapas.xtext.ccsp.tests

import com.google.inject.Inject
import org.cmg.tapas.ccsp.runtime.CCSPModel
import org.cmg.tapas.ccsp.runtime.CCSPState
import org.cmg.tapas.ccsp.runtime.CCSPUtil
import org.cmg.tapas.core.util.NamedElement
import org.cmg.tapas.xtext.ccsp.CCSPSpecificationInjectorProvider
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Model
import org.cmg.tapas.xtext.ccsp.generator.CCSPSpecificationGenerator
import org.eclipse.xtext.generator.InMemoryFileSystemAccess
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

@InjectWith(typeof(CCSPSpecificationInjectorProvider))
@RunWith(typeof(XtextRunner))
class TestDP {
	
	@Inject
	ParseHelper<Model> parser
	
	@Inject extension ValidationTestHelper
	
//	@Inject
//	CCSPSpecificationGenerator generator
	
	@Test
	def void testParsing() {
		var data = parser.parse(
			'''
			model DF;
			
			channel getL;
			channel getR;
			channel releaseL;
			channel releaseR;
			
			process Philosopher:
				Thinking -> tau.Angry;
				Angry -> ?getL.WaitingRight;
				WaitingRight -> ?getR.Eating;
				Eating -> !releaseL.ReleasingRight;
				ReleasingRight -> !releaseR.Thinking;
			end
			
			
			channel get_fork;
			channel release_fork;
			
			process Fork:
				Available -> !get_fork.UnAvailable;
				UnAvailable -> ?release_fork.Available;
			end
			
			channel getFork1;
			channel relFork1;
			channel getFork2;
			channel relFork2;
			channel getFork3;
			channel relFork3;
			channel getFork4;
			channel relFork4;
			channel getFork5;
			channel relFork5;
			
			system Main:
				(
				 Fork[Available]< get_fork -> getFork1 , release_fork -> relFork1 >
				|Philosopher[Thinking]< getL -> getFork1 , releaseL -> relFork1 , getR -> getFork2 , releaseR -> relFork2 >
				|Fork[Available]< get_fork -> getFork2 , release_fork -> relFork2 >
				|Philosopher[Thinking]< getL -> getFork2 , releaseL -> relFork2 , getR -> getFork3 , releaseR -> relFork3 >
				|Fork[Available]< get_fork -> getFork3 , release_fork -> relFork3 >
				|Philosopher[Thinking]< getL -> getFork3 , releaseL -> relFork3 , getR -> getFork4 , releaseR -> relFork4 >
				|Fork[Available]< get_fork -> getFork4 , release_fork -> relFork4 >
				|Philosopher[Thinking]< getL -> getFork4 , releaseL -> relFork4 , getR -> getFork1 , releaseR -> relFork1 >
			//	|Fork[Available]< get_fork -> getFork5 , release_fork -> relFork5 >
			//	|Philosopher[Thinking]< getL -> getFork5 , releaseL -> relFork5 , getR -> getFork1 , releaseR -> relFork1 >
				)\{ getFork1 , getFork2 , relFork1 , relFork2 }
			end
			
			'''
		)
		data.assertNoErrors
//		var access = new InMemoryFileSystemAccess();
//		print("FILES:\n")
//		generator.doGenerate(data.eResource(), access);
//		for (f: access.files.keySet) {
//			print(f+"\n")			
//		}
//		compiler.addClassPathOfClass(typeof(CCSPState));
//		compiler.addClassPathOfClass(typeof(NamedElement));
//		print("ENDFILES\n")
//		if (access.files.size > 0) {
//			for (e: access.files.entrySet) {
//				var name = e.getKey().substring("DEFAULT_OUTPUT".length(), e.getKey().length() - ".java".length())
//				name = name.replace('/', '.');
//				//var clazz = 
//				var c = compiler.compileToClass(name, e.getValue().toString())
//				var o = c.newInstance as CCSPModel
//				for (p:o.processes) {
//					print("PROCESS: "+p+"\n")
//					print("STATES:\n")
//					for(s:o.getStates(p)) {
//						print(s+"\n	")
//					}				
//					print("ENDSTATES\n\n")	
//				}
//				for (s:o.systems) {
//					print("SYSTEM: "+s+"\n")
//					var g = CCSPUtil::getGraph(o.getSystem(s))					
//					print("Nodes: "+g.numberOfStates+"\n")
//					print("Edges: "+g.numberOfEdges+"\n")
//				}
//				
//				/*
//				 * 									 
//				 */
//			}			
//		} 
		
		
		
	}
	
}