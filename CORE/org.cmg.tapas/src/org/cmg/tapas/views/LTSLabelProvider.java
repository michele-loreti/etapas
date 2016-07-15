/**
 * 
 */
package org.cmg.tapas.views;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
//import org.eclipse.gef4.zest.core.viewers.EntityConnectionData;
//import org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider;
//import org.eclipse.gef4.zest.core.viewers.IEntityStyleProvider;
//import org.eclipse.gef4.zest.core.widgets.ZestStyles;
//import org.eclipse.gef4.zest.core.widgets.decoration.IConnectionDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.cmg.tapas.pa.Process;


/**
 * @author loreti
 *
 */
//TAPAsGraphElementLabelProvider extends LabelProvider implements IConnectionStyleProvider, IEntityStyleProvider
public class LTSLabelProvider    extends TAPAsGraphElementLabelProvider {

	
	  @Override
	  public String getText(Object element) {
	    if (element instanceof Process<?,?>) {
	      return " ";
	    }
	    if (element instanceof TAPAsGraphElementRelation) {
	      return ((TAPAsGraphElementRelation) element).getAction();
	    }
	    throw new RuntimeException("Wrong type: "
	        + element.getClass().toString());
	  }

		@Override
		public int getConnectionStyle(Object rel) {
			return ZestStyles.CONNECTIONS_DIRECTED;
		}
	
		@Override
		public Color getColor(Object rel) {
			
			return ColorConstants.black;
		}
	
		@Override
		public Color getHighlightColor(Object rel) {
			// TODO Auto-generated method stub
			return null;
		}
	
		@Override
		public int getLineWidth(Object rel) {
			// TODO Auto-generated method stub
			return 0;
		}
	
		@Override
		public IFigure getTooltip(Object entity) {
		    if (entity instanceof Process<?,?>) {
		    	return new Label( entity.toString() );
		    }
			// TODO Auto-generated method stub
			return null;
		}
	
//		@Override
//		public ConnectionRouter getRouter(Object rel) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//	
//		@Override
//		public IConnectionDecorator getConnectionDecorator(Object rel) {
//			// TODO Auto-generated method stub
//			return null;
//		}

		@Override
		public Color getNodeHighlightColor(Object entity) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Color getBorderColor(Object entity) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Color getBorderHighlightColor(Object entity) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getBorderWidth(Object entity) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Color getBackgroundColour(Object entity) {
			if (entity instanceof Process<?, ?>) {
				if (((Process<?, ?>) entity).isDeadlock() ) {
					return SWTResourceManager.getColor(SWT.COLOR_RED);
				}
			}
			return null;
		}

		@Override
		public Color getForegroundColour(Object entity) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean fisheyeNode(Object entity) {
			return false;
		}


}
