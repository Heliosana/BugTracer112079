package Bugtracer.trash;

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
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import Bugtracer.Gui;

public class ControlPanel extends JPanel implements ActionListener,
		TableModelListener {

	private Gui gui;
	private DefaultTableModel tableModel;
	private String tableName;
	private ResultSet rslt;
	private ResultSetMetaData rsltMetaData;
	private JTable table;

	private ControlPanel(Gui gui) {
		this.gui = gui;
		initialize();
	}

	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 100 };
		gridBagLayout.rowHeights = new int[] { 50, 50, 50, 50, 0, 100 };
		gridBagLayout.columnWeights = new double[] { 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0,
				Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(this);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 0;
		add(btnAdd, gbc_btnAdd);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(this);
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.insets = new Insets(0, 0, 5, 0);
		gbc_btnDelete.gridx = 0;
		gbc_btnDelete.gridy = 1;
		add(btnDelete, gbc_btnDelete);

		JButton btnreload = new JButton("Reload");
		btnreload.addActionListener(this);
		GridBagConstraints gbc_btnLoad = new GridBagConstraints();
		gbc_btnLoad.insets = new Insets(0, 0, 5, 0);
		gbc_btnLoad.gridx = 0;
		gbc_btnLoad.gridy = 2;
		add(btnreload, gbc_btnLoad);

		JButton btnTest = new JButton("Test");
		btnTest.addActionListener(this);
		GridBagConstraints gbc_btnTest = new GridBagConstraints();
		gbc_btnTest.insets = new Insets(0, 0, 5, 0);
		gbc_btnTest.gridx = 0;
		gbc_btnTest.gridy = 3;
		add(btnTest, gbc_btnTest);
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

	private void reload() throws SQLException {
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
		// rslt = gui.statement.executeQuery("SELECT * FROM " + tableName);
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

	public void setActiveJTable(JTable table, String tableName) {
		state("switch to " + tableName);
		if (this.tableModel != null) {
			this.tableModel.removeTableModelListener(this);
		}
		this.table = table;
		this.tableModel = (DefaultTableModel) this.table.getModel();
		this.tableModel.addTableModelListener(this);
		this.tableName = tableName;
		try {
			reload();
		} catch (SQLException e) {
			gui.handleSQLException(e);
		}
	}

	@Override
	public void tableChanged(TableModelEvent event) {
		if (TableModelEvent.ALL_COLUMNS == event.getColumn()) {
			// rebuild the model
		} else if (gui.connected) {
			int column = event.getColumn();
			int row = event.getFirstRow();
			Object value = tableModel.getValueAt(row++, column++);
			try {
				rslt.absolute(row);
				Object old = rslt.getObject(column);
				if (old == null) {
					old = "";
				}
				// System.out.println(old + "\t-->\t" + value);
				if (!value.toString().equals(old.toString())) {
					// new and old values different
					rslt.updateObject(column, value);
					rslt.updateRow();
					state("update x: " + column + " ; y: " + row + " to: "
							+ value);
				}
			} catch (SQLException e) {
				if (e.getErrorCode() == 0) {
					// row not in set --> insert row
					try {
						rslt.moveToInsertRow();
						rslt.updateObject(column, value);
					} catch (SQLException e1) {
						gui.handleSQLException(e1);
					}
				} else {
					System.out.println(e.getErrorCode());
					e.printStackTrace();
				}
			} finally {
				// reload();
			}
		} else {
			state("can't alter db --> not connected");
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "Add") {
			try {
				insert();
			} catch (SQLException e) {
				if (e.getErrorCode() == 2627) {
					// Verletzung der PRIMARY KEY-Einschr�nkung
					try {
						reload();
					} catch (SQLException e1) {
						gui.handleSQLException(e1);
					}
					state("double PRIMARY KEY");
				} else if (e.getErrorCode() == 515) {
					// NULL as PRIMARY KEY
					try {
						reload();
					} catch (SQLException e1) {
						gui.handleSQLException(e1);
					}
					state("Null as PRIMARY KEY");
				} else {
					gui.handleSQLException(e);
				}
			}
		} else if (event.getActionCommand() == "Reload") {
			try {
				reload();
			} catch (SQLException e) {
				state("can't reload --> error: " + e.getErrorCode());
				gui.handleSQLException(e);
			}
		} else if (event.getActionCommand() == "Delete") {
			try {
				delete();
			} catch (SQLException e) {
				state("can't delete --> error: " + e.getErrorCode());
				gui.handleSQLException(e);
			}
		} else {
			System.out.println("sollte nicht kommen --< Controlpanel");
		}
	}

	private void state(String state) {
		gui.setState(state);
	}
}