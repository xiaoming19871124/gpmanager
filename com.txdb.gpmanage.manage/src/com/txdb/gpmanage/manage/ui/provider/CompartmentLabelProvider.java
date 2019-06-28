package com.txdb.gpmanage.manage.ui.provider;

import com.txdb.gpmanage.manage.entity.Compartment;
import com.txdb.gpmanage.manage.entity.Level;
import com.txdb.gpmanage.manage.entity.Policy;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class CompartmentLabelProvider extends ITXTableLabelProvider {

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Compartment p = (Compartment) element;
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
