package udp_socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSniffer {

	public static void main(String[] args) {
			
		InetAddress group = null;
		int port = 8888;
		
		try {
			group = InetAddress.getByName("all-systems.mcast.net");
		} catch (Exception exception) {
			
		}
		
		MulticastSocket multicastSocket = null;
		
		try {
			multicastSocket = new MulticastSocket(port);
			multicastSocket.joinGroup(group);
			System.out.println("connected");
			byte[] buffer = new byte[8192];
			while (true) {
				
				DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);				
				multicastSocket.receive(datagramPacket);
				String string = new String(datagramPacket.getData());
				System.out.println(string);
			}
		} catch (IOException exception) {
			if (multicastSocket != null) {
				try {
					multicastSocket.leaveGroup(group);
					multicastSocket.close();
				} catch (IOException e) {
					
				}
				
			}
		}
	}
}
