package com.txdb.gpmanage.manage.ui.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.txdb.gpmanage.manage.entity.DatabaseEntity;
import com.txdb.gpmanage.manage.entity.GPData;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class QueryTableLabelProvider  extends ITXTableLabelProvider {
	@Override
	public String getColumnText(Object element, int columnIndex) {
		DatabaseEntity database = (DatabaseEntity) element;
		switch (columnIndex) {
		case 0:
			return database.getName();
		case 1:
			String schame = "";
			if (database.getSchame().size() < 1) {
				schame = GPData.ALl_NAME;
			} else {

				for (GPData data : database.getSchame()) {
					schame = schame + data.getName() + ",";
				}
				int index = schame.lastIndexOf(",");
				schame = schame.substring(0, index);
			}
			return schame;
		case 2:
			return database.getTableName();
		default:
			break;
		}
		return null;
	}
}
