package udp_socket;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPEchoServerWithChannels {

	public static final int DEFAULT_PORT = 5555;
	public static final int MAX_PACKET_SIZE = 65507;
	
	public static void main(String[] args) {
		
		int port = DEFAULT_PORT;
		
		try {
			port = Integer.parseInt(args[0]);
		} catch (IndexOutOfBoundsException exception) {
			port = DEFAULT_PORT;
		}
		
		try {
			DatagramChannel channel = DatagramChannel.open();
			DatagramSocket socket = channel.socket();
			SocketAddress address = new InetSocketAddress(port);
			socket.bind(address);
			ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
			
			while (true) {
				
				SocketAddress client = channel.receive(buffer);
				buffer.flip();
				channel.send(buffer, client);
				buffer.clear();
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
