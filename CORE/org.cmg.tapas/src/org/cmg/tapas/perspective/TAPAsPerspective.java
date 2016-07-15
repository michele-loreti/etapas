package org.cmg.tapas.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

public class TAPAsPerspective implements IPerspectiveFactory {

	/**
	 * Creates the initial layout for a page.
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		addFastViews(layout);
		addViewShortcuts(layout);
		addPerspectiveShortcuts(layout);
		{
			IFolderLayout folderLayout = layout.createFolder("folder_1", IPageLayout.LEFT, 0.31f, IPageLayout.ID_EDITOR_AREA);
			folderLayout.addView("org.eclipse.jdt.ui.ProjectsView");
			folderLayout.addView("org.eclipse.ui.navigator.ProjectExplorer");
		}
		layout.addView("org.cmg.tapas.views.TAPAsElementView", IPageLayout.BOTTOM, 0.5f, "org.eclipse.jdt.ui.ProjectsView");
		{
			IFolderLayout folderLayout = layout.createFolder("folder", IPageLayout.BOTTOM, 0.58f, IPageLayout.ID_EDITOR_AREA);
			folderLayout.addView("org.cmg.tapas.views.TAPAsModelCheckingView");
			folderLayout.addView("org.cmg.tapas.views.TAPAsEquivalenceCheckerView");
			folderLayout.addView("org.cmg.tapas.views.TAPAsLTSView");
		}
	}

	/**
	 * Add fast views to the perspective.
	 */
	private void addFastViews(IPageLayout layout) {
	}

	/**
	 * Add view shortcuts to the perspective.
	 */
	private void addViewShortcuts(IPageLayout layout) {
	}

	/**
	 * Add perspective shortcuts to the perspective.
	 */
	private void addPerspectiveShortcuts(IPageLayout layout) {
	}

}
