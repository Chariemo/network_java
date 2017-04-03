package io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

public class ChargenClient {
	
	public static final int DEFAULT_PORT = 3000;
	
	public static void main(String[] args) {
		
		
		int port;
		try {
			port = Integer.parseInt(args[1]);
		} catch (IndexOutOfBoundsException exception) {
			port = DEFAULT_PORT;
		}
		
		try {
			SocketAddress address = new InetSocketAddress("localhost", port);
			
			SocketChannel client = SocketChannel.open(address);
			
			ByteBuffer buffer = ByteBuffer.allocate(74);
			WritableByteChannel writer = Channels.newChannel(System.out);
			
			while (client.read(buffer) != -1) {
				buffer.flip();
				writer.write(buffer);
				buffer.clear();
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
