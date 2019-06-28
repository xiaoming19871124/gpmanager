package com.txdb.gpmanage.manage.ui.provider;

import com.txdb.gpmanage.core.entity.Host;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class ExtendMangeTableLabelProvider extends ITXTableLabelProvider {

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Host host = (Host) element;
		switch (columnIndex) {
		case 0:
			return host.getName();
			case 1:
			return host.getIp();
		case 2:
			return host.getRole().getName();
		default:
			break;
		}
		return null;
	}
}
