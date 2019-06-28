package com.txdb.gpmanage.manage.ui.provider;

import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.manage.entity.Policy;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class PolicyTableLabelProvider extends ITXTableLabelProvider {

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Policy p = (Policy) element;
		switch (columnIndex) {
		case 0:
			return p.getName();
		case 1:
			return p.getColumn();
		case 2:
			return String.valueOf(p.isHide());
		case 3:
			return String.valueOf(p.isEnable());
		default:
			break;
		}
		return null;
	}
}
