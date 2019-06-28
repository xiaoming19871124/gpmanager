package com.txdb.gpmanage.manage.ui.dialog;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.txdb.gpmanage.core.gp.entry.GPRequiredSw;
import com.txdb.gpmanage.manage.i18n.ResourceHandler;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class SoftTableLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		GPRequiredSw sw = (GPRequiredSw) element;
		switch (columnIndex) {
		case 0:
			return sw.getSoftwareName();
		case 1:
			return sw.getRequiredVer();
		case 2:
			return sw.getCurrentVer();
		case 3:
			switch (sw.getStatus()) {
			case GPRequiredSw.STATUS_UNINSTALL:
				// return ResourceHandler.getValue("soft.check.result");
				return ResourceHandler.getValue("soft.result.uninstall");
			case GPRequiredSw.STATUS_UNKNOW:
				return ResourceHandler.getValue("soft.result.unknow");
			case GPRequiredSw.STATUS_UPGRADE:
				return  ResourceHandler.getValue("soft.result.upgrade");
			case GPRequiredSw.STATUS_SATISFIED:
				return ResourceHandler.getValue("soft.result.ok");

			}
			return ResourceHandler.getValue("unknown");
		default:
			break;
		}
		return null;
	}
}
