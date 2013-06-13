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
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class TablePane extends JPanel implements ActionListener,
		TableModelListener {

	private DefaultTableModel tableModel;
	private JTable table;
	private Gui gui;
	private ResultSet rslt;
	private ResultSetMetaData rsltMetaData;
	private final JButton btntest = new JButton("Test");

	public TablePane(Gui gui, String tableName) {
		this.gui = gui;
		setName(tableName);
		initialize();

	}

	private void initialize() {
		setLayout(new BorderLayout(0, 0));

		tableModel = new DefaultTableModel();
		tableModel.addTableModelListener(this);
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);

		JPanel controlPanel = new JPanel();
		add(controlPanel, BorderLayout.EAST);
		GridBagLayout gbl_controlPanel = new GridBagLayout();
		gbl_controlPanel.columnWidths = new int[] { 50, 0 };
		gbl_controlPanel.rowHeights = new int[] { 20, 20, 20, 0, 0 };
		gbl_controlPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_controlPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, 0.0,
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
		gbc_btnReload.insets = new Insets(0, 0, 5, 0);
		btnReload.addActionListener(this);
		gbc_btnReload.gridx = 0;
		gbc_btnReload.gridy = 2;
		controlPanel.add(btnReload, gbc_btnReload);
		GridBagConstraints gbc_btntest = new GridBagConstraints();
		gbc_btntest.gridx = 0;
		gbc_btntest.gridy = 3;
		btntest.addActionListener(this);
		controlPanel.add(btntest, gbc_btntest);
	}

	private void test() throws SQLException {
		// TODO someting testing
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
		rslt = gui.statement.executeQuery("SELECT * FROM " + getName());
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

	private void state(String state) {
		gui.setState(state);
	}

	public void setSelectedRow(int ID) throws SQLException {
		boolean found = false;
		rslt.beforeFirst();
		while (rslt.next()) {
			if (rslt.getInt(1) == ID) {
				found = true;
				break;
			}
		}
		if (found) {
			// table. (rslt.getRow());
		} else {
			// TODO no refernced ID
		}
	}

	public void setSelectedID(TablePane refTablePane) throws SQLException {
		refTablePane.getName();
		refTablePane.getSelectedRowID();
		// TODO
	}

	public int getSelectedRowID() throws SQLException {
		int row = table.getSelectedRow();
		rslt.absolute(++row);
		return rslt.getInt(1);
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
			try {
				test();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
						// TODO Auto-generated catch block
						e1.printStackTrace();
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

	public void addListSelectionListener(ListSelectionListener listener) {
		table.getSelectionModel().addListSelectionListener(listener);
	}
}
