package udp_socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class MulticastSender {

	public static final String DEFAULT_HOST = "all-systems.mcast.net";
	public static final int DEFAULT_PORT = 8888;
	
	public static void main(String[] args) {
		
		InetAddress address = null;
		int port = DEFAULT_PORT;
		byte ttl = (byte) 2;
		
		try {
			address = InetAddress.getByName(DEFAULT_HOST);
		} catch (Exception exception) {
			
		}
		
		byte[] data = "Here's some multicast data\r\n".getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		
		try {
			MulticastSocket multicastSocket = new MulticastSocket();
			multicastSocket.joinGroup(address);
			multicastSocket.send(packet);
			multicastSocket.leaveGroup(address);
			multicastSocket.close();
		} catch (SocketException exception) {
			System.err.println(exception);
		} catch (IOException exception) {
			System.err.println(exception);
		}
	}
}
