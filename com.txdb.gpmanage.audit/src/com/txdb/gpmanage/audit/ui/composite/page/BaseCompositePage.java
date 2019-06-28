package com.txdb.gpmanage.audit.ui.composite.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.txdb.gpmanage.application.composite.AbstractComposite;
import com.txdb.gpmanage.application.composite.IupperComposite;
import com.txdb.gpmanage.audit.ui.composite.AuditContainer;
import com.txdb.gpmanage.core.exception.CompositeCode;

public abstract class BaseCompositePage extends IupperComposite {

	// 日志查询输出
	public final String PGLOG_OUTPUT_DIR = "/pgLogOutput/";
	// 日志查询收集
	public final String PGLOG_GATHER_DIR = "/pgLogGather/";
	// 日志查询上传
	public final String PGLOG_UPLOAD_DIR = "/pgLogUpload/";
	
	protected final String KEYWORD_HOSTNAME = "$(hostname)";
	protected final String KEYWORD_TEMPNAME = "<example.output>";
	protected final String KEYWORD_UPLOAD   = "<example.upload>";
	
	protected final String AT_FLAG = "@";
	protected AuditContainer mainContainer;
	
	protected final String WAITING_PAGE = "http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/waiting.jsp";
	
	public BaseCompositePage(AbstractComposite mainComposite, AuditContainer mainContainer, CompositeCode code, String title, String desc) {
		super(mainComposite, mainContainer, code, title + " (" + mainContainer.getAuditName() + ")", desc);
		this.mainContainer = mainContainer;
	}
	
	public AuditContainer getContainer() {
		return mainContainer;
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
	
	public abstract void loadData();
	public void loadStaticData() throws Exception {}
}
