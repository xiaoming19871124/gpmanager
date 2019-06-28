package com.txdb.gpmanage.monitor.ui.composite.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class ClusterSystemComposite extends BaseCompositePage {

	private Browser browser_system;
//	private Browser browser_cpu;
//	private Browser browser_memory;
//	private Browser browser_diskio;
//	private Browser browser_network;
//	private Browser browser_load;
//	private Browser browser_swap;
	
	public ClusterSystemComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_CLUSTER_SYSTEM_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_CLUSTER_SYSTEM_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new FillLayout());
		
		// System
		browser_system = new Browser(composte, SWT.BORDER);
		browser_system.setMenu(new Menu(browser_system));
		
//		ScrolledComposite comp_system_scrolled = new ScrolledComposite(composte, SWT.H_SCROLL | SWT.V_SCROLL);
//		comp_system_scrolled.setLayout(new GridLayout(1, true));
//		
//		Composite comp_system = new Composite(comp_system_scrolled, SWT.NONE);
//		comp_system.setSize(-1, 2000);
//		comp_system_scrolled.setContent(comp_system);
//		comp_system_scrolled.setExpandHorizontal(true);
//		comp_system.setLayout(new GridLayout(1, true));
		
//		// CPU
//		Group group_cpu = new Group(comp_system, SWT.NONE);
//		group_cpu.setText("CPU");
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_cpu.setLayoutData(gd);
//		group_cpu.setLayout(new FillLayout());
//		browser_cpu = new Browser(group_cpu, SWT.BORDER);
//		browser_cpu.setMenu(new Menu(browser_cpu));
//		
//		// Memory
//		Group group_memory = new Group(comp_system, SWT.NONE);
//		group_memory.setText("Memory");
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_memory.setLayoutData(gd);
//		group_memory.setLayout(new FillLayout());
//		browser_memory = new Browser(group_memory, SWT.BORDER);
//		browser_memory.setMenu(new Menu(browser_memory));
//		
//		// Disk I/O
//		Group group_diskio = new Group(comp_system, SWT.NONE);
//		group_diskio.setText("Disk I/O");
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_diskio.setLayoutData(gd);
//		group_diskio.setLayout(new FillLayout());
//		browser_diskio = new Browser(group_diskio, SWT.BORDER);
//		browser_diskio.setMenu(new Menu(browser_diskio));
//		
//		// Network
//		Group group_network = new Group(comp_system, SWT.NONE);
//		group_network.setText("Network");
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_network.setLayoutData(gd);
//		group_network.setLayout(new FillLayout());
//		browser_network = new Browser(group_network, SWT.BORDER);
//		browser_network.setMenu(new Menu(browser_network));
//		
//		// Load
//		Group group_load = new Group(comp_system, SWT.NONE);
//		group_load.setText("Load");
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_load.setLayoutData(gd);
//		group_load.setLayout(new FillLayout());
//		browser_load = new Browser(group_load, SWT.BORDER);
//		browser_load.setMenu(new Menu(browser_load));
//		
//		// Swap
//		Group group_swap = new Group(comp_system, SWT.NONE);
//		group_swap.setText("Swap");
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.heightHint = 300;
//		group_swap.setLayoutData(gd);
//		group_swap.setLayout(new FillLayout());
//		browser_swap = new Browser(group_swap, SWT.BORDER);
//		browser_swap.setMenu(new Menu(browser_swap));
	}
	
	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		System.out.println("ClusterSystemComposite loadData");
		
		String monitorName = getContainer().getMonitorName();
		browser_system.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_system.jsp?monitorName=" + monitorName);
//		browser_cpu.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_cpu.jsp?monitorName=" + monitorName);
//		browser_memory.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_memory.jsp?monitorName=" + monitorName);
//		browser_diskio.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_diskio.jsp?monitorName=" + monitorName);
//		browser_network.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_network.jsp?monitorName=" + monitorName);
//		browser_load.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_load.jsp?monitorName=" + monitorName);
//		browser_swap.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/clustermetrics/cluster_usage_swap.jsp?monitorName=" + monitorName);
	}
}
