package com.txdb.gpmanage.core.gp.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class BrowserTest {

	private Display display;
	private Shell shell;
	private Point applicationSize = new Point(1200, 650);
	
	private Browser browser;

	public BrowserTest() {
		display = new Display();
		shell = new Shell(display, SWT.SHELL_TRIM);
		init();
	}

	private void init() {
		shell.setText("GP安装演示");
		Rectangle screenBounds = Display.getCurrent().getBounds();
		shell.setBounds((screenBounds.width - applicationSize.x) / 2, 
				(screenBounds.height - applicationSize.y) / 2, applicationSize.x, applicationSize.y);
		
		shell.setLayout(new FillLayout());
		Composite parent = new Composite(shell, SWT.NORMAL);
		createClientArea(parent);
	}

	public void open() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!Display.getCurrent().readAndDispatch())
				Display.getCurrent().sleep();
		}
		Display.getCurrent().dispose();
	}

	public void createClientArea(Composite parent) {
		GridData gridData;
		GridLayout gridLayout = new GridLayout(1, false);
		parent.setLayout(gridLayout);
		
		Button btn = new Button(parent, SWT.NONE);
		btn.setText("Load");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					browser.setUrl("http://localhost:9681/fusioncharts-suite-xt-v3.12.2/monitorCharts/host_usage_cpu.jsp");
				} catch (SWTError ex) {
					System.err.println("ex: " + ex.getMessage());
				}
			}
		});
		
		browser = new Browser(parent, SWT.NONE);
		gridData = new GridData(GridData.FILL_BOTH);
		browser.setLayoutData(gridData);
	}

	public static void main(String[] args) {
		BrowserTest browserTest = new BrowserTest();
		browserTest.open();
	}
}
