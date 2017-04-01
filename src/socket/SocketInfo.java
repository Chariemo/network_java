package socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketInfo {
	
	
	public static void main(String[] args) throws IOException {
		InputStream reader = null;
		for (int i = 0; i < args.length; i += 2) {
			try {
				Socket socket = new Socket(args[i], Integer.parseInt(args[i + 1]));
				reader = socket.getInputStream();
				int c;
				StringBuffer time = new StringBuffer();
				while ((c = reader.read()) != -1) {
					time.append((char) c);
				}
				System.out.println("Connected to " + socket.getInetAddress() + " on port " +
						socket.getPort() + " from port " + socket.getLocalPort() + " of " + 
						socket.getLocalAddress() + " " + time);
			} catch (UnknownHostException exception) {
				System.out.println("Can't to find " + args[i]);
			} catch (IOException exception) {
				System.err.println("Could not connect host " + args[i]);
			} finally {
				reader.close();
			}
		}
		
		
	}
}
