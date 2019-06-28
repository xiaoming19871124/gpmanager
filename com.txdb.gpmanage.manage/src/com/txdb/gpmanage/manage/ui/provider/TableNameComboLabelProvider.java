package com.txdb.gpmanage.manage.ui.provider;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import com.txdb.gpmanage.core.gp.entry.GPExpandStatusDetail;

public class TableNameComboLabelProvider extends BaseLabelProvider implements ILabelProvider {

	/**
	 * Creates a new label provider.
	 */
	public TableNameComboLabelProvider() {
	}

	/**
	 * The <code>LabelProvider</code> implementation of this
	 * <code>ILabelProvider</code> method returns <code>null</code>. Subclasses
	 * may override.
	 */
	@Override
	public Image getImage(Object element) {
		return null;
	}

	/**
	 * The <code>LabelProvider</code> implementation of this
	 * <code>ILabelProvider</code> method returns the element's
	 * <code>toString</code> string. Subclasses may override.
	 */
	@Override
	public String getText(Object element) {
		return element == null ? "" : ((GPExpandStatusDetail)element).getFq_name();//$NON-NLS-1$
	}
}
