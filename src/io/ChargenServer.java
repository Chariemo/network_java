package io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChargenServer {
	
	public static final int DEFAULT_PORT = 3000;
	
	public static void main(String[] args) {
		
		int port;
		try {
			port = Integer.parseInt(args[0]);
			if (port < 0 || port > 65536) {
				System.out.println("The port must be between 0 and 65536");
				return;
			}
		} catch (IndexOutOfBoundsException exception) {
			port = DEFAULT_PORT;
		}
		
		System.out.println("Listening for connections on port " + port);
		
		byte[] rotation = new byte[95 * 2];
		for (byte i = ' '; i <= '~'; i++) {
			rotation[i-' '] = i;
			rotation[i+95-' '] = i;
		}
		
		ServerSocketChannel serverSocketChannel;
		Selector selector;
		try {
			serverSocketChannel = ServerSocketChannel.open();
			ServerSocket serverSocket = serverSocketChannel.socket();
			InetSocketAddress address = new InetSocketAddress(port);
			serverSocket.bind(address);
			selector = Selector.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
		} catch (IOException exception) {
			exception.printStackTrace();
			return;
		}
		
		while (true) {
			
			try {
				selector.select();
			} catch (IOException exception) {
				exception.printStackTrace();
				break;
			}
			
			Set readyKey = selector.selectedKeys();
			Iterator iterator = readyKey.iterator();
			while (iterator.hasNext()) {
				
				SelectionKey key = (SelectionKey) iterator.next();
				iterator.remove();
				try {
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel client = server.accept();
						client.configureBlocking(false);
						System.out.println("Accepted connection from " + client);
						SelectionKey key2 = client.register(selector, SelectionKey.OP_WRITE);
						ByteBuffer buffer = ByteBuffer.allocate(74);
						buffer.put(rotation, 0, 72);
						buffer.put((byte)'\r');
						buffer.put((byte)'\n');
						buffer.flip();
						key2.attach(buffer);
					}
					else if (key.isWritable()) {
						SocketChannel client = (SocketChannel) key.channel();
						ByteBuffer buffer = (ByteBuffer) key.attachment();
						if (!buffer.hasRemaining()) {
							buffer.rewind();
							int first = buffer.get();
							buffer.rewind();
							int position = first - ' ' + 1;
							buffer.put(rotation, position, 72);
							buffer.put((byte)'\r');
							buffer.put((byte)'\n');
							buffer.flip();
						}
						client.write(buffer);
					}
				} catch (IOException exception) {
					key.cancel();
					try {
						key.channel().close();
					} catch (IOException exception2) {
						
					}
				}
			}
		}
	}
}
