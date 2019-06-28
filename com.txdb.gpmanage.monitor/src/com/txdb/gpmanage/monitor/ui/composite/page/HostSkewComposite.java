package com.txdb.gpmanage.monitor.ui.composite.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class HostSkewComposite extends BaseCompositePage {

	private Browser browser_skew;
	
	public HostSkewComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_HOST_SKEW_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_HOST_SKEW_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		
		// 1.0 Operation Area
		Composite operComp = new Composite(composte, SWT.NONE);
		operComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operComp.setLayout(new FillLayout(SWT.VERTICAL));
		
		new Label(operComp, SWT.NONE).setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_HOST_SKEW_DESC_LOW));
		new Label(operComp, SWT.NONE).setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_HOST_SKEW_DESC_MODERATE));
		new Label(operComp, SWT.NONE).setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_HOST_SKEW_DESC_HIGH));
		new Label(operComp, SWT.NONE).setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_HOST_SKEW_DESC_HIGHEST));
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HOST_SKEW_DESC));
		
		// 2.0 Data Area
		Composite dataComp = new Composite(composte, SWT.NONE);
		dataComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		dataComp.setLayout(new FillLayout());
		
		browser_skew = new Browser(dataComp, SWT.BORDER);
		browser_skew.setMenu(new Menu(browser_skew));
	}
	
	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		System.out.println("HostSkewComposite loadData");
		browser_skew.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/hostmetrics/host_usage_skew.jsp?monitorName=" + getContainer().getMonitorName());
	}
}
