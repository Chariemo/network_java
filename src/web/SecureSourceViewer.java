package web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;

public class SecureSourceViewer {
	
	public static void main(String[] args) {
		
		Authenticator.setDefault(new DialogAuthenticator());
		
		for (int i = 0; i < args.length; i++) {
			try {
				URL url = new URL(args[i]);
				Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
				int c;
				while ((c = reader.read()) != -1) {
					System.out.print((char)c);
				}
			} catch (MalformedURLException exception) {
				// TODO: handle exception
				System.out.println(args[i] + " is not a pareseable URL");
			} catch (IOException exception) {
				System.out.println(exception);
			}
			
			System.out.println();
		}
		System.exit(0);
	}
}
