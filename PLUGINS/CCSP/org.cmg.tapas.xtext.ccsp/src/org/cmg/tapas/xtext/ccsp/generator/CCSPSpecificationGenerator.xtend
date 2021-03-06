/*
 * generated by Xtext
 */
package org.cmg.tapas.xtext.ccsp.generator

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IGenerator
import org.eclipse.xtext.generator.IFileSystemAccess
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Model
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.TauAction
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.InputAction
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.OutputAction
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.NilProcess
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateReference
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessReference
import org.eclipse.emf.ecore.EObject
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Restriction
import org.eclipse.emf.common.util.EList
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Channel
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.SystemBody
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ParallelComposition
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.SystemReference
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Renaming
import org.cmg.tapas.TAPAsProjectHelper
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ActlFormulaDeclaration
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlFormulaDeclaration
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlOrFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlTrue
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlFalse
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlAndFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlNotFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlDiamondFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlBoxFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlMinFixPoint
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlMaxFixPoint
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlRecursionFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.HmlRecursionVariable
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.CupLabelPredicate
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.CapLabelPredicate
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.NotLabelPredicate
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.AnyLabelPredicate
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ActionLabelPredicate
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.LabelPredicate
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.CooperationComposition
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.InterleavingComposition
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.SynchronizationComposition
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.OrFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.AndFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.TrueFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.FalseFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ExistsFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ForAllFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.PathFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.NextFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.UntilFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.AlwaysFormula
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.EventuallyFormula

class CCSPSpecificationGenerator implements IGenerator {
	
	var channelCounter = 0;
	var stateCounter = 0;
	
	override void doGenerate(Resource resource, IFileSystemAccess fsa) {

		for( m: resource.allContents.toIterable.filter(typeof(Model)) ) {
			println(resource.URI)
			print("Is relative: ")
			println(resource.URI.relative)
			print("Is file: ")
			println(resource.URI.file)
			print("Is hierarchical: ")
			println(resource.URI.hierarchical)
			print("Path: ")
			println(resource.URI.path)
			print("Is platform: ")
			println(resource.URI.platform)
			print("To platform (true): ")
			println(resource.URI.toPlatformString(true))
			print("To platform (false): ")
			println(resource.URI.toPlatformString(false))
			print("Package folder: ")
			println(TAPAsProjectHelper.getPackageFolder(resource))
			fsa.generateFile(
				TAPAsProjectHelper.getPackageFolder(resource)+"/"+m.name+".java",
				m.generateSpecificationClass(
					TAPAsProjectHelper.getPackageFolder(resource).replace('/','.')
				)
			);
		}


//		for (r:resource.contents) {
//			r.doGenerateSpecificationClass(fsa)
//		}
	}
	
//	def dispatch void doGenerateSpecificationClass( EObject e , IFileSystemAccess fsa) {
//		
//	}
//	
//	def dispatch void doGenerateSpecificationClass( Model m , IFileSystemAccess fsa) {
//			fsa.generateFile(m.name+".java" , m.generateSpecificationClass )	
//	}
	
	def generateSpecificationClass( Model m , String pack ) {
		'''		
		package «pack»;
		
		import org.cmg.tapas.core.util.*;
		import org.cmg.tapas.formulae.actl.*;
		import org.cmg.tapas.formulae.actl.path.*;
		import org.cmg.tapas.formulae.actl.state.*;
		import org.cmg.tapas.ccsp.runtime.*;
		import org.cmg.tapas.formulae.hml.*;
		import org.cmg.tapas.core.graph.filter.*;
		import java.util.*;
		
		
		public class «m.name» extends CCSPModel {
			
			«FOR c:m.channels»
			private static CCSPChannel _CH_«c.name» = new CCSPChannel( "«c.name»" , «channelId» );				
			«ENDFOR»
			
			public CCSPChannel getChannel( String name ) {
				«FOR c:m.channels»
				if ("«c.name»".equals( name ) ) {
					return _CH_«c.name»;									
				}
				«ENDFOR»
				return null;
			}
			
			
			«FOR p:m.processes»
			«FOR s:p.states»
			public static CCSPState _S_«p.name»_«s.name» = new CCSPState( "«p.name»" , "«s.name»" , «stateId») {

				@Override
				protected void initializeTransistionTable() {
					«FOR t:s.actions»
					addNext( 
						«t.act.code» , 
						«t.next.getReferenceCode(p.name)»
					);
					«ENDFOR»
				}
				
			};
			«ENDFOR»
			«ENDFOR»	

			«FOR s:m.systems»
			public static CCSPProcess _SYS_«s.name» = «s.body.processCode»;
			«ENDFOR»
			
			@Override
			protected void doInitBehaviour() { 
				«FOR p:m.processes»
				addProcess("«p.name»");
				«FOR s:p.states»
				addState(_S_«p.name»_«s.name»);
				«ENDFOR»
				«ENDFOR»				
				«FOR s:m.systems»
				addSystem( "«s.name»" , _SYS_«s.name» );
				«ENDFOR»
			} 
			
			«FOR f:m.formulae»
			«f.formulaDeclaration»
			«ENDFOR»
			
			@Override
			protected void doInitFormulae() { 
			«FOR f:m.formulae.filter(typeof(HmlFormulaDeclaration))»
				addHmlFormula( "«f.name»" , _HMLFORMULA_«f.name» );
			«ENDFOR»								
			«FOR f:m.formulae.filter(typeof(ActlFormulaDeclaration))»
				addActlFormula( "«f.name»" , _ACTLFORMULA_«f.name» );
			«ENDFOR»								
			}
			
		}
		'''
	}	
	
	def dispatch getFormulaDeclaration( HmlFormulaDeclaration formula ) {
		'''
		public static HmlFormula<CCSPProcess,CCSPAction> _HMLFORMULA_«formula.name» = generate_HMLFORMULA_«formula.name»();
		
		public static HmlFormula<CCSPProcess,CCSPAction> generate_HMLFORMULA_«formula.name»() {
				«formula.body.generateDeclarationsOfRecursiveFormulae»				
				«formula.body.generatePopulationOfRecursiveFormulae»
				return «formula.body.generateHmlFormulaeCode»;
		}
		
		'''		
	}
	
	def CharSequence generateDeclarationsOfRecursiveFormulae( HmlFormula f ) {
		switch f {
			HmlOrFormula: {
				'''
				«f.left.generateDeclarationsOfRecursiveFormulae»
				«f.right.generateDeclarationsOfRecursiveFormulae»
				'''
			}
			HmlAndFormula: {
				'''
				«f.left.generateDeclarationsOfRecursiveFormulae»
				«f.right.generateDeclarationsOfRecursiveFormulae»
				'''
			}
			HmlNotFormula: {
				f.arg.generateDeclarationsOfRecursiveFormulae
			}
			HmlDiamondFormula:
				f.arg.generateDeclarationsOfRecursiveFormulae
			HmlBoxFormula:
				f.arg.generateDeclarationsOfRecursiveFormulae
			HmlMinFixPoint:
				'''
				HmlFixPoint<CCSPProcess, CCSPAction> _FIX_«f.name» = new HmlFixPoint<CCSPProcess, CCSPAction>(false, "«f.name»");
				'''
			HmlMaxFixPoint:
				'''
				HmlFixPoint<CCSPProcess, CCSPAction> _FIX_«f.name» = new HmlFixPoint<CCSPProcess, CCSPAction>(true, "«f.name»");
				'''
			HmlTrue: ''''''
			HmlFalse: ''''''
			default:
				''''''
		}
	}
	
	def CharSequence generatePopulationOfRecursiveFormulae( HmlFormula f ) {
		switch f {
			HmlOrFormula: {
				'''
				«f.left.generatePopulationOfRecursiveFormulae»
				«f.right.generatePopulationOfRecursiveFormulae»
				'''
			}
			HmlAndFormula: {
				'''
				«f.left.generatePopulationOfRecursiveFormulae»
				«f.right.generatePopulationOfRecursiveFormulae»
				'''
			}
			HmlNotFormula: {
				f.arg.generatePopulationOfRecursiveFormulae
			}
			HmlDiamondFormula:
				f.arg.generatePopulationOfRecursiveFormulae
			HmlBoxFormula:
				f.arg.generatePopulationOfRecursiveFormulae
			HmlMinFixPoint:
				'''
				_FIX_«f.name».setSubformula( «f.arg.generateHmlFormulaeCode»); 
				«f.arg.generatePopulationOfRecursiveFormulae»
				'''
			HmlMaxFixPoint:
				'''
				_FIX_«f.name».setSubformula( «f.arg.generateHmlFormulaeCode»); 
				«f.arg.generatePopulationOfRecursiveFormulae»
				'''
			HmlTrue: ''''''
			HmlFalse: ''''''
			default:
				''''''
		}
	}

	def CharSequence generateHmlFormulaeCode( HmlFormula f ) {
		switch f {
			HmlOrFormula: {
				'''
				new HmlOrFormula<CCSPProcess, CCSPAction>( 
					«f.left.generateHmlFormulaeCode» , 
					«f.right.generateHmlFormulaeCode»  
				)
				'''
			}
			HmlAndFormula: {
				'''
				new HmlAndFormula<CCSPProcess, CCSPAction>( 
					«f.left.generateHmlFormulaeCode» , 
					«f.right.generateHmlFormulaeCode»  
				)
				'''
			}
			HmlNotFormula: {
				'''
				new HmlNotFormula<CCSPProcess, CCSPActions>(
					«f.arg.generateHmlFormulaeCode»
				)
				'''
			}
			HmlDiamondFormula:
				'''
				new HmlDiamondFormula<CCSPProcess,CCSPAction>( 
						«f.action.generateFilterCode» , 
						«f.arg.generateHmlFormulaeCode»
				)
				'''
			HmlBoxFormula:
				'''
				new HmlBoxFormula<CCSPProcess,CCSPAction>( 
						«f.action.generateFilterCode» , 
						«f.arg.generateHmlFormulaeCode»
				)
				'''
			HmlMinFixPoint:
				'''
				_FIX_«f.name»
				'''
			HmlMaxFixPoint:
				'''
				_FIX_«f.name»
				'''
			HmlRecursionVariable:
				'''
				_FIX_«f.refeerence.name».getReference()
				'''
			HmlTrue: '''new HmlTrue<CCSPProcess,CCSPAction>()'''
			HmlFalse: '''new HmlFalse<CCSPProcess,CCSPAction>()'''
			default:
				'''new HmlFalse<CCSPProcess,CCSPAction>()'''
		}
	}

	def dispatch getFormulaDeclaration( ActlFormulaDeclaration formula ) {
		'''public static HmlFormula<CCSPProcess,CCSPAction> _ACTLFORMULA_«formula.name» = «formula.body.generateActlFormula»;'''
	}
	
	
	def dispatch CharSequence generateActlFormula( OrFormula f ) {
		'''new HmlOrFormula<CCSPProcess, CCSPAction>( 
				«f.left.generateActlFormula» , 
				«f.right.generateActlFormula»  
			)
		'''	
	}

	def dispatch CharSequence generateActlFormula( AndFormula f ) {
		'''new HmlAndFormula<CCSPProcess, CCSPAction>( 
				«f.left.generateActlFormula» , 
				«f.right.generateActlFormula»  
			)
		'''	
	}

	def dispatch generateActlFormula( TrueFormula f ) {
		'''new HmlTrue<CCSPProcess,CCSPAction>()'''
	}
	
	def dispatch generateActlFormula( FalseFormula f ) {
		'''new HmlFalse<CCSPProcess,CCSPAction>()'''
	}
	
	def dispatch generateActlFormula( ExistsFormula f ) {
		f.path.generateActlPathFormula( false )		
	}
	
	def dispatch generateActlFormula( ForAllFormula f ) {
		f.path.generateActlPathFormula( true )		
	}
	
	
	def dispatch CharSequence generateActlPathFormula( NextFormula f , boolean isForAll ) {
		if (isForAll) {
			'''
			new HmlAndFormula<CCSPProcess,CCSPAction>(
				new HmlDiamondFormula<CCSPProcess,CCSPAction>( «f.label.generateFilterCode» , new HmlTrue<CCSPProcess,CCSPAction>() ) ,
				new HmlBoxFormula<CCSPProcess,CCSPAction>( «f.label.generateFilterCode» , «f.arg.generateActlFormula» ) ,
			)
			'''			
		} else {
			'''
			new HmlDiamondFormula<CCSPProcess,CCSPAction>( «f.label.generateFilterCode» , «f.arg.generateActlFormula» )
			'''			
		}
	}
	
	def dispatch CharSequence generateActlPathFormula( UntilFormula f , boolean isForAll ) {
		if (isForAll) {
			'''
			HmlAndFormula.forAllUntil(
				«f.left.generateActlFormula» ,
				«f.label1.generateFilterCode» , «IF f.label2!=null»
				«f.label2.generateFilterCode» ,				
				«ENDIF» «f.right.generateActlFormula»
			)
			'''			
		} else {
			'''
			HmlAndFormula.existsUntil(
				«f.left.generateActlFormula» ,
				«f.label1.generateFilterCode» , «IF f.label2!=null»
				«f.label2.generateFilterCode» ,				
				«ENDIF» «f.right.generateActlFormula»
			)
			'''			
		}
	}
	
	def dispatch CharSequence generateActlPathFormula( AlwaysFormula f , boolean isForAll ) {
		if (isForAll) {
			'''
			HmlAndFormula.forAllGlobally(
				«IF f.label!=null»«f.label.generateFilterCode»«ELSE»new TrueFilter<CCSPAction>()«ENDIF» , 
				«f.arg.generateActlFormula»
			)
			'''			
		} else {
			'''
			HmlAndFormula.existsGlobally(
				«IF f.label!=null»«f.label.generateFilterCode»«ELSE»new TrueFilter<CCSPAction>()«ENDIF» , 
				«f.arg.generateActlFormula»
			)
			'''			
		}	
	}
	
	def dispatch CharSequence generateActlPathFormula( EventuallyFormula f , boolean isForAll ) {
		if (isForAll) {
			'''
			HmlAndFormula.forAllEventually(
				«IF f.label!=null»«f.label.generateFilterCode»«ELSE»new TrueFilter<CCSPAction>()«ENDIF» , 
				«f.arg.generateActlFormula»
			)
			'''			
		} else {
			'''
			HmlAndFormula.existsEventually(
				«IF f.label!=null»«f.label.generateFilterCode»«ELSE»new TrueFilter<CCSPAction>()«ENDIF» , 
				«f.arg.generateActlFormula»
			)
			'''			
		}	
	}
	
	
	
	def dispatch getCode( TauAction a ) {
		'''CCSPAction.TAU'''
	}
	
	def dispatch getCode( InputAction a ) {
		'''new CCSPAction.InputAction(_CH_«a.channel.name»)'''
	}
	
	def dispatch getCode( OutputAction a ) {
		'''new CCSPAction.OutputAction(_CH_«a.channel.name»)'''
	}
	
	def dispatch getReferenceCode( NilProcess p , String context ) {
		'''CCSPProcess.NIL'''
	}

	def dispatch getReferenceCode( StateReference p , String context ) {
		'''_S_«context»_«p.state.name»'''
	}

	def dispatch getReferenceCode( ProcessReference p , String context ) {
		'''_S_«p.process.name»_«p.state.name»'''
	}
	
	def dispatch CharSequence getProcessCode( SystemBody p ) {
		'''CCSPProcess.NIL'''
	}
	
	def dispatch CharSequence getProcessCode( Renaming p ) {
		'''
		new CCSPRenaming( 
			«p.arg.processCode» ,
			new CCSPChannel[] { «FOR m:p.maps SEPARATOR ','» _CH_«m.src.name» «ENDFOR»} ,
			new CCSPChannel[] { «FOR m:p.maps SEPARATOR ','» _CH_«m.trg.name» «ENDFOR»} 
		)
		'''
	}
	def dispatch CharSequence getProcessCode( NilProcess p ) {
		'''CCSPProcess.NIL'''
	}

	def dispatch CharSequence getProcessCode( ProcessReference p ) {
		'''_S_«p.process.name»_«p.state.name»'''
	}

	def dispatch CharSequence getProcessCode( SystemReference p ) {
		'''_SYS_«p.state.name»'''
	}

	def dispatch CharSequence getProcessCode( ParallelComposition p ) {
		'''
		new CCSPParallel(
			«p.left.processCode» ,
			«p.right.processCode»
		)
		'''
	}
	
	def dispatch CharSequence getProcessCode( CooperationComposition p ) {
		'''
		new CCSPCooperation(
			«p.left.processCode» ,
			«p.channels.actionFilter» ,
			«p.right.processCode»
		)
		'''
	}
	
	def dispatch CharSequence getProcessCode( InterleavingComposition p ) {
		'''
		new CCSPCooperation(
			«p.left.processCode» ,
			new CCSPChannelSet() ,
			«p.right.processCode»
		)
		'''
	}
	
	def dispatch CharSequence getProcessCode( SynchronizationComposition p ) {
		'''
		new CCSPCooperation(
			«p.left.processCode» ,
			new TrueFilter<CCSPAction>( ) ,
			«p.right.processCode»
		)
		'''
	}
	
	def dispatch CharSequence getProcessCode( Restriction p ) {
		'''
		new CCSPRestriction( 
			«p.arg.processCode» ,
			«p.channels.actionFilter»
		)
		'''
	}

	def getActionFilter( EList<Channel> channels ) {
		'''
		new CCSPChannelSet(«FOR c:channels SEPARATOR ','»_CH_«c.name»«ENDFOR»)
		'''
	}

	def getStateId() {
		stateCounter = stateCounter+1;
		stateCounter
	}
	
	def getChannelId() {
		channelCounter = channelCounter+1
		channelCounter
	}
	
	def CharSequence generateFilterCode( LabelPredicate pred ) {
		switch pred {
			CupLabelPredicate: 
				'''new OrFilter<CCSPAction>( «pred.left.generateFilterCode» , «pred.righ.generateFilterCode»)'''	
			CapLabelPredicate:
				'''new AndFilter<CCSPAction>( «pred.left.generateFilterCode» , «pred.right.generateFilterCode»)'''	
			NotLabelPredicate:
				'''new NotFilter<CCSPAction>( «pred.arg.generateFilterCode» )'''	
			AnyLabelPredicate:
				'''new TrueFilter<CCSPAction>() '''
			ActionLabelPredicate:
				'''new SingleActionFilter<CCSPAction>( «pred.act.code» )'''
			default:
				'''new FalseFilter<CCSPAction>( )'''
		}
	}
	

}
