package web;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class DialogAuthenticator extends Authenticator{
	private JDialog passwordDialog;
	private JLabel mainLabel = new JLabel("Please enter username and password");
	private JLabel userLabel = new JLabel("Username: ");
	private JLabel passwordLabel = new JLabel("Password: ");
	private JTextField usernameField = new JTextField(20);
	private JPasswordField passwordField = new JPasswordField(20);
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	
	public DialogAuthenticator() {
		this("", new JFrame());
	}
	
	public DialogAuthenticator(String username) {
		this(username, new JFrame());
	}
	
	public DialogAuthenticator(JFrame parent) {
		this("", parent);
	}
	
	public DialogAuthenticator(String username, JFrame parent) {
		this.passwordDialog = new JDialog(parent, true);
		Container pane = passwordDialog.getContentPane();
		pane.setLayout(new GridLayout(4, 1));
		pane.add(mainLabel);
		JPanel p2 = new JPanel();
		p2.add(userLabel);
		p2.add(usernameField);
		usernameField.setText(username);
		pane.add(p2);
		JPanel p3 = new JPanel();
		p3.add(passwordLabel);
		p3.add(passwordField);
		pane.add(p3);
		JPanel p4 = new JPanel();
		p4.add(okButton);
		p4.add(cancelButton);
		pane.add(p4);
		passwordDialog.pack();
		
		ActionListener aListener = new OKResponse();
		okButton.addActionListener(aListener);
		usernameField.addActionListener(aListener);
		passwordField.addActionListener(aListener);
		cancelButton.addActionListener(new CancelResponse());	
	}
	
	private void show() {
		String prompt = this.getRequestingPrompt();
		if (prompt == null) {
			String site = this.getRequestingSite().getHostName();
			String protocol = this.getRequestingProtocol();
			int port = this.getRequestingPort();
			if (site != null && protocol != null) {
				prompt = protocol + "://" + site;
				if (port > 0) {
					prompt += " : " + port;
				}
			}
			else {
				prompt = "";
			}
		}
		
		mainLabel.setText("Please enter username and password for " + prompt + ": ");
		passwordDialog.pack();
		passwordDialog.show();
	}
	
	PasswordAuthentication response = null;
	
	class OKResponse implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			passwordDialog.hide();
			char[] password = passwordField.getPassword();
			String userName = usernameField.getText();
			passwordField.setText("");
			response = new PasswordAuthentication(userName, password);
		}
	}
	
	class CancelResponse implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			passwordDialog.hide();
			passwordField.setText("");
			response = null;
		}
	}
	
	public PasswordAuthentication getPasswordAutentication() {
		this.show();
		return this.response;
	}
	
	
	
}