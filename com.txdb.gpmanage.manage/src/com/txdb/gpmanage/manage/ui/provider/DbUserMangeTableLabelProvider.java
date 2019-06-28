package com.txdb.gpmanage.manage.ui.provider;

import com.txdb.gpmanage.core.entity.DbUser;
import com.txdb.gpmanage.core.gp.entry.GPConfig;
import com.txdb.gpmanage.core.gp.entry.GPConfigValue;
import com.txdb.gpmanage.manage.entity.GPConfigParam;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class DbUserMangeTableLabelProvider extends ITXTableLabelProvider {

	@Override
	public String getColumnText(Object element, int columnIndex) {
		DbUser user = (DbUser) element;
		switch (columnIndex) {
		case 0:
			return user.getUserName();
		case 1:
			String pwd = user.getUserPwd();
			if(pwd==null||pwd.isEmpty())
				return "";
			else
				return "******";
		case 2:
			return user.getUsecreatedb();
		case 3:
			return user.getUsesuper();
		case 4:
			return user.getUsecatupd();
//		case 5:
//			return user.getUserepl();
		case 5:
			return user.getValuntil();
		case 6:
			return user.getUseconfig();
		default:
			break;
		}
		return null;
	}
}
