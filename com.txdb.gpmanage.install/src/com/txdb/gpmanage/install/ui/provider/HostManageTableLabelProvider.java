package com.txdb.gpmanage.install.ui.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.txdb.gpmanage.core.entity.Host;

/**
 * 机器管理页table标签提供器
 * 
 * @author ws
 *
 */
public class HostManageTableLabelProvider implements ITableLabelProvider {

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
		Host host = (Host) element;
		switch (columnIndex) {
		case 0:
			return host.getName();
		case 1:
			return host.getIp();
		case 2:
			return host.getUserName();
		default:
			break;
		}
		return null;
	}
}
