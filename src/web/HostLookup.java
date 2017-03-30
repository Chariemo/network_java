package web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostLookup {
	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String host = null;
		System.out.println("Please input host:(e or q to quit)");
		try {
			while (true) {
				host = reader.readLine();
				if (host.equals("e") || host.equals("q")) {
					break;
				}
				System.out.println(lookup(host));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	private static String lookup(String host) {
		String result = null;
		InetAddress address = null;
		try {
			address = InetAddress.getByName(host);
		} catch(UnknownHostException exception) {
			System.out.println("Can not find host: " + host);
		}
		
		if (isHostName(host)) {
			result = address.getHostAddress();
		}
		else {
			result = address.getHostName();
		}
		
		return result;
	}
	
	private static boolean isHostName(String host) {
		boolean result = false;
		
		if (host.indexOf(':') != -1) {
			return result;
		}
		
		char[] chhost = host.toCharArray();
		for (int i = 0; i < chhost.length; i++) {
			if (!Character.isDigit(chhost[i]) && chhost[i] != '.') {
				result = true;
			}
		}
		return result;
	}
}
