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

public class ClusterQueriesComposite extends BaseCompositePage {

	private Browser browser_queries;
	
	public ClusterQueriesComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_CLUSTER_QUERIES_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_CLUSTER_QUERIES_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		
		// 1.0 Operation Area
		Composite operComp = new Composite(composte, SWT.NONE);
		operComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operComp.setLayout(new FillLayout(SWT.VERTICAL));
		
		new Label(operComp, SWT.NONE).setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_CLUSTER_QUERIES_DESC_RUNNING));
		new Label(operComp, SWT.NONE).setText(" ☀ " + ResourceHandler.getValue(MessageConstants.MONITOR_CLUSTER_QUERIES_DESC_QUEUED));
		
		// 2.0 Data Area
		Composite dataComp = new Composite(composte, SWT.NONE);
		dataComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		dataComp.setLayout(new FillLayout());
		
		browser_queries = new Browser(dataComp, SWT.BORDER);
		browser_queries.setMenu(new Menu(browser_queries));
	}
	
	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		System.out.println("ClusterQueriesComposite loadData");
		browser_queries.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_queries.jsp?monitorName=" + getContainer().getMonitorName());
	}
	
	@Override
	public void loadStaticData() {
		// TODO Auto-generated method stub
	}
}
