package com.txdb.gpmanage.manage.ui.provider;

import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class AddMirrorTableLabelProvider extends ITXTableLabelProvider {

	@Override
	public String getColumnText(Object element, int columnIndex) {
		GPSegmentInfo info = (GPSegmentInfo) element;
		switch (columnIndex) {
		case 0:
			return info.getHostname();
		case 1:
			return String.valueOf(info.getPort());
		case 2:
			return ResourceHandler.getValue(info.getRole());
		case 3:
			return ResourceHandler.getValue(info.getMode());
		case 4:
			return ResourceHandler.getValue(info.getStatus());
		case 5:
			return info.getDatadir();
		default:
			break;
		}
		return null;
	}
}
