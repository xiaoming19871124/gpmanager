package com.txdb.gpmanage.application;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.txdb.gpmanage.application.composite.AbstractUICreater;

public class ExtenSionFactory {
	private static final String EXTENSION_ID = "com.txdb.ui.wizard";
	private static ExtenSionFactory factory;
	private AbstractUICreater[] list = null;

	public static ExtenSionFactory getInstance() {
		if (factory == null)
			factory = new ExtenSionFactory();
		return factory;
	}

	public AbstractUICreater[] getAllCreater() {
		if (list != null)
			return list;
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
		if (elements == null || elements.length < 1)
			return new AbstractUICreater[] {};
		list = new AbstractUICreater[elements.length];
		for (IConfigurationElement element : elements) {
			AbstractUICreater creater = null;
			try {
				creater = (AbstractUICreater) element.createExecutableExtension("class");
				String location = element.getAttribute("location");
				if (location != null)
					creater.setLocation(Integer.valueOf(location));
				list[Integer.valueOf(location)] = creater;

			} catch (CoreException e) {
				e.printStackTrace();
			}

		}
		return list;
	}
}
