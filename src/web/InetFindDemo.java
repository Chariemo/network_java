package web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetFindDemo {
	public static void main(String[] args) throws IOException {
		try {
			InetAddress[] address = InetAddress.getAllByName("www.baidu.com");
			for (int i = 0; i < address.length; i++) {
				System.out.println(address[i]);
			}
			
			/*NetworkInterface nInterface = NetworkInterface.getByInetAddress((InetAddress.getByName("127.0.0.1")));
			System.out.println(nInterface);*/
			
			/*Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface nInterface2 = (NetworkInterface) interfaces.nextElement();
				System.out.println(nInterface2);
			}
			*/
			InetAddress inetAddress = InetAddress.getByName("220.181.111.188");
			System.out.println("localAddress: " + InetAddress.getLocalHost());
		//	System.out.println(inetAddress.isReachable(2000) + " " + inetAddress.getHostName());
		} catch(UnknownHostException exception) {
			exception.printStackTrace();
		}
	}
}
