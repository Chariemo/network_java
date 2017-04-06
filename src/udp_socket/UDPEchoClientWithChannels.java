package udp_socket;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class UDPEchoClientWithChannels {

	public static final int DEFAULT_PORT = 5555;
	public static final int LIMIT = 100;
	
	public static void main(String[] args) {
		
		int port = DEFAULT_PORT;
		try {
			port = Integer.parseInt(args[0]);
		} catch (IndexOutOfBoundsException e) {
			port = DEFAULT_PORT;
		}
		
		SocketAddress address = null;
		try {
			address = new InetSocketAddress("localhost", port);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			DatagramChannel channel = DatagramChannel.open();
			channel.configureBlocking(false);
			channel.connect(address);
			Selector selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			
			ByteBuffer buffer = ByteBuffer.allocate(4);
			int n = 0;
			int numbersRead = 0;
			while (true) {
			
				selector.select(6000);
				Set readyKeys = selector.selectedKeys();
				if (readyKeys.isEmpty() && n == LIMIT) {
					break;
				}
				
				else {
					Iterator iterator = readyKeys.iterator();
					while (iterator.hasNext()) {
						
						SelectionKey key = (SelectionKey) iterator.next();
						iterator.remove();
						if (key.isReadable()) {
							buffer.clear();
							channel.read(buffer);
							buffer.flip();
							int echo = buffer.getInt();
							System.out.println("Read: " + echo);
							numbersRead++;
							
						}
						if (key.isWritable()) {
							buffer.clear();
							buffer.putInt(n);
							buffer.flip();
							channel.write(buffer);
							System.out.println("Write: " + n);
							n++;
							
						}
						if (n == LIMIT) {
							key.interestOps(SelectionKey.OP_READ);
						}
					}
				}
			}
			System.out.println("Echoed " + numbersRead + " out of " + LIMIT + " sent");
			System.out.println("Success rate: " + 100.0 + numbersRead/LIMIT + "%");
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
