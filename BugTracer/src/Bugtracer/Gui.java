package Bugtracer;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class Gui implements ActionListener, ChangeListener {

	private BugTracer bugTracer;
	private JTextField loginnameTextfield;
	private JPasswordField pwField;
	private JFrame frame;
	private boolean connected = false;
	private JPanel loginPanel;
	private Statement statement;
	private JTextField serverNameTextfield;
	private JTextField serverIPTextfield;
	private JLabel statepane;
	private JTabbedPane tableTPanel;
	public DefaultTableModel testModel = new DefaultTableModel();
	private DefaultTableModel models[] = new DefaultTableModel[10];
	private ControlPanel controlPanel;

	public Gui(BugTracer bugTracer) {
		this.bugTracer = bugTracer;
		initialize();
		setState("gui started");
		frame.setVisible(true);
		tableTPanel.setSelectedIndex(1);
		tableTPanel.setSelectedIndex(0);

	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Bugtracer");
		frame.setBounds(0, 0, 1005, 710);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 1101, 10, 0 };
		gridBagLayout.rowHeights = new int[] { 33, 737, 14, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0,
				Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		loginPanel = new JPanel();
		loginPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_loginPanel = new GridBagConstraints();
		gbc_loginPanel.gridwidth = 2;
		gbc_loginPanel.anchor = GridBagConstraints.NORTH;
		gbc_loginPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_loginPanel.insets = new Insets(0, 0, 5, 0);
		gbc_loginPanel.gridx = 0;
		gbc_loginPanel.gridy = 0;
		frame.getContentPane().add(loginPanel, gbc_loginPanel);
		loginPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

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

		tableTPanel = new JTabbedPane(JTabbedPane.TOP);
		tableTPanel.addChangeListener(this);

		GridBagConstraints gbc_tableTabbedPanel = new GridBagConstraints();
		gbc_tableTabbedPanel.fill = GridBagConstraints.BOTH;
		gbc_tableTabbedPanel.insets = new Insets(0, 0, 5, 5);
		gbc_tableTabbedPanel.gridx = 0;
		gbc_tableTabbedPanel.gridy = 1;
		frame.getContentPane().add(tableTPanel, gbc_tableTabbedPanel);

		addTab("TestTable1");
		addTab("TestTable2");

		controlPanel = new ControlPanel(this, testModel);
		controlPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_controlPanel = new GridBagConstraints();
		gbc_controlPanel.gridheight = 2;
		gbc_controlPanel.anchor = GridBagConstraints.WEST;
		gbc_controlPanel.fill = GridBagConstraints.VERTICAL;
		gbc_controlPanel.insets = new Insets(0, 0, 5, 0);
		gbc_controlPanel.gridx = 1;
		gbc_controlPanel.gridy = 1;
		frame.getContentPane().add(controlPanel, gbc_controlPanel);
		GridBagLayout gbl_controlPanel = new GridBagLayout();
		gbl_controlPanel.columnWidths = new int[] { 60, 0 };
		gbl_controlPanel.rowHeights = new int[] { 23, 0, 0 };
		gbl_controlPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_controlPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		controlPanel.setLayout(gbl_controlPanel);

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

	private synchronized void addTab(String tabName) {
		DefaultTableModel model = new DefaultTableModel();
		JTable table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane sPane = new JScrollPane(table);
		sPane.setName(tabName);
		tableTPanel.addTab(tabName, null, sPane, null);
		models[tableTPanel.getTabCount() - 1] = model;
	}

	@SuppressWarnings("deprecation")
	private void login() {
		logout();
		try {
			statement = bugTracer.connect(serverIPTextfield.getText(),
					serverNameTextfield.getText(),
					loginnameTextfield.getText(), pwField.getText());
		} catch (IllegalArgumentException e) {
			new ErrorFrame(e);
		}

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

	@Override
	public void stateChanged(ChangeEvent state) {
		String tableName= tableTPanel.getSelectedComponent().getName();
		int i = tableTPanel.getSelectedIndex();
		if (models[i] != null) {
			controlPanel.setActiveModel(models[i],tableName);
		}
	}

}
