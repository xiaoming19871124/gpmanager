package com.txdb.gpmanage.monitor.ui.composite.page;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.monitor.i18n.MessageConstants;
import com.txdb.gpmanage.monitor.i18n.ResourceHandler;
import com.txdb.gpmanage.monitor.ui.composite.MonitorComposite;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public class HostCpuUsageComposite extends BaseCompositePage {
	
	private Combo clusterSelection;
	private String clusterSelection_text;
	private Text txt_user;
	private Text txt_system;
	private Text txt_used;
	private Text txt_idle;
	
	private Browser browser_cpu;

	public HostCpuUsageComposite(MonitorComposite mainComposite, MonitorContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, 
				ResourceHandler.getValue(MessageConstants.MONITOR_HOST_CPU_TITLE), 
				ResourceHandler.getValue(MessageConstants.MONITOR_HOST_CPU_SUBTITLE));
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		
		// 1.0 Operation Area
		Composite operComp = new Composite(composte, SWT.NONE);
		operComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		operComp.setLayout(new GridLayout(10, false));
		
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HOST_CPU_LIST));
		clusterSelection = new Combo(operComp, SWT.READ_ONLY);
		GridData gd = new GridData();
		gd.widthHint = 150;
		clusterSelection.setLayoutData(gd);
		
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HOST_CPU_USER));
		txt_user = new Text(operComp, SWT.NO_TRIM);
		gd = new GridData();
		gd.widthHint = 50;
		txt_user.setLayoutData(gd);
		
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HOST_CPU_SYSTEM));
		txt_system = new Text(operComp, SWT.NO_TRIM);
		gd = new GridData();
		gd.widthHint = 50;
		txt_system.setLayoutData(gd);
		
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HOST_CPU_USED));
		txt_used = new Text(operComp, SWT.NO_TRIM);
		gd = new GridData();
		gd.widthHint = 50;
		txt_used.setLayoutData(gd);
		
		new Label(operComp, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.MONITOR_HOST_CPU_IDLE));
		txt_idle = new Text(operComp, SWT.NO_TRIM);
		gd = new GridData();
		gd.widthHint = 50;
		txt_idle.setLayoutData(gd);
		
		clusterSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String textStr = clusterSelection.getText();
				List<com.txdb.gpmanage.core.gp.entry.gpmon.System> systemList = getCurrentSystemList();
				for (com.txdb.gpmanage.core.gp.entry.gpmon.System system : systemList) {
					if (!textStr.equals(system.getHostname()))
						continue;
					
					txt_user.setText(system.getCpu_user() + "%");
					txt_system.setText(system.getCpu_sys() + "%");
					txt_used.setText(system.getCpu_used() + "%");
					txt_idle.setText(system.getCpu_idle() + "%");
					break;
				}
			}
		});
		
		// 2.0 Data Area
		Composite dataComp = new Composite(composte, SWT.NONE);
		dataComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		dataComp.setLayout(new FillLayout());
		
		browser_cpu = new Browser(dataComp, SWT.BORDER);
		browser_cpu.setMenu(new Menu(browser_cpu));
	}
	
	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		System.out.println("HostCpuUsageComposite loadData");
		browser_cpu.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/hostmetrics/host_usage_cpu.jsp?monitorName=" + getContainer().getMonitorName());
	}
	
	@Override
	public void loadStaticData() {
		// TODO Auto-generated method stub
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				clusterSelection_text = clusterSelection.getText();
				clusterSelection.removeAll();
			}
		});
		List<com.txdb.gpmanage.core.gp.entry.gpmon.System> systemList = getCurrentSystemList();
		for (final com.txdb.gpmanage.core.gp.entry.gpmon.System system : systemList) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					clusterSelection.add(system.getHostname());
				}
			});
		}
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				clusterSelection.select(0);
				if (!"".equals(clusterSelection_text))
					clusterSelection.setText(clusterSelection_text);
				clusterSelection.notifyListeners(SWT.Selection, null);
			}
		});
	}
}
