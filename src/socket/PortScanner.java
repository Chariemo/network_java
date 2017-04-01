package socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PortScanner {
	
	public static void main(String[] args) {
		
		String host = "localhost";
		ExecutorService executorService = Executors.newCachedThreadPool();
		if (args.length > 0) {
			host = args[0];
		}
		Scanner reader = new Scanner(System.in);
		int port_max = 0;
		System.out.println("from port: ");
		port_max = reader.nextInt();
		try {
			InetAddress inetAddress = InetAddress.getByName(host);
			for (int i = 1; i < port_max; i++) {
				executorService.execute(new Connect(inetAddress, i));
			}
			executorService.shutdown();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static class Connect implements Runnable {

		private int port;
		private InetAddress host;
		
		public Connect(InetAddress host, int port) {
			// TODO Auto-generated constructor stub
			this.host = host;
			this.port = port;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Socket socket = null;
			try {
				 socket = new Socket(host, port);
				System.out.println("There is a server on port " + port + " of " + host);
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
	}
}
