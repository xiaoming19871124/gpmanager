package com.txdb.gpmanage.manage.ui.provider;

import com.txdb.gpmanage.manage.entity.Level;
import com.txdb.gpmanage.manage.entity.Policy;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class LevelLabelProvider extends ITXTableLabelProvider {

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Level p = (Level) element;
		switch (columnIndex) {
		case 0:
			return p.getName();
		case 1:
			return String.valueOf(p.getId());
		default:
			break;
		}
		return null;
	}
}
