/*
 * generated by Xtext
 */
package org.cmg.tapas.xtext.ccsp.validation

import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessDeclaration
import static extension org.eclipse.xtext.EcoreUtil2.*

import org.eclipse.xtext.validation.Check
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Model
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.CCSPSpecificationPackage
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateDeclaration
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.SystemDeclaration
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Channel

/**
 * Custom validation rules. 
 *
 * see http://www.eclipse.org/Xtext/documentation.html#validation
 */
class CCSPSpecificationValidator extends AbstractCCSPSpecificationValidator {
	
	public static final String DUPLICATED_PROCESS_DECLARATION = "DUPLICATED_PROCESS_DECLARATION"

	public static final String DUPLICATED_STATE_DECLARATION = "DUPLICATED_STATE_DECLARATION"
	
	public static final String DUPLICATED_CHANNEL_DECLARATION = "DUPLICATED_CHANNEL_DECLARATION"
	
	public static final String DUPLICATED_SYSTEM_DECLARATION = "DUPLICATED_SYSTEM_DECLARATION"
	
	@Check
	def checkSingleSystemName( SystemDeclaration declaration){
		var model = declaration.getContainerOfType(typeof(Model))
		var withName = model.systems.filter[ it.name == declaration.name]
		if (withName.size > 1) {
			error( 'Duplicated system declaration!', CCSPSpecificationPackage.Literals.SYSTEM_DECLARATION__NAME , DUPLICATED_SYSTEM_DECLARATION)
		}
	}
	
	@Check
	def checkSingleChannelName( Channel declaration ) {
		var model = declaration.getContainerOfType(typeof(Model))
		var withName = model.channels.filter[ it.name == declaration.name ]
		if (withName.size > 1) {
			error( 'Duplicated channel declaration!' , CCSPSpecificationPackage.Literals.CHANNEL__NAME , DUPLICATED_CHANNEL_DECLARATION )			
		}
	}
	
	@Check
	def checkSingleProcessName( ProcessDeclaration declaration ) {
		var model = declaration.getContainerOfType(typeof(Model))
		var withName = model.processes.filter[ it.name == declaration.name ]
		if (withName.size > 1) {
			error( 'Duplicated process declaration!' , CCSPSpecificationPackage.Literals.PROCESS_DECLARATION__NAME , DUPLICATED_PROCESS_DECLARATION )			
		}
	}

	@Check
	def checkSingleProcessName( StateDeclaration declaration ) {
		var process = declaration.getContainerOfType(typeof(ProcessDeclaration))
		var withName = process.states.filter[ it.name == declaration.name ]
		if (withName.size > 1) {
			error( 'Duplicated state declaration!' , CCSPSpecificationPackage.Literals.STATE_DECLARATION__NAME , DUPLICATED_STATE_DECLARATION )			
		}
	}
	

//  public static val INVALID_NAME = 'invalidName'
//
//	@Check
//	def checkGreetingStartsWithCapital(Greeting greeting) {
//		if (!Character.isUpperCase(greeting.name.charAt(0))) {
//			warning('Name should start with a capital', 
//					MyDslPackage.Literals.GREETING__NAME,
//					INVALID_NAME)
//		}
//	}

//	extends AbstractCCSPSpecificationJavaValidator {
//}
//
////	@Check
////	public void checkGreetingStartsWithCapital(Greeting greeting) {
////		if (!Character.isUpperCase(greeting.getName().charAt(0))) {
////			warning("Name should start with a capital", MyDslPackage.Literals.GREETING__NAME);
////		}
////	}
//	
//	@Check
//	public void checkProcessNames(Model model){
//		Set<String> names = new HashSet<String>();
//		int index = 0;
//		//Process names check
//		for(ProcessDeclaration p : model.getProcesses()){
//			if(names.contains(p.getName())){
//				error("Process name already exists", p.eContainingFeature(), index);
//				break;
//			}else{
//				index += 1;
//				names.add(p.getName());
//			}
//		}
//		
//		names.clear();
//		index = 0;
//		//Channel names check
//		for(Channel p : model.getChannels()){
//			if(names.contains(p.getName())){
//				error("Channel name already exists", p.eContainingFeature(), index);
//				break;
//			}else{
//				index += 1;
//				names.add(p.getName());
//			}
//		}
//		
//		names.clear();
//		index = 0;
//		//System names check
//		for(SystemDeclaration p : model.getSystems()){
//			if(names.contains(p.getName())){
//				error("System name already exists", p.eContainingFeature(), index);
//				break;
//			}else{
//				index += 1;
//				names.add(p.getName());
//			}
//		}
//	}

}
