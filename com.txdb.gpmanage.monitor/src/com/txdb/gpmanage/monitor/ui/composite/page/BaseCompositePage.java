package com.txdb.gpmanage.monitor.ui.composite.page;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.txdb.gpmanage.application.composite.AbstractComposite;
import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;
import com.txdb.gpmanage.core.gp.dao.IExecuteDao;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.core.gp.entry.gpmon.Database;
import com.txdb.gpmanage.core.gp.entry.gpmon.Diskspace;
import com.txdb.gpmanage.core.gp.entry.gpmon.RequireConnection;
import com.txdb.gpmanage.monitor.ui.composite.MonitorContainer;

public abstract class BaseCompositePage extends IupperComposite {

	protected MonitorContainer mainContainer;
	
	protected final String WAITING_PAGE = "http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/waiting.jsp";
	
	public BaseCompositePage(AbstractComposite mainComposite, MonitorContainer mainContainer, CompositeCode code, String title, String desc) {
		super(mainComposite, mainContainer, code, title + " (" + mainContainer.getMonitorName() + ")", desc);
		this.mainContainer = mainContainer;
	}
	
	public MonitorContainer getContainer() {
		return mainContainer;
	}
	
	public RequireConnection getRequireConnection() {
		IGPConnector gpConnector = getContainer().getGpController();
		IExecuteDao dao = gpConnector.getDao();
		
		RequireConnection requireConnection = new RequireConnection();
		requireConnection.setMonitorName(getContainer().getMonitorName());
		requireConnection.setHost(dao.getHost());
		requireConnection.setSshUsername(dao.getSshUserName());
		requireConnection.setSshPassword(dao.getSshPassword());
		requireConnection.setSshPort(dao.getSshPort());
		requireConnection.setJdbcUsername(dao.getJdbcUsername());
		requireConnection.setJdbcPassword(dao.getJdbcPassword());
		requireConnection.setJdbcPort(dao.getJdbcPort());
		requireConnection.setDatabase(dao.getJdbcDatabase());
		
		return requireConnection;
	}

	@Override
	public void initComposite(String title, String desc) {
		this.setLayout(new GridLayout());
		createTitle(title, desc);
		
		Composite mainComposite = new Composite(this, SWT.BORDER);
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createCenter(mainComposite);
	}
	
	@Override
	public void createTitle(String title, String desc) {
		Composite titleComposite = new Composite(this, SWT.BORDER);
		titleComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		titleComposite.setLayout(new GridLayout(1, true));
		
		Label titleLbl = new Label(titleComposite, SWT.NONE);
		Font font = new Font(Display.getCurrent(), "Courier New", 10, SWT.BOLD);
		titleLbl.setFont(font);
		titleLbl.setText(title);
		
		Label descLbl = new Label(titleComposite, SWT.NONE);
		descLbl.setText(desc);
	}
	
	protected List<GPSegmentInfo> getCurrentSegmentInfoList() {
		return mainContainer.getSegmentInfoList();
	}
	
	protected List<com.txdb.gpmanage.core.gp.entry.gpmon.System> getCurrentSystemList() {
		return mainContainer.getSystemList();
	}
	
	protected List<Database> getCurrentDatabaseList() {
		return mainContainer.getDatabaseList();
	}
	
	protected List<Diskspace> getCurrentDiskspaceList() {
		return mainContainer.getDiskspaceList();
	}
	
	public abstract void loadData();
	public void loadStaticData() throws Exception {
		
	}
}
