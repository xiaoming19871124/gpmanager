package com.txdb.gpmanage.manage.ui.provider;

import com.txdb.gpmanage.manage.entity.ObjectAuth;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class AuthMangeTableLabelProvider extends ITXTableLabelProvider {

	@Override
	public String getColumnText(Object element, int columnIndex) {
		ObjectAuth auth = (ObjectAuth) element;
		switch (columnIndex) {
		case 0:
			return auth.getDbName();
		case 1:
			return auth.getSchemaName();
		case 2:
			return auth.getObject_type();
		case 3:
			return auth.getObjectName();
		case 4:
			return auth.getPrivilege_type();
		case 5:
			return auth.getIs_grantable();
		default:
			break;
			 }
			return null;
	}
}
