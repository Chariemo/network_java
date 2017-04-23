package socket;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 3456;
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		String host = DEFAULT_HOST;
		int port = DEFAULT_PORT;
		try {
			if (args.length > 0) {
				host = args[0];
			}
			if (args.length > 1) {
				port = Integer.parseInt(args[1]);
			}
		} catch(IndexOutOfBoundsException exception) {
			
		}
		
		Socket socket = new Socket(host, port);
		System.out.println("Connect " + socket.getInetAddress() + " finished");
		
		Thread readerThread = new Thread(new SocketReader(socket.getInputStream()));
		readerThread.start();
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		String string;
		while ((string = bufferedReader.readLine()) != null) {
			bufferedWriter.write(string + "\r\n");
			bufferedWriter.flush();
		}
		
		bufferedReader.close();
		bufferedWriter.close();
	}
	static class SocketReader implements Runnable {

		private InputStream in;
		
		public SocketReader(InputStream in) {
			this.in = in;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
			
				BufferedInputStream reader = new BufferedInputStream(this.in);
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File("src/socket/download.txt")));
				
				int n = 0;
				while((n = reader.read()) != -1) {
					System.out.write(n);
					writer.write((char)n);
					writer.flush();
				}
				reader.close();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

