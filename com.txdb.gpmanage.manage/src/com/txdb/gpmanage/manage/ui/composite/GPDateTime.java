package com.txdb.gpmanage.manage.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.manage.Activator;


public class GPDateTime extends Composite {
	int hour = 0;
	int minute = 0;
	int second = 0;
	private static final int WIDTHHINT = 15;
	private static final int HEIGHTINT = 10;
	static Font songFont;
	static Cursor hand;

	public GPDateTime(Composite parent, int style) {
		super(parent, style);
		songFont = new Font(Display.getCurrent(), "宋体", 10, SWT.NONE);
		hand = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
		GridLayout gd_this = new GridLayout(6, false);
		gd_this.horizontalSpacing = 2;
		gd_this.verticalSpacing = 2;
		gd_this.marginHeight = 0;
		gd_this.marginBottom = 0;
		gd_this.marginLeft = 0;
		gd_this.marginWidth = 0;
		gd_this.marginRight = 0;
		gd_this.marginTop = 0;
		this.setLayout(gd_this);
		final Text hourLb = new Text(this, SWT.BORDER);
		hourLb.setFont(songFont);
		GridData gd_hourLb = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
		gd_hourLb.widthHint = WIDTHHINT + 5;
		gd_hourLb.heightHint = HEIGHTINT;
		hourLb.setLayoutData(gd_hourLb);
		hourLb.setText("0");
		Label a = new Label(this, SWT.NONE);
		a.setText(":");
		a.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
		final Text minuteLb = new Text(this, SWT.BORDER);
		minuteLb.setText("0");
		minuteLb.setFont(songFont);
		GridData gd_minuteLb = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
		gd_minuteLb.widthHint = WIDTHHINT;
		gd_minuteLb.heightHint = HEIGHTINT;
		minuteLb.setLayoutData(gd_minuteLb);
		Label b = new Label(this, SWT.NONE);
		b.setText(":");
		b.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
		final Text secondLb = new Text(this, SWT.BORDER);
		secondLb.setText("0");
		secondLb.setFont(songFont);
		GridData gd_secondLb = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
		gd_secondLb.widthHint = WIDTHHINT;
		gd_secondLb.heightHint = HEIGHTINT;
		secondLb.setLayoutData(gd_secondLb);

		Label add = new Label(this, SWT.NONE);
		add.setText("+");
		add.setCursor(hand);
		GridData gd_add = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		add.setLayoutData(gd_add);
		add.setImage(Activator.getImage("/icons/add.png"));
		Label sub = new Label(this, SWT.NONE);
		sub.setCursor(hand);
		GridData gd_sub = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
		sub.setLayoutData(gd_sub);
		sub.setImage(Activator.getImage("/icons/sub.png"));
		secondLb.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				String value = secondLb.getText();
				if (value == null || value.isEmpty()) {
					minute = 0;
					secondLb.setText("0");
					return;
				}
				int intValue = second;
				try {
					intValue = Integer.valueOf(value);
				} catch (NumberFormatException e1) {
					secondLb.setText(String.valueOf(second));
					return;
				}
				if (intValue < 0 || intValue > 59) {
					secondLb.setText(String.valueOf(second));
					return;
				}
				second = intValue;
			}
		});
		minuteLb.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				String value = minuteLb.getText();
				if (value == null || value.isEmpty()) {
					minute = 0;
					minuteLb.setText("0");
					return;
				}
				int intValue = minute;
				try {
					intValue = Integer.valueOf(value);
				} catch (NumberFormatException e1) {
					minuteLb.setText(String.valueOf(minute));
					return;
				}
				if (intValue < 0 || intValue > 59) {
					minuteLb.setText(String.valueOf(minute));
					return;
				}
				minute = intValue;
			}
		});
		hourLb.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				String value = hourLb.getText();
				if (value == null || value.isEmpty())
					return;
				int intValue = hour;
				try {
					intValue = Integer.valueOf(value);
				} catch (NumberFormatException e1) {
					hourLb.setText(String.valueOf(hour));
					return;
				}
				if (intValue < 0) {
					hourLb.setText(String.valueOf(hour));
					return;
				}
				hour = intValue;
			}
		});
		add.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				boolean isHour = hourLb.isFocusControl();
				if (isHour) {
					hour = hour + 1;
					hourLb.setText(String.valueOf(hour));
				}
				boolean isMinute = minuteLb.isFocusControl();
				if (isMinute) {
					if (minute == 59) {
						minute = 0;
					} else {
						minute = minute + 1;
					}
					minuteLb.setText(String.valueOf(minute));
				}
				boolean isSecond = secondLb.isFocusControl();
				if (isSecond) {
					if (second == 59) {
						second = 0;
					} else {
						second = second + 1;
					}
					secondLb.setText(String.valueOf(second));
				}
			}
		});
		sub.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				boolean isHour = hourLb.isFocusControl();
				if (isHour) {
					if (hour == 0)
						return;
					hour = hour - 1;
					hourLb.setText(String.valueOf(hour));
				}
				boolean isMinute = minuteLb.isFocusControl();
				if (isMinute) {
					if (minute == 0) {
						minute = 59;
					} else {
						minute = minute - 1;
					}
					minuteLb.setText(String.valueOf(minute));
				}
				boolean isSecond = secondLb.isFocusControl();
				if (isSecond) {
					if (second == 0) {
						second = 59;
					} else {
						second = second - 1;
					}
					secondLb.setText(String.valueOf(second));
				}
			}
		});
	}

	public String getTime() {
		return hour + ":" + minute + ":" + second;
	}

	public void canEnabled(boolean isEnabled) {
		Control[] children = this.getChildren();
		for (Control c : children) {
			c.setEnabled(isEnabled);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		hand.dispose();
		songFont.dispose();
	}
}
