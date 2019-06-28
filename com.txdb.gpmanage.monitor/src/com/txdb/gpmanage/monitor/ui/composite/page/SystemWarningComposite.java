package com.txdb.gpmanage.monitor.ui.composite.page;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class SystemWarningComposite extends BaseCompositePage {
	
	public SystemWarningComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, "system warning", "configure warning message");
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(2, true));
		
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
	}
}
