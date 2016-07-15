package org.cmg.tapas.ccsp.extensions

import org.cmg.tapas.views.TAPAsGraphElementLabelProvider
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessReference
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateReference
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateDeclaration
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.NilProcess
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Transition
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Action
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.InputAction
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.OutputAction
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.TauAction
//import org.eclipse.gef4.zest.core.widgets.ZestStyles
import org.eclipse.draw2d.ColorConstants
import org.eclipse.zest.core.widgets.ZestStyles

class CCSPGraphElementLabelProvider extends TAPAsGraphElementLabelProvider {
	
	override getColor(Object rel) {
		ColorConstants::black
	}
	
//	override getConnectionDecorator(Object rel) {
//		null
//	}
	
	override getConnectionStyle(Object rel) {
		ZestStyles::CONNECTIONS_DIRECTED
	}
	
	override getHighlightColor(Object rel) {
		ColorConstants::yellow
	}
	
	override getLineWidth(Object rel) {
		1
	}
	
//	override getRouter(Object rel) {
//		null
//	}
	
	override getTooltip(Object rel) {
		null
	}
	
	override fisheyeNode(Object entity) {
		false
	}
	
	override getBackgroundColour(Object entity) {
		null
	}
	
	override getBorderColor(Object entity) {
		null
	}
	
	override getBorderHighlightColor(Object entity) {
		null
	}
	
	override getBorderWidth(Object entity) {
		1
	}
	
	override getForegroundColour(Object entity) {
		null
	}
	
	override getNodeHighlightColor(Object entity) {
		null
	}
	
	override getText(Object element) {
		switch element {
			ProcessReference: element.process.name+"["+element.state.name+"]"			
			StateReference: element.state.name
			StateDeclaration: element.name
			NilProcess: "nil"
			Transition: element.act.stringOfAct
		}
	}
	
		
	def stringOfAct( Action a ) {
		switch a {
			InputAction: "?"+a.channel.name
			OutputAction: "!"+a.channel.name
			TauAction: "tau"
		}
	}
	
}