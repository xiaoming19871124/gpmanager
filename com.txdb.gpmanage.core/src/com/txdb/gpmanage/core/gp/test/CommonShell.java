//package com.txdb.gpmanage.core.gp.test;
//
//import java.util.Properties;
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.Text;
//
//import com.txdb.gpmanage.core.gp.service.ExecuteSite;
//
//public class CommonShell {
//	
//	private Shell shell;
//	private Point applicationSize = new Point(500, 400);
//	
//	private Text txt_hosts;
//	
//	private ExecuteSite executeSite;
//	
//	public CommonShell(Shell parent, ExecuteSite executeSite) {
//		shell = new Shell(parent, SWT.CLOSE | SWT.APPLICATION_MODAL);
//		this.executeSite = executeSite;
//		init();
//	}
//	
//	private void init() {
//		shell.setText("修改/etc/hosts");
//		Rectangle screenBounds = Display.getCurrent().getBounds();
//		shell.setBounds((screenBounds.width - applicationSize.x) / 2, 
//				(screenBounds.height - applicationSize.y) / 2, applicationSize.x, applicationSize.y);
//		
//		shell.setLayout(new FillLayout());
//		Composite parent = new Composite(shell, SWT.NORMAL);
//		createClientArea(parent);
//	}
//	
//	private void createClientArea(Composite parent) {
//		parent.setLayout(new GridLayout(1, false));
//		
//		GridData gridData = new GridData(GridData.FILL_BOTH);
//		txt_hosts = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
//		txt_hosts.setLayoutData(gridData);
//		txt_hosts.append("192.168.73.120 mdw\n");
//		txt_hosts.append("192.168.73.121 sdw\n");
//		txt_hosts.append("192.168.73.122 sdw2");
//		
//		Composite operateArea = new Composite(parent, SWT.NO_TRIM);
//		gridData = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false);
//		gridData.heightHint = 35;
//		gridData.widthHint = 200;
//		operateArea.setLayoutData(gridData);
//		operateArea.setLayout(new GridLayout(2, false));
//		
//		Button btn_ok = new Button(operateArea, SWT.PUSH);
//		btn_ok.setText("修改");
//		gridData = new GridData(GridData.FILL_BOTH);
//		btn_ok.setLayoutData(gridData);
//		btn_ok.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				String[] hosts = txt_hosts.getText().replaceAll("\r", "").split("\n");
//				Properties props = new Properties();
//				for (String host : hosts) {
//					String[] ipName = host.split(" ");
//					System.out.println(ipName[0] +"<->"+ ipName[1]);
//					props.setProperty(ipName[0], ipName[1]);
//				}
////				executeSite.getEnvProxy().updateHosts(props);
//			}
//		});
//		Button btn_cancel = new Button(operateArea, SWT.PUSH);
//		btn_cancel.setText("关闭");
//		gridData = new GridData(GridData.FILL_BOTH);
//		btn_cancel.setLayoutData(gridData);
//		btn_cancel.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				shell.close();
//			}
//		});
//	}
//	
//	public void open() {
//		shell.open();
//	}
//}
