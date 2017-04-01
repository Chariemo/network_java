package socket;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class WhoisGUI extends JFrame{
	
	private JTextField  searchString = new JTextField(30);
	private JTextArea names = new JTextArea(15, 80);
	private JButton findButton = new JButton("Find");
	private ButtonGroup searchIn = new ButtonGroup();
	private ButtonGroup searchFor = new ButtonGroup();
	private JCheckBox exactMatch = new JCheckBox("Exact Match", true);
	private JTextField chosenServer = new JTextField();
	
	private Whois server;
	
	public WhoisGUI(Whois whois) {
		
		super("whois");
		this.server = whois;
		Container pane = this.getContentPane();
		Font font = new Font("Monospaced", Font.PLAIN, 12);
		names.setFont(font);
		names.setEditable(false);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1, 1, 10, 10));
		JScrollPane jScrollPane = new JScrollPane(names);
		centerPanel.add(jScrollPane);
		pane.add("Center", centerPanel);
		
		JPanel northPanel = new JPanel();
		JPanel northPanelTop = new JPanel();
		northPanelTop.setLayout(new FlowLayout(FlowLayout.LEFT));
		northPanelTop.add(new JLabel("Whois"));
		northPanelTop.add("North", searchString);
		northPanelTop.add(exactMatch);
		northPanelTop.add(findButton);
		northPanel.setLayout(new BorderLayout(2, 1));
		northPanel.add("North", northPanelTop);
		JPanel northPanelBottom = new JPanel();
		northPanelBottom.setLayout(new GridLayout(1, 3, 5, 5));
		northPanelBottom.add(initRecordType());
		northPanelBottom.add(initSearchFields());
		northPanelBottom.add(initServerChoice());
		northPanel.add("Center", northPanelBottom);
		
		pane.add("North", northPanel);
		
		ActionListener aListener = new LookupNames();
		findButton.addActionListener(aListener);
		searchString.addActionListener(aListener);
		
	}
	
	private JPanel initRecordType() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(6, 2, 5, 2));
		panel.add(new JLabel("Search for: "));
		panel.add(new JLabel(""));
		
		JRadioButton any = new JRadioButton("Any", true);
		any.setActionCommand("Any");
		searchFor.add(any);
		panel.add(any);
		
		panel.add(this.makeradioButton("Network"));
		panel.add(this.makeradioButton("Person"));
		panel.add(this.makeradioButton("Host"));
		panel.add(this.makeradioButton("Domain"));
		panel.add(this.makeradioButton("organization"));
		panel.add(this.makeradioButton("Group"));
		panel.add(this.makeradioButton("Gateway"));
		panel.add(this.makeradioButton("ASN"));
		
		return panel;
	}
	
	private JRadioButton makeradioButton(String label) {
		
		JRadioButton button = new JRadioButton(label, false);
		button.setActionCommand(label);
		searchFor.add(button);
		return button;
	
	}
	
	private JRadioButton makeSearchInRadioButton(String label) {
		
		JRadioButton button = new JRadioButton(label, false);
		button.setActionCommand(label);
		searchIn.add(button);
		return button;
	
	}
	
	private JPanel initSearchFields() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(6, 1, 5, 2));
		panel.add(new JLabel("Search In: "));
		
		JRadioButton all = new JRadioButton("All", true);
		all.setActionCommand("All");
		searchIn.add(all);
		panel.add(all);
		
		panel.add(this.makeSearchInRadioButton("Name"));
		panel.add(this.makeSearchInRadioButton("MailBox"));
		panel.add(this.makeSearchInRadioButton("Handle"));
		
		return panel;
		
	}
	
	private JPanel initServerChoice() {
		
		final JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(6, 1, 5, 2));
		panel.add(new JLabel("Search At:"));
		
		chosenServer.setText(server.getHost().getHostName());
		panel.add(chosenServer);
		chosenServer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					InetAddress newhost = InetAddress.getByName(chosenServer.getText());
					Whois newServer = new Whois(newhost);
					server = newServer;
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(panel, exception.getMessage(),
							"Alert", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		return panel;
		
	}
	
	class LookupNames implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Whois.SearchIn group = Whois.SearchIn.ALL;
			Whois.SearchFor category = Whois.SearchFor.ANY;
			
			String searchforLabel = searchFor.getSelection().getActionCommand();
			String searchInLabel = searchIn.getSelection().getActionCommand();
			if (searchInLabel.equals("Name")) {
				group = Whois.SearchIn.NAME;
			}
			else if (searchInLabel.equals("Handle")) {
				group = Whois.SearchIn.HANDLE;
			}
			else if (searchInLabel.equals("Mailbox")) {
				group = Whois.SearchIn.MAILBOX;
			}
			
			if (searchforLabel.equals("Network")) {
				category = Whois.SearchFor.NETWORK;
			}
			else if (searchforLabel.equals("Person")) {
				category = Whois.SearchFor.PERSON;
			}
			else if (searchforLabel.equals("Host")) {
				category = Whois.SearchFor.HOST;
			}
			else if (searchforLabel.equals("Domain")) {
				category = Whois.SearchFor.DOMAIN;
			}
			else if (searchforLabel.equals("Organization")) {
				category = Whois.SearchFor.ORGANIZATION;
			}
			else if (searchforLabel.equals("Group")) {
				category = Whois.SearchFor.GROUP;
			}
			else if (searchforLabel.equals("Gateway")) {
				category = Whois.SearchFor.GATEWAY;
			}
			else if (searchforLabel.equals("ASN")) {
				category = Whois.SearchFor.ASN;
			}
			
			Thread connect = new Thread(new Connect(category, group));
			connect.start();
		}
		
	}
	
	public static void main(String[] args) {
		
		try {
			Whois server = new Whois();
			WhoisGUI gui = new WhoisGUI(server);
			gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			gui.pack();
			EventQueue.invokeLater(new FrameShower(gui));
		} catch (UnknownHostException exception) {
			JOptionPane.showMessageDialog(null, "Could not locate default host " + Whois.DEFAULT_HOST,
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	private class Connect implements Runnable {
		
		private Whois.SearchFor category;
		private Whois.SearchIn group;
		
		public Connect(Whois.SearchFor category, Whois.SearchIn group) {
			// TODO Auto-generated constructor stub
			this.category = category;
			this.group = group;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				System.out.println("1");
				names.setText("");
				server.setHost(chosenServer.getText());
				String result = server.lookUnNames(searchString.getText(), category, group, exactMatch.isSelected());
				names.setText(result);
				System.out.println("111");
			} catch (IOException exception) {
				JOptionPane.showMessageDialog(WhoisGUI.this, exception.getMessage(),
						"Lookup Failed", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
	
	private static class FrameShower implements Runnable {
		
		private final Frame frame;
		
		public FrameShower(Frame frame) {
			// TODO Auto-generated constructor stub
			this.frame =frame;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			frame.setVisible(true);
		}
	}
	
}
