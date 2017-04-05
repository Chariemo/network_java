package udp_socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public abstract class UDPServer extends Thread {

	private int BufferSize;
	protected DatagramSocket socket;
	
	public UDPServer(int port, int bufferSize) throws SocketException {
		this.socket = new DatagramSocket(port);
		this.BufferSize = bufferSize;
	}
	
	public UDPServer(int port) throws SocketException {
		this(port, 8192);
	}
	
	public void run() {
		
		byte[] buffer = new byte[BufferSize];
		while(true) {
			
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(incoming);
				this.respond(incoming);
			} catch (IOException exception) {
				System.err.println(exception);
			}
		}
	}
	
	public abstract void respond(DatagramPacket request);
}
