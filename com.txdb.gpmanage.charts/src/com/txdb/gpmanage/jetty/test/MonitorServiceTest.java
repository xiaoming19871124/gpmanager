package com.txdb.gpmanage.jetty.test;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

public class MonitorServiceTest {
	
	public static String address = "http://localhost:9681/services/MonitorService";
	
	public static void main(String[] args) throws IOException {
		
		String systemJsonStr = "[{\"cpu_idle\":95.19,\"cpu_sys\":2.89,\"cpu_used\":4.44,\"cpu_user\":1.55,\"ctime\":{\"date\":21,\"day\":2,\"hours\":16,\"minutes\":38,\"month\":7,\"seconds\":45,\"time\":1534840725000,\"timezoneOffset\":-480,\"year\":118},\"disk_rb_rate\":0,\"disk_ro_rate\":0,\"disk_wb_rate\":182716,\"disk_wo_rate\":45,\"hostname\":\"mdw\",\"load0\":0.15,\"load1\":0.03,\"load2\":0.01,\"mem_actual_free\":1465401344,\"mem_actual_used\":488161280,\"mem_total\":1953562624,\"mem_used\":1805946880,\"mem_used_percent\":24.99,\"net_rb_rate\":156016,\"net_rp_rate\":246,\"net_wb_rate\":169781,\"net_wp_rate\":253,\"quantum\":15,\"swap_page_in\":0,\"swap_page_out\":0,\"swap_total\":4194295808,\"swap_used\":0},{\"cpu_idle\":96.78,\"cpu_sys\":2.45,\"cpu_used\":2.99,\"cpu_user\":0.54,\"ctime\":{\"date\":21,\"day\":2,\"hours\":16,\"minutes\":38,\"month\":7,\"seconds\":45,\"time\":1534840725000,\"timezoneOffset\":-480,\"year\":118},\"disk_rb_rate\":0,\"disk_ro_rate\":0,\"disk_wb_rate\":146157,\"disk_wo_rate\":36,\"hostname\":\"sdw1\",\"load0\":0.05,\"load1\":0.09,\"load2\":0.03,\"mem_actual_free\":1544667136,\"mem_actual_used\":408895488,\"mem_total\":1953562624,\"mem_used\":1631113216,\"mem_used_percent\":20.93,\"net_rb_rate\":131892,\"net_rp_rate\":165,\"net_wb_rate\":118330,\"net_wp_rate\":158,\"quantum\":15,\"swap_page_in\":0,\"swap_page_out\":0,\"swap_total\":4194295808,\"swap_used\":0}]";
		Object[] result = invoke("updateSystemNow", new Object[] { systemJsonStr }, new Class[] { String.class });
		System.out.println(result[0]);
	}

	@SuppressWarnings("rawtypes")
	public static Object[] invoke(String method, Object[] params, Class[] classes) throws IOException {
		RPCServiceClient client = new RPCServiceClient();
		Options option = client.getOptions();

		EndpointReference reference = new EndpointReference(address);
		option.setTo(reference);
		QName qname = new QName("http://www.jettywebservice.com/xsd", method);
		Object[] result = client.invokeBlocking(qname, params, classes);
		
		return result;
	}
}
