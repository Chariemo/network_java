package nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NonblockingSingleFileHTTPServer {

	private ByteBuffer contentBuffer;
	private int port = 80;
	
	public NonblockingSingleFileHTTPServer(ByteBuffer data, String encoding, String MIMEType, int port) throws UnsupportedEncodingException {
		
		this.port = port;
		
		String header = "HTTP/1.0 200 OK\r\n"
				+ "Server: OneFile 2.0\r\n"
				+ "Content-length: " + data.limit() + "\r\n" 
				+ "Content-type: " + MIMEType + "\r\n\r\n";
		
		byte[] headerData = header.getBytes("ASCII");
		
		ByteBuffer buffer = ByteBuffer.allocate(headerData.length + data.limit());
		buffer.put(headerData);
		buffer.put(data);
		buffer.flip();
		this.contentBuffer = buffer;
	}
	
	
	public void run() throws IOException {
		
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		ServerSocket server = serverSocketChannel.socket();
		InetSocketAddress address = new InetSocketAddress(port);
		server.bind(address);
		serverSocketChannel.configureBlocking(false);
		Selector selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("Listening for connections on port " + port);
		while (true) {
			
			selector.select();
			Iterator keys = selector.selectedKeys().iterator();
			
			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();
				keys.remove();
				
				try {
					if (key.isAcceptable()) {
						ServerSocketChannel serverSocketChannel2 = (ServerSocketChannel) key.channel();
						SocketChannel socketChannel = serverSocketChannel2.accept();
						socketChannel.configureBlocking(false);
						socketChannel.register(selector, SelectionKey.OP_READ);
					}
					else if (key.isWritable()) {
						SocketChannel socketChannel = (SocketChannel) key.channel();
						ByteBuffer buffer = (ByteBuffer) key.attachment();
						if (buffer.hasRemaining()) {
							socketChannel.write(buffer);
						}
						else {
							socketChannel.close();
						}
					}
					else if (key.isReadable()) {
						SocketChannel socketChannel = (SocketChannel) key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(4096);
						socketChannel.read(buffer);
						key.interestOps(SelectionKey.OP_WRITE);
						key.attach(contentBuffer);
					}
				} catch (IOException e) {
					key.cancel();
					try {
						key.channel().close();
					} catch (IOException exception) {
						
					}
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		
		File file = new File("src/socket/index.html");
		try {
			String contentType = "text/plain";
			if (file.getName().endsWith(".html") || file.getName().endsWith(".htm")) {
				contentType = "text/html";
			}
			FileInputStream fInputStream = new FileInputStream(file);
			FileChannel fileChannel = fInputStream.getChannel();
			ByteBuffer data = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			
			int port;
			try {
				port = Integer.parseInt(args[1]);
				if (port < 1 || port > 65535) {
					port = 80;
				}
			} catch (IndexOutOfBoundsException exception) {
				port = 80;
			}
			
			String encoding = "ASCII";
			if (args.length > 2) {
				encoding = args[2];
			}
			
			NonblockingSingleFileHTTPServer server = new NonblockingSingleFileHTTPServer(data, encoding, contentType, port);
			server.run();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
	}
}
