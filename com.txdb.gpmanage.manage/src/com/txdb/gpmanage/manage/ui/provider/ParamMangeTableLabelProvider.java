package com.txdb.gpmanage.manage.ui.provider;

import com.txdb.gpmanage.core.gp.entry.GPConfig;
import com.txdb.gpmanage.core.gp.entry.GPConfigValue;
import com.txdb.gpmanage.manage.entity.GPConfigParam;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class ParamMangeTableLabelProvider extends ITXTableLabelProvider {

	@Override
	public String getColumnText(Object element, int columnIndex) {
		GPConfigParam param = (GPConfigParam) element;
		GPConfig gpConfig = param.getGpconfig();
		GPConfigValue gpValue = param.getValue();
		switch (columnIndex) {
		case 0:
			return gpConfig.getName();
		case 1:
			return param.getMasterValue();
		case 2:
			return param.getSegValue();
		case 3:
			return gpValue.getMasterValue();
		case 4:
			return gpValue.getSegmentValue();
		case 5:
			return gpConfig.getVartype();
		case 6:
			return gpConfig.getMax_val();
		case 7:
			return gpConfig.getMin_val();
		default:
			break;
		}
		return null;
	}
}
