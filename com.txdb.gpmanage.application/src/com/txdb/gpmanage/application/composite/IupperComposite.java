package com.txdb.gpmanage.application.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.connector.UICallBack;
import com.txdb.gpmanage.core.service.AbstractUIService;

/**
 * 各导航节点对应的面板
 * @author ws
 */
public abstract class IupperComposite extends Composite implements UICallBack {
	
	/**
	 * 父类窗口（mainComposite）
	 */
	public AbstractComposite mianComposite;
	
	/**
	 * 消息输出框
	 */
	public StyledText text;
	
	/**
	 * 中间界面
	 */
//	private Composite center;
	
	/**
	 * 滚动条
	 */
	private ProgressBar bar;
	
	/**
	 * 默认display
	 */
	public Display display;

	public CompositeCode getCode() {
		return code;
	}

	/**
	 * 面板Code
	 */
	private CompositeCode code;

	public IupperComposite(AbstractComposite mainComposite, Composite parent, CompositeCode code, String title, String desc) {
		super(parent, SWT.NONE);
		this.code = code;
		this.mianComposite = mainComposite;
		display = Display.getDefault();
		initComposite(title, desc);
	}

	/**
	 * 创建中部
	 * 
	 * @param composte
	 */
	abstract public void createCenter(Composite composte);

	/**
	 * 创建头部
	 * 
	 * @param title
	 * @param desc
	 */
	public void createTitle(String title, String desc) {
		Composite titleComposite = new Composite(this, SWT.BORDER);
		titleComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		titleComposite.setLayout(new GridLayout());
		Label titleLb = new Label(titleComposite, SWT.NONE);
		Font font = new Font(Display.getCurrent(), "Courier New", 10, SWT.BOLD);
		titleLb.setFont(font);
		titleLb.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		// font.dispose();
		titleLb.setText(title);
		Label descLb = new Label(titleComposite, SWT.NONE);
		descLb.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		descLb.setText(desc);
	}

	/**
	 * 获取服务类
	 * 
	 * @return
	 */
	public AbstractUIService getService() {
		return mianComposite.getService();
	}

	/**
	 * 初始化
	 * 
	 * @param title
	 * @param desc
	 */
	public void initComposite(String title, String desc) {
		this.setLayout(new GridLayout());
		createTitle(title, desc);
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite center = new Composite(sashForm, SWT.BORDER);
		Composite bottom = new Composite(sashForm, SWT.BORDER);
		sashForm.setWeights(new int[] { 4, 1 });
		center.setLayout(new GridLayout());
		ScrolledComposite scrolledComposite = new ScrolledComposite(center, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		Composite mainComposite = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(mainComposite);
		createCenter(mainComposite);
		
		// 设置滚动条面板
		scrolledComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		createBottom(bottom);
	}

	/**
	 * 消息输出面板
	 * 
	 * @param composte
	 */
	public void createBottom(Composite composte) {
		// 消息输出面板
		composte.setLayout(new GridLayout(2, false));
		bar = new ProgressBar(composte, SWT.HORIZONTAL | SWT.INDETERMINATE);
		bar.setMinimum(0);
		bar.setMaximum(30);
		bar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		bar.setVisible(false);
		Button clearBtn = new Button(composte, SWT.NONE);
		clearBtn.setText("清空");
		clearBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		
		// Label selectLb = new Label(composte, SWT.NONE);
		// selectLb.setText(ResourceHandler.getValue("message"));
		// selectLb.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		
		text = new StyledText(composte, SWT.WRAP | SWT.H_SCROLL | SWT.BORDER | SWT.V_SCROLL);
		text.setEditable(false);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		text.setTopIndex(Integer.MAX_VALUE);
		clearBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText("");
			}
		});
	}

	@Override
	public void refreshUI(final String msg) {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				text.append(msg);
				text.setTopIndex(Integer.MAX_VALUE);
			}
		});
	}

	/**
	 * 开始滚动条
	 */
	public void startBar() {
//		bar.setSelection(1);
		bar.setVisible(true);
	}

	/**
	 * 结束滚动条
	 */
	public void stopBar() {
//		bar.setSelection(1);
		bar.setVisible(false);
	}

	public AbstractComposite getMianComposite() {
		return mianComposite;
	}

//	public Composite getCenter() {
//		return center;
//	}
}
