package udp_socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPEchoClient {

	public static final int DEFAULT_PORT = 3333;
	
	public static void main(String[] args) {
		
		String host = "localhost";
		int port = DEFAULT_PORT;
		if (args.length > 0) {
			host = args[0];
		}
		
		try {
			InetAddress address = InetAddress.getByName(host);
			SenderThread sender = new SenderThread(address, port);
			sender.start();
			Thread receiver = new ReceiverThread(sender.getSocket());
			receiver.start();
		} catch (UnknownHostException e) {
			System.out.println(e);
		} catch (SocketException exception) {
			System.out.println(exception);
		}
		
	}
}

class SenderThread extends Thread {
	
	private InetAddress address;
	private DatagramSocket socket;
	private boolean stopped = false;
	private int port;
	
	public SenderThread(InetAddress address, int port) throws SocketException {
		
		this.address = address;
		this.port = port;
		this.socket = new DatagramSocket();
		this.socket.connect(address, port);
	}
	
	public void halt() {
		this.stopped = true;
		
	}
	
	public DatagramSocket getSocket() {
		return this.socket;
	}
	
	public void run() {
		
		try {
			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				if (stopped) {
					return;
				}
				String theLine = userInput.readLine();
				if (theLine.equals(".")) {
					break;
				}
				
				byte[] data = theLine.getBytes();
				DatagramPacket output = new DatagramPacket(data, data.length, address, port);
				socket.send(output);
				Thread.yield();
			}
		} catch(IOException exception) {
			System.err.println(exception);
		}
	}
}


class ReceiverThread extends Thread {
	
	private DatagramSocket socket;
	private boolean stopped = false;
	
	public ReceiverThread(DatagramSocket socket) {
		this.socket = socket;
	}
	
	public void halt() {
		this.stopped = true;
		
	}
	
	public void run() {
		
		byte[] buffer = new byte[65507];
		if (stopped) {
			return;
		}
		
		DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(incoming);
			String string = new String(incoming.getData(), 0, incoming.getData().length);
			System.out.println(string);
			Thread.yield();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
