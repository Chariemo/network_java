package socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class PortScanner {
	
	public static void main(String[] args) {
		
		String host = "localhost";
		if (args.length > 0) {
			host = args[0];
		}
		Scanner reader = new Scanner(System.in);
		int port_max = 0;
		System.out.println("from port: ");
		port_max = reader.nextInt();
		try {
			InetAddress inetAddress = InetAddress.getByName(host);
			for (int i = 1; i < port_max; i++) {
				Socket socket = null;
				try {
					 socket = new Socket(inetAddress, i);
					System.out.println("There is a server on port " + i + " of " + host);
				} catch (Exception e) {
					// TODO: handle exception
					continue;
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
