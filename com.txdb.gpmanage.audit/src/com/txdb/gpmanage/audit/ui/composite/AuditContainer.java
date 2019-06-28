package com.txdb.gpmanage.audit.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.audit.ui.composite.page.BaseCompositePage;
import com.txdb.gpmanage.audit.ui.composite.page.AuditConditionComposite;
import com.txdb.gpmanage.audit.ui.composite.page.AuditLogComposite;
import com.txdb.gpmanage.core.entity.impl.GPAuditEntity;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.connector.IGPConnector;

public class AuditContainer extends Composite {
	
	private GPAuditEntity auditEntity;
	private IGPConnector gpController;
	private AuditComposite auditComposite;
	
	// Pages List
	private List<IupperComposite> compositeList;
	private AuditConditionComposite auditConditionComposite;
	private AuditLogComposite auditLogComposite;
	
	private List<AuditTaskThread> taskThreadList = new ArrayList<AuditTaskThread>();
	
	AuditContainer(GPAuditEntity auditEntity, AuditComposite auditComposite, Composite parent, int style) {
		super(parent, style);
		this.auditEntity = auditEntity;
		this.auditComposite = auditComposite;
		createControl();
	}
	
	private void createControl() {
		StackLayout layout = new StackLayout();
		setLayout(layout);
		
		compositeList = new ArrayList<IupperComposite>();
		
		// Audit
		auditConditionComposite = new AuditConditionComposite(auditComposite, this, CompositeCode.AUDIT_CONDITION);
		compositeList.add(auditConditionComposite);
		auditLogComposite = new AuditLogComposite(auditComposite, this, CompositeCode.AUDIT_LOG);
		compositeList.add(auditLogComposite);
		
		layout.topControl = compositeList.get(0);
		layout();
	}
	
	public void switchTopComposite(CompositeCode code) {
		Control[] children = getChildren();
		for (Control child : children) {
			if (((IupperComposite) child).getCode() == code) {
				StackLayout layout = (StackLayout) getLayout();
				if (!layout.topControl.equals((Composite) child)) {
					layout.topControl = (Composite) child;
					layout();
				}
				break;
			}
		}
	}
	
	public String getAuditName() {
		return auditEntity.getAuditName();
	}
	
	@Override
	public void dispose() {
		Control[] controls = getChildren();
		for (Control control : controls)
			control.dispose();
		super.dispose();
	}
	
	// Main GP Controller
	public IGPConnector getGpController() {
		return gpController;
	}

	public void setGpController(IGPConnector gpController) {
		this.gpController = gpController;
	}
	
	// Data Task Thread
	public void taskThreadStart() {
		final long timeInterval = 15000;
		AuditTaskThread taskThread = new AuditTaskThread() {
			@Override
			public void taskBody() {
				// Refresh Data Without Chart
				loadStaticData();
				
				try {
					Thread.sleep(timeInterval);
				} catch (InterruptedException e) {}
			}
		};
		taskThreadList.add(taskThread);
		taskThread.start();
	}
	
	public void taskThreadStop() {
		for (AuditTaskThread task : taskThreadList)
			task.stopMe();
		taskThreadList.clear();
	}
	
	private abstract class AuditTaskThread extends Thread {
		private boolean stopMe;
		
		@Override
		public void run() {
			while (!stopMe)
				taskBody();
		}
		
		public void stopMe() {
			stopMe = true;
		}
		
		public abstract void taskBody();
	}
	
	public void updateAuditCommand(String command) {
		auditLogComposite.updateCommand(command);
	}
	
	public String getLocalFileWithDelimiter() {
		return auditConditionComposite.getInputFile() + "@" + auditConditionComposite.getDelimiter();
	}
	
	public void loadData() {
		for (IupperComposite composite : compositeList)
			((BaseCompositePage) composite).loadData();
	}
	
	public void loadStaticData() {
		try {
			// Audit
			auditConditionComposite.loadStaticData();
			auditLogComposite.loadStaticData();
			
		} catch (Exception e) {
			System.err.println("Exception occured durning staticData updating... " + e.getMessage());
		}
	}
}
