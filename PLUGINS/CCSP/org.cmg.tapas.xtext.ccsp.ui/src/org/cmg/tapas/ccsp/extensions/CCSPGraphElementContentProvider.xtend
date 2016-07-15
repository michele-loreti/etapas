package org.cmg.tapas.ccsp.extensions

import java.util.LinkedList
import org.cmg.tapas.views.TAPAsGraphElementContentProvider
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.NextProcess
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.NilProcess
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessDeclaration
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessReference
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateDeclaration
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateReference
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Transition
import org.eclipse.jface.viewers.Viewer

class CCSPGraphElementContentProvider implements TAPAsGraphElementContentProvider {
	
	ProcessDeclaration process
	
	override getElements(Object inputElement) {
		if (inputElement instanceof ProcessDeclaration) {
			process = inputElement as ProcessDeclaration
		}		
		(process?.collectProcessStates?.toSet?.toArray) ?: newArrayOfSize(0)
	}
	
	override dispose() {
		process = null
	}
	
	override inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}
	
	override getRelationships(Object source, Object dest) {
		switch source {
			StateDeclaration: {
				source.actions.filter[ a | a.leadsTo(dest) ].toSet.toArray
			}	
		}
	}
	
	def LinkedList<Object> collectProcessStates( ProcessDeclaration process ) {
		var stateList = new LinkedList<Object>()
		stateList.addAll(process.states)
		process.states.fold( stateList , [ l,s | 
			l.addAll(s.actions.map[ a | a.next ].filter[ x | !x.local ]) 
			l
		] )
	}
		
		
	def isLocal( NextProcess p ) {
		switch (p) {
			StateReference: true
			ProcessReference: p.process == process
			NilProcess: false
			default: false
		}
	}	
	
	
	def leadsTo( Transition t , Object o ) {
		var target = t.next
		switch target {
			StateReference: target.state == o			
			ProcessReference: (target == o) || (target.local && target.state==o) 
			NilProcess: target == o
		}
	}
	
	
	
}