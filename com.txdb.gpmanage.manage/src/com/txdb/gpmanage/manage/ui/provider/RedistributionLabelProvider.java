package com.txdb.gpmanage.manage.ui.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.txdb.gpmanage.core.gp.entry.GPExpandStatusDetail;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class RedistributionLabelProvider  extends ITXTableLabelProvider {
	@Override
	public String getColumnText(Object element, int columnIndex) {
		GPExpandStatusDetail host = (GPExpandStatusDetail) element;
		switch (columnIndex) {
		case 0:
			return host.getDbname();
		case 1:
			return host.getSchema_name();
		case 2:
			return host.getFq_name();
		case 3:
			return String.valueOf(host.getRank());
		default:
			break;
		}
		return null;
	}
}
