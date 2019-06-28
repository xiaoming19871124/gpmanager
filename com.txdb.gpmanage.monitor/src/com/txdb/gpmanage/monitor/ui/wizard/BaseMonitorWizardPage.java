package com.txdb.gpmanage.monitor.ui.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public abstract class BaseMonitorWizardPage extends WizardPage {
	
	private final String ENABLE_FLAG = "#ENABLE_FLAG";
	protected final String GP_CONTROLLER = "#GP_CONTROLLER";
	
	protected Composite operateComp;
	protected ProgressBar progressBar;
	
	public final static String MONITOR_NAME = "#MONITOR_NAME";

	protected BaseMonitorWizardPage(String pageName, String title) {
		super(pageName);
		setTitle(title);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		
		// 1.0 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		operateComp = new Composite(composite, SWT.NONE);
		operateComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		operateComp.setLayout(new FillLayout());
		createBody(operateComp);
		
		// 2.0 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		Composite progressComp = new Composite(composite, SWT.NONE);
		progressComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressComp.setLayout(new GridLayout(1, false));
		
		progressBar = new ProgressBar(progressComp, SWT.HORIZONTAL | SWT.INDETERMINATE);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setVisible(false);
		
		setControl(composite);
		setPageComplete(false);
	}
	
	protected void runTask(final WizardRunnable wizardRunnable, final String taskName) {
		progressBar.setVisible(true);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				wizardRunnable.run();
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						Map<String, Object> resultMap = new HashMap<String, Object>();
						resultMap.put(taskName, wizardRunnable.getTaskResult());
						try {
							taskComplete(resultMap);
						} catch (Exception e) {}
					}
				});
			}
		});
		thread.start();
	}
	
	public abstract class WizardRunnable implements Runnable {
		public Object taskResult;
		public Object getTaskResult() {
			return taskResult;
		}
		public void setTaskResult(Object taskResult) {
			this.taskResult = taskResult;
		}
	}
	
	public void taskComplete(Map<String, Object> taskMap) {
		progressBar.setVisible(false);
	}
	
	public abstract void createBody(Composite parent);
	
	public void fillData() {}
	
	public void restorePage() {}
	
	protected void setBodyEnable(boolean enable) {
		setBodyEnable(operateComp.getChildren(), enable);
	}
	
	protected void setBodyEnable(Control[] controls, boolean enable) {
		for (Control control : controls) {
			if (control instanceof Label)
				continue;
			
			if (control instanceof Composite || control instanceof Group)
				setBodyEnable(((Composite) control).getChildren(), enable);
			else {
				if (!enable) {
					control.setData(ENABLE_FLAG, control.isEnabled());
					control.setEnabled(enable);
				} else {
					boolean originalState = (boolean) control.getData(ENABLE_FLAG);
					control.setEnabled(originalState);
				}
			}
		}
	}
}
