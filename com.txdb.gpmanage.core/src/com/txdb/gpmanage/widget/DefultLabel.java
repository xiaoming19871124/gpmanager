package com.txdb.gpmanage.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.txdb.gpmanage.core.Activator;


public class DefultLabel {
private Image img_link = Activator.getImage("icons/link.png");
private Image img_link_gray = Activator.getImage("icons/link-gray.png");
private Image icon;
private Composite parent;
private int style;
private GridData layoutData;
private String text;
	public DefultLabel(Composite parent, int style,GridData layoutData,final String text) {
		this.parent=parent;
		this.style=style;
		this.layoutData=layoutData;
		this.text=text;
	}
	public DefultLabel(Composite parent, int style,GridData layoutData,final String text,Image icon) {
		this.parent=parent;
		this.style=style;
		this.layoutData=layoutData;
		this.text=text;
		this.icon=icon;
	}
public Label createLabel() {
	final Label label = new Label(parent, style);
	label.setLayoutData(layoutData);
	label.setImage(img_link_gray);
	label.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
	label.addPaintListener(new PaintListener() {
		@Override
		public void paintControl(PaintEvent e) {
			GC gc = e.gc;
			if(icon!=null)
				gc.drawImage(icon, 0, 0, icon.getBounds().width, icon.getBounds().height, e.width/2-50,  e.height/2-10, icon.getBounds().width, icon.getBounds().height);
			if(text!=null){
				gc.setFont(new Font(Display.getCurrent(), "微软雅黑", 15, SWT.NORMAL));
				gc.drawText(text, e.width/2-20, e.height/2-15, true);
				
			}
			
		}
	});
	label.addMouseTrackListener(new MouseTrackListener() {
		@Override
		public void mouseHover(MouseEvent e) {
		}
		@Override
		public void mouseExit(MouseEvent e) {
			label.setImage(img_link_gray);
			
		}
		
		@Override
		public void mouseEnter(MouseEvent e) {
			label.setImage(img_link);
			
		}
	});
return label;

}
}
