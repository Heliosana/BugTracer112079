package Bugtracer;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class TablePane extends JPanel implements ActionListener {

	private String tableName;
	private DefaultTableModel tableModel;
	private JTable table;
	private Gui gui;
	private ResultSet rslt;
	private ResultSetMetaData rsltMetaData;

	public TablePane(Gui gui, String tableName) {
		this.gui = gui;
		this.tableName = tableName;
		initialize();

	}

	private void initialize() {
		setLayout(new BorderLayout(0, 0));

		tableModel = new DefaultTableModel();
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);

		JPanel controlPanel = new JPanel();
		add(controlPanel, BorderLayout.EAST);
		GridBagLayout gbl_controlPanel = new GridBagLayout();
		gbl_controlPanel.columnWidths = new int[] { 50, 0 };
		gbl_controlPanel.rowHeights = new int[] { 20, 20, 20, 0 };
		gbl_controlPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_controlPanel.rowWeights = new double[] { 1.0, 1.0, 1.0,
				Double.MIN_VALUE };
		controlPanel.setLayout(gbl_controlPanel);

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(this);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 0;
		controlPanel.add(btnAdd, gbc_btnAdd);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(this);
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.insets = new Insets(0, 0, 5, 0);
		gbc_btnDelete.gridx = 0;
		gbc_btnDelete.gridy = 1;
		controlPanel.add(btnDelete, gbc_btnDelete);

		JButton btnReload = new JButton("Reload");
		GridBagConstraints gbc_btnReload = new GridBagConstraints();
		btnReload.addActionListener(this);
		gbc_btnReload.gridx = 0;
		gbc_btnReload.gridy = 2;
		controlPanel.add(btnReload, gbc_btnReload);
	}

	private void test() {
		// TODO Auto-generated method stub
	}

	private void insert() throws SQLException {
		if (gui.connected) {
			rslt.moveToInsertRow();
			rslt.insertRow();
			reload();
		} else
			state("can't add --> not connected");
	}

	private void delete() throws SQLException {
		if (gui.connected) {
			int row = table.getSelectedRow();
			if (row != -1) {
				rslt.absolute(++row);
				try {
					rslt.deleteRow();
					reload();
				} catch (SQLException e) {
					state("cant delete insert row");
				}
			} else {
				state("can't delete --> no row selected");
			}
		} else
			state("can't delete --> not connected");
	}

	 public void reload() throws SQLException {
		if (gui.connected) {
			loadSql();
			rebuildModel();
		} else
			state("can't reload --> not connected");
	}

	private void loadSql() throws SQLException {
		state("load sql table from db");
		if (rslt != null) {
			rslt.close();
		}
		rslt = gui.statement.executeQuery("SELECT * FROM " + tableName);
		rsltMetaData = rslt.getMetaData();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void rebuildModel() throws SQLException {
		state("reload model");
		Vector columnNames = new Vector();
		Vector data = new Vector();
		rslt.beforeFirst();
		int columns = rsltMetaData.getColumnCount();
		for (int i = 1; i <= columns; i++) {
			columnNames.addElement(rsltMetaData.getColumnName(i));
		}
		while (rslt.next()) {
			Vector row = new Vector(columns);
			for (int i = 1; i <= columns; i++) {
				row.addElement(rslt.getObject(i));
			}
			data.addElement(row);
		}
		data.addElement(new Vector(columns));
		// System.out.println(data.toString()+columnNames.toString());
		tableModel.setDataVector(data, columnNames);

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "Add") {
			try {
				insert();
			} catch (SQLException e) {
				if (e.getErrorCode() == 2627) {
					// Verletzung der PRIMARY KEY-Einschränkung
					try {
						reload();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					state("double PRIMARY KEY");
				} else if (e.getErrorCode() == 515) {
					// NULL as PRIMARY KEY
					try {
						reload();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					state("Null as PRIMARY KEY");
				} else {
					System.out.println(e.getErrorCode());
					e.printStackTrace();
				}
			}
		} else if (event.getActionCommand() == "Reload") {
			try {
				reload();
			} catch (SQLException e) {
				state("can't reload --> error: " + e.getErrorCode());
				// e.printStackTrace();
			}
		} else if (event.getActionCommand() == "Delete") {
			try {
				delete();
			} catch (SQLException e) {
				state("can't delete --> error: " + e.getErrorCode());
				// e.printStackTrace();
			}
		} else {
			test();
		}
	}

	private void state(String state) {
		gui.setState(state);
	}
}
