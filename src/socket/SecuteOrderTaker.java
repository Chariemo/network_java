package socket;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class SecuteOrderTaker {
	
	public final static int DEFAULT_PORT = 7000;
	public final static String algorithm = "SSL";
	
	public static void main(String[] args) {
		
		int port = DEFAULT_PORT;
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
				if (port < 0 || port > 65536) {
					System.out.println("Port must between 0 and 65536");
					return;
				}
			} catch (NumberFormatException exception) {
				
			}
		}
		
		try {
			
			SSLContext context = SSLContext.getInstance(algorithm);
			
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			KeyStore keyStore = KeyStore.getInstance("JKS");
			
			char[] password = "nibuzhidao".toCharArray();
			keyStore.load(new FileInputStream("src/socket/default.keys"), password);
			keyManagerFactory.init(keyStore, password);
			context.init(keyManagerFactory.getKeyManagers(), null, null);
			
			SSLServerSocketFactory factory = context.getServerSocketFactory();
			
			SSLServerSocket sslServerSocket = (SSLServerSocket) factory.createServerSocket(port);
			
			String[] supported = sslServerSocket.getSupportedCipherSuites();
			String[] anonCipherSuitesSupported = new String[supported.length];
			int numAnonCipherSuitesSupported = 0;
			for (int i = 0; i < supported.length; i++) {
				if (supported[i].indexOf("_anon_") > 0) {
					anonCipherSuitesSupported[numAnonCipherSuitesSupported++] = supported[i];
				}
			}
			
			String[] oldEnabled = sslServerSocket.getEnabledCipherSuites();
			String[] newEnabled = new String[oldEnabled.length + numAnonCipherSuitesSupported];
			System.arraycopy(oldEnabled, 0, newEnabled, 0, oldEnabled.length);
			System.arraycopy(anonCipherSuitesSupported, 0, newEnabled, oldEnabled.length, numAnonCipherSuitesSupported);
			sslServerSocket.setEnabledCipherSuites(newEnabled);
			
			try {
				while (true) {
					
					Socket connection = sslServerSocket.accept();
					
					InputStream reader = connection.getInputStream();
					int c;
					while ((c = reader.read()) != -1) {
						System.out.write(c);
					}
					reader.close();
					connection.close();
				} 
			} catch (IOException e) {
				System.err.println(e);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		} catch (KeyManagementException exception) {
			exception.printStackTrace();
		} catch (KeyStoreException exception) {
			exception.printStackTrace();
		} catch (NoSuchAlgorithmException exception) {
			exception.printStackTrace();
		} catch (java.security.cert.CertificateException exception) {
			exception.printStackTrace();
		} catch (UnrecoverableKeyException exception) {
			exception.printStackTrace();
		}
	}
}
