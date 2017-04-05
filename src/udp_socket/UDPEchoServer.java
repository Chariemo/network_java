package udp_socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

public class UDPEchoServer extends UDPServer {

	public static final int DEFAULT_PORT = 3333;
	
	public UDPEchoServer(int port) throws SocketException {
		super(port);
		
	}
	public UDPEchoServer() throws SocketException {
		
		super(DEFAULT_PORT);
	}

	@Override
	public void respond(DatagramPacket request) {
		
		DatagramPacket output = new DatagramPacket(request.getData(), request.getLength(), 
				request.getAddress(), request.getPort());
		
		try {
			socket.send(output);
		} catch (IOException e) {
			
			System.out.println(e);
			
		}
	}
	
	public static void main(String[] args) {
		
		try {
			UDPEchoServer server = new UDPEchoServer();
			server.start();
		} catch (SocketException e) {
			System.err.println(e);
		}
		
	}

}
