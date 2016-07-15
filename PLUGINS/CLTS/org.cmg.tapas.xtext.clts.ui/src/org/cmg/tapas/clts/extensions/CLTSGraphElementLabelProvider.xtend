package org.cmg.tapas.clts.extensions

import org.cmg.tapas.views.TAPAsGraphElementLabelProvider
import org.cmg.tapas.xtext.clts.composedLts.Rule
import org.eclipse.draw2d.ColorConstants
import org.eclipse.zest.core.widgets.ZestStyles
import org.cmg.tapas.xtext.clts.composedLts.LtsState
import org.cmg.tapas.xtext.clts.composedLts.LtsRule

class CLTSGraphElementLabelProvider extends TAPAsGraphElementLabelProvider {
	
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
			LtsState: element.name
			LtsRule: element.act.name
			}
	}
}
