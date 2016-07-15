/**
 * 
 */
package org.cmg.tapas.ccsp.extensions;

import org.cmg.tapas.ccsp.runtime.CCSPAction;
import org.cmg.tapas.ccsp.runtime.CCSPProcess;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
//import org.eclipse.gef4.zest.core.viewers.EntityConnectionData;
//import org.eclipse.gef4.zest.core.viewers.IConnectionStyleProvider;
//import org.eclipse.gef4.zest.core.viewers.IEntityStyleProvider;
//import org.eclipse.gef4.zest.core.widgets.ZestStyles;
//import org.eclipse.gef4.zest.core.widgets.decoration.IConnectionDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;

/**
 * @author loreti
 *
 */
public class TAPAsLTSLabelProvider extends LabelProvider implements IConnectionStyleProvider, IEntityStyleProvider {

	
	  @Override
	  public String getText(Object element) {
	    if (element instanceof CCSPProcess) {
	      return ((CCSPProcess) element).toString();
	    }
	    if (element instanceof CCSPTransition) {
	      return ((CCSPTransition) element).getAction().toString();
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
			// TODO Auto-generated method stub
			return null;
		}
	
//		@Override
//		public ConnectionRouter getRouter(Object rel) {
//			// TODO Auto-generated method stub
//			return null;
//		}
	
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
			if (entity instanceof CCSPProcess) {
				if (((CCSPProcess) entity).isDeadlock() ) {
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
