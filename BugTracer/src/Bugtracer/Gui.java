package Bugtracer;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;

import Bugtracer.trash.SQLExceptionHandler;

public class Gui implements ActionListener {

	public boolean debug = false;

	private BugTracer bugTracer;
	private JTextField loginnameTextfield;
	private JPasswordField pwField;
	public JFrame frame;
	public Connection connection;
	public boolean connected = false;
	private JPanel loginPanel;
	private JTextField serverNameTextfield;
	private JTextField serverIPTextfield;
	private JLabel statepane;
	private JTabbedPane tabbedPanel;
	private SQLExceptionHandler sqlexceptionhandler;

	public Gui(BugTracer bugTracer) {
		setUI();
		this.bugTracer = bugTracer;
		initialize();
		createTabs();
		setdisconnected();
		sqlexceptionhandler = new SQLExceptionHandler(this);
		setState("gui started");
		frame.setVisible(true);
	}

	private void setUI() {
		try {
			UIManager
					.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private void addTab(String name) {
		tabbedPanel.addTab(name, null, new ReferencePane(this, name), null);
	}

	private void createTabs() {

		addTab("UserData");
		addTab("Adresse");
		addTab("Dev");
		addTab("Tester");
		addTab("Projekt");
		addTab("Release");
		addTab("Bug");
		addTab("Ticket");
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Bugtracer");
		frame.setBounds(0, 0, 1005, 710);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 1101, 0 };
		gridBagLayout.rowHeights = new int[] { 30, 737, 20, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0,
				Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		loginPanel = new JPanel();
		loginPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_loginPanel = new GridBagConstraints();
		gbc_loginPanel.anchor = GridBagConstraints.NORTH;
		gbc_loginPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_loginPanel.insets = new Insets(0, 0, 5, 0);
		gbc_loginPanel.gridx = 0;
		gbc_loginPanel.gridy = 0;
		frame.getContentPane().add(loginPanel, gbc_loginPanel);
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

		pwField = new JPasswordField();
		pwField.setText("guipasswort");
		loginPanel.add(pwField);
		pwField.setColumns(10);
		pwField.addActionListener(this);

		JButton btnLogin = new JButton("Login");
		btnLogin.setHorizontalAlignment(SwingConstants.RIGHT);
		btnLogin.addActionListener(this);
		loginPanel.add(btnLogin);

		JButton btnLogout = new JButton("Logout");
		btnLogout.addActionListener(this);
		loginPanel.add(btnLogout);

		tabbedPanel = new JTabbedPane(SwingConstants.TOP);
		tabbedPanel.setBorder(new LineBorder(new Color(0, 0, 0)));

		GridBagConstraints gbc_tableTabbedPanel = new GridBagConstraints();
		gbc_tableTabbedPanel.fill = GridBagConstraints.BOTH;
		gbc_tableTabbedPanel.insets = new Insets(0, 0, 5, 0);
		gbc_tableTabbedPanel.gridx = 0;
		gbc_tableTabbedPanel.gridy = 1;
		frame.getContentPane().add(tabbedPanel, gbc_tableTabbedPanel);

		statepane = new JLabel();
		statepane.setHorizontalAlignment(SwingConstants.CENTER);
		statepane.setFont(new Font("Tahoma", Font.PLAIN, 13));
		statepane.setBackground(Color.black);
		statepane.setForeground(Color.BLACK);
		GridBagConstraints gbc_statepane = new GridBagConstraints();
		gbc_statepane.anchor = GridBagConstraints.SOUTHEAST;
		gbc_statepane.gridx = 0;
		gbc_statepane.gridy = 2;
		frame.getContentPane().add(statepane, gbc_statepane);
	}

	@SuppressWarnings("deprecation")
	public void login() {
		logout();
		try {
			connection = bugTracer.connect(serverIPTextfield.getText(),
					serverNameTextfield.getText(),
					loginnameTextfield.getText(), pwField.getText());
		} catch (SQLException e) {
			handleSQLException(e);
		}
		ReferencePane toFire = (ReferencePane) tabbedPanel
				.getSelectedComponent();
		toFire.stateChanged(new ChangeEvent(tabbedPanel));
	}

	private void logout() {
		bugTracer.disconnect();

	}

	public void setdisconnected() {
		connected = false;
		setState("disconnected");
		loginPanel.setBackground(Color.RED);
	}

	public void setconnected() {
		connected = true;
		setState("connected");
		loginPanel.setBackground(Color.GREEN);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "Logout") {
			logout();
		} else
			login();
	}

	public void setState(String state) {
		statepane.setText("state:  " + state + "...");
	}

	public void addtabbedPanelListener(ReferencePane referencePane) {
		tabbedPanel.addChangeListener(referencePane);
	}

	public void handleSQLException(SQLException e) {
		new ErrorDialog(e, this);
	}
}
