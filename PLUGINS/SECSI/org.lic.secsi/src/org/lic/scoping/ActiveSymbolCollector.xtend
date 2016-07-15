package org.lic.scoping

import org.lic.secsi.Statement
import org.lic.secsi.Block
import java.util.LinkedList
import org.lic.secsi.ReferenceableElement
import org.eclipse.xtext.scoping.Scopes
import org.lic.secsi.VarDeclaration
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.scoping.IScope
import org.lic.secsi.IfThenElse
import org.lic.secsi.While
import org.lic.secsi.Switch_Case
import org.lic.secsi.Agent
import org.lic.secsi.Program

class ActiveSymbolCollector {
	
	def IScope getActiveSymbol( Statement s ) {
		s.eContainer.getActiveSymbol( s );
	}

	def IScope getActiveSymbol( Agent p ) {
		var program = p.eContainer as Program
		var list = new LinkedList<ReferenceableElement>()
		for (t:program.types) {
			list.addAll(t.elements)
		}
		list.addAll(program.consts)
		Scopes::scopeFor(list)
	}
	
	def dispatch IScope getActiveSymbol( EObject container , EObject element ) {
		IScope::NULLSCOPE
	}
	
	def dispatch IScope getActiveSymbol( Block b , Statement s ) {
		var list = new LinkedList<ReferenceableElement>();
		for (c:b.cmds) {
			if (c == s) {
				return Scopes::scopeFor(list, b.activeSymbol );
			} else {
				switch c {
					VarDeclaration: {
						list.add(c.variable);
					}
				}
			}
		}		
	}

	def dispatch IScope getActiveSymbol( IfThenElse c , Statement s ) {
		c.activeSymbol
	}

	def dispatch IScope getActiveSymbol( While c , Statement s ) {
		c.activeSymbol
	}

	def dispatch IScope getActiveSymbol( Switch_Case c , Statement s ) {
		c.activeSymbol
	}
	
	def dispatch IScope getActiveSymbol( Agent p , Statement s)	{
		Scopes::scopeFor(p.params , p.activeSymbol );
	}
}