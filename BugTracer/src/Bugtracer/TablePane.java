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
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class TablePane extends JPanel implements ActionListener,
		TableModelListener, ListSelectionListener {

	private DefaultTableModel tableModel;
	private JTable table;
	private Gui gui;
	private ResultSet rslt;
	private ResultSetMetaData rsltMetaData;
	private final JButton btntest = new JButton("Test");
	private String referencedColumnName;
	private String parentColumnName;
	private int lastSelectedRow;
	private int insertrow;

	public TablePane(Gui gui, String tableName) {
		this.gui = gui;
		setName(tableName);
		initialize();
	}

	/**
	 * @wbp.parser.constructor
	 */
	public TablePane(Gui gui, String tableName, String referencedColumnName,
			String parentColumnName) {
		this(gui, tableName);
		this.referencedColumnName = referencedColumnName;
		this.parentColumnName = parentColumnName;
	}

	private void createTable() {
		tableModel = new DefaultTableModel();
		tableModel.addTableModelListener(this);
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoscrolls(true);
		addListSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
	}

	private void initialize() {
		setLayout(new BorderLayout(0, 0));
		setBorder(new TitledBorder(null, "", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		createTable();

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
		new ErrorDialog(null);
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
					gui.handleSQLException(e);
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
		if (gui.connection != null) {
			Statement statement = gui.connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			state("load sql table from db");
			if (rslt != null) {
				rslt.close();
			}
			rslt = statement.executeQuery("SELECT * FROM " + getName());
			rsltMetaData = rslt.getMetaData();
			rslt.beforeFirst();
		}
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
		rslt.last();
		insertrow = rslt.getRow() + 1;
		data.addElement(new Vector(columns));
		// System.out.println(data.toString()+columnNames.toString());
		tableModel.setDataVector(data, columnNames);
		table.getSelectionModel().setSelectionInterval(0, lastSelectedRow);

	}

	private void state(String state) {
		gui.setState(state);
	}

	public void setSelectedRow(Integer ID) throws SQLException {
		rslt.beforeFirst();
		while (rslt.next()) {
			if (ID.equals(rslt.getInt(referencedColumnName))) {
				table.getSelectionModel().setSelectionInterval(0,
						rslt.getRow() - 1);
			}
		}
	}

	public void setSelectedRow(TablePane parentTablePane) throws SQLException {
		try {
			setSelectedRow(parentTablePane
					.getSeletedReference(parentColumnName));
		} catch (SQLException e) {
			if (e.getErrorCode() == 0) {

				table.getSelectionModel()
						.setSelectionInterval(0, insertrow - 1);
			} else
				throw e;
		}
	}

	private Integer getSeletedReference(String parentColumnName)
			throws SQLException {
		rslt.absolute(table.getSelectedRow() + 1);
		return rslt.getInt(parentColumnName);
	}

	public void setReferencedID(TablePane referencedTable) throws SQLException {
		updateReference(table.getSelectedRow() + 1,
				referencedTable.getParentColumnName(),
				referencedTable.getSelectedRowID());
	}

	private void updateReference(int row, String parentColumnName,
			int selectedRowID) throws SQLException {
		try {
			rslt.absolute(row);
			rslt.updateObject(parentColumnName, selectedRowID);
			rslt.updateRow();
			state("update x: " + parentColumnName + " ; y: " + row + " to: "
					+ selectedRowID);

		} catch (SQLException e) {
			if (e.getErrorCode() == 0) {
				// row not in set --> insert row
				rslt.moveToInsertRow();
				rslt.updateObject(parentColumnName, selectedRowID);

			} else {
				// System.out.println(e.getErrorCode());
				// e.printStackTrace();
				gui.handleSQLException(e);
			}
		} finally {
			// TODO fix stackoverflow
			// this.rebuildModel();
		}
	}

	public int getSelectedRowID() throws SQLException {
		int row = table.getSelectedRow();
		rslt.absolute(++row);
		return rslt.getInt(referencedColumnName);
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
						// e1.printStackTrace();
						gui.handleSQLException(e1);
					}
					// state("double PRIMARY KEY");
					gui.handleSQLException(e);

				} else if (e.getErrorCode() == 515) {
					// NULL as PRIMARY KEY
					try {
						reload();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						// e1.printStackTrace();
						gui.handleSQLException(e1);
					}
					// state("Null as PRIMARY KEY");
					gui.handleSQLException(e);
				} else {
					// System.out.println(e.getErrorCode());
					// e.printStackTrace();
					gui.handleSQLException(e);
				}
			}
		} else if (event.getActionCommand() == "Reload") {
			try {
				reload();
			} catch (SQLException e) {
				// state("can't reload --> error: " + e.getErrorCode());
				// e.printStackTrace();
				state("can't reload --> error:");
				gui.handleSQLException(e);
			}
		} else if (event.getActionCommand() == "Delete") {
			try {
				delete();
			} catch (SQLException e) {
				// state("can't delete --> error: " + e.getErrorCode());
				// e.printStackTrace();
				state("can't delete --> error:");
				gui.handleSQLException(e);
			}
		} else {
			try {
				test();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				gui.handleSQLException(e);
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
						// e1.printStackTrace();
						gui.handleSQLException(e1);
					}
				} else {
					// System.out.println(e.getErrorCode());
					// e.printStackTrace();
					gui.handleSQLException(e);
				}
			} finally {
				// reload();
			}
		} else {
			state("can't alter db --> not connected");
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		this.lastSelectedRow = event.getFirstIndex();
	}

	public void addListSelectionListener(ListSelectionListener listener) {
		table.getSelectionModel().addListSelectionListener(listener);
	}

	public ListSelectionModel getListSelectionModel() {
		return table.getSelectionModel();
	}

	public String getReferencedColumnName() {
		return referencedColumnName;
	}

	public String getParentColumnName() {
		return parentColumnName;
	}
}
