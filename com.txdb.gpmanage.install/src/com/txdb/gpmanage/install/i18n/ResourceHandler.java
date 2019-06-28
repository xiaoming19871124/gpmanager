package com.txdb.gpmanage.install.i18n;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
public class ResourceHandler {
	private static final String DEFAULT_RES_BUNDLE_NAME = "com.txdb.gpmanage.install.i18n.message";
	private static Map<Locale, ResourceBundle> resourceBundles = new Hashtable<Locale, ResourceBundle>();
	public static ResourceBundle getBundle(Locale locale) {
		ResourceBundle bundle = (ResourceBundle)resourceBundles.get(locale);
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(DEFAULT_RES_BUNDLE_NAME, locale);
			resourceBundles.put(locale, bundle);
		}
		return bundle;
	}
	public static String getValue(String key, Object[] paramValues) {
		// Locale.setDefault(Locale.ENGLISH);
		return MessageFormat.format(getBundle(Locale.getDefault()).getString(key), paramValues);
	}
	public static String getValue(String key) {
		// Locale.setDefault(Locale.ENGLISH);
		return getBundle(Locale.getDefault()).getString(key);
	}
}
/* 

 * 
 */