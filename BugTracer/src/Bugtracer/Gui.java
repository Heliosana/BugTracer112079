package Bugtracer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JSeparator;
import java.awt.CardLayout;
import java.sql.Statement;
import javax.swing.JTabbedPane;

public class Gui implements ActionListener {

	private BugTracer bugTracer;
	private JTextField loginnameTextfield;
	private JTextField pwTextfield;
	private JFrame frame;
	private boolean connected = false;
	private JPanel loginPanel;
	private Statement statement;
	private JTextField serverNameTextfield;
	private JTextField serverIPTextfield;

	public Gui(BugTracer bugTracer) {
		this.bugTracer = bugTracer;
		initialize();
		frame.setVisible(true);

	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Bugtracer");
		frame.setBounds(0, 0, 1127, 822);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		loginPanel = new JPanel();
		frame.getContentPane().add(loginPanel, BorderLayout.NORTH);
		loginPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblServerip = new JLabel("Server-IP:");
		loginPanel.add(lblServerip);
		
		serverIPTextfield = new JTextField();
		serverIPTextfield.setText("127.0.0.1:1433");
		loginPanel.add(serverIPTextfield);
		serverIPTextfield.setColumns(10);
		
		JLabel lblServername = new JLabel("Servername:");
		loginPanel.add(lblServername);
		
		serverNameTextfield = new JTextField();
		serverNameTextfield.setText("BugTracer");
		serverNameTextfield.setColumns(10);
		loginPanel.add(serverNameTextfield);

		JLabel lblLoginname = new JLabel("Loginname:");
		loginPanel.add(lblLoginname);

		loginnameTextfield = new JTextField();
		loginnameTextfield.setText("Gui");
		loginPanel.add(loginnameTextfield);
		loginnameTextfield.setColumns(10);
		loginnameTextfield.addActionListener(this);

		JLabel lblPassword = new JLabel("Password:");
		loginPanel.add(lblPassword);

		pwTextfield = new JTextField();
		pwTextfield.setText("guipasswort");
		loginPanel.add(pwTextfield);
		pwTextfield.setColumns(10);
		pwTextfield.addActionListener(this);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setHorizontalAlignment(SwingConstants.RIGHT);
		btnLogin.addActionListener(this);
		loginPanel.add(btnLogin);

		JButton btnLogout = new JButton("Logout");
		btnLogout.addActionListener(this);
		loginPanel.add(btnLogout);

		JPanel controlPanel = new JPanel();
		frame.getContentPane().add(controlPanel, BorderLayout.EAST);
		
		JTabbedPane tableTabbedPanel = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tableTabbedPanel, BorderLayout.CENTER);
	}

	private void login() {
		logout();
		try {
		statement = bugTracer.connect(serverIPTextfield.getText()+"/"+serverNameTextfield.getText(),loginnameTextfield.getText(),
			pwTextfield.getText());
		} catch (IllegalArgumentException e) {
			new ErrorFrame(e);
		}
		
	}

	private void logout() {
		bugTracer.disconnect();
	}

	public void setdisconnected() {
		connected = false;
		loginPanel.setBackground(Color.RED);
	}

	public void setconnected() {
		connected = true;
		loginPanel.setBackground(Color.GREEN);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "Logout") {
			logout();
		} else
			login();
	}
}
