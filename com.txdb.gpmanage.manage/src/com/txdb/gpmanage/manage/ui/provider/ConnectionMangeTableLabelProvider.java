package com.txdb.gpmanage.manage.ui.provider;

import com.txdb.gpmanage.core.gp.entry.PGHbaInfo;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class ConnectionMangeTableLabelProvider extends ITXTableLabelProvider {

	@Override
	public String getColumnText(Object element, int columnIndex) {
		PGHbaInfo host = (PGHbaInfo) element;
		switch (columnIndex) {
		case 0:
			return host.getType();
		case 1:
			return host.getDatabaseArrayString();
		case 2:
			return host.getUserArrayString();
		case 3:
			return host.getAddress();
		case 4:
			return host.getMethod();
		default:
			break;
		}
		return null;
	}
}
