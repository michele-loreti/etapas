/*
 * generated by Xtext
 */
package org.cmg.tapas.rm.xtext.scoping

import org.cmg.tapas.rm.xtext.module.Model
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.scoping.Scopes
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation.html#scoping
 * on how and when to use it 
 *
 */
class ModuleScopeProvider extends AbstractDeclarativeScopeProvider {


	def scope_Variable( Model m , EReference reference ) {		
		Scopes::scopeFor( m.collectVariables )
	}

	def scope_Literal( Model m , EReference reference ) {		
		Scopes::scopeFor( m.collectVariables + m.constants + m.functions )
	}
	
	def scope_RenameableElement( Model m , EReference reference ) {		
		Scopes::scopeFor( 
			m.collectVariables + m.constants + m.actions
		)
	}
	
	def collectVariables( Model m ) {
		m.globalVariables.map[ v | v.variable ] + m.modules.map[ module | module.variables ].flatten
	}

}
