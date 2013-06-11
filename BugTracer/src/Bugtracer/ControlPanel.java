package Bugtracer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JLabel;

public class ControlPanel extends JPanel implements ActionListener,
		TableModelListener {

	private Gui gui;
	private DefaultTableModel tableModel;
	private String tableName;
	private ResultSet rslt;
	private ResultSetMetaData rsltMetaData;

	public ControlPanel(Gui gui, DefaultTableModel tableModel) {
		this.gui = gui;
		this.tableModel = tableModel;
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

		JButton btnTest = new JButton("Test");
		btnTest.addActionListener(this);
		GridBagConstraints gbc_btnTest = new GridBagConstraints();
		gbc_btnTest.insets = new Insets(0, 0, 5, 0);
		gbc_btnTest.gridx = 0;
		gbc_btnTest.gridy = 1;
		add(btnTest, gbc_btnTest);

		JButton btnreload = new JButton("Reload");
		btnreload.addActionListener(this);
		GridBagConstraints gbc_btnLoad = new GridBagConstraints();
		gbc_btnLoad.insets = new Insets(0, 0, 5, 0);
		gbc_btnLoad.gridx = 0;
		gbc_btnLoad.gridy = 2;
		add(btnreload, gbc_btnLoad);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.insets = new Insets(0, 0, 5, 0);
		gbc_btnSave.gridx = 0;
		gbc_btnSave.gridy = 3;
		add(btnSave, gbc_btnSave);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "Add") {
			// TODO
			state("add content");
		} else if (event.getActionCommand() == "Reload") {
			state("reload content");
			reload();
		} else if (event.getActionCommand() == "Save") {
			// TODO
			state("save content");
		} else {
			test();
			state("test content");
		}
	}

	public void setActiveModel(DefaultTableModel tableModel, String tableName) {
		state("switch to " + tableName);
		if (tableModel != null) {
			this.tableModel.removeTableModelListener(this);
		}
		this.tableModel = tableModel;
		this.tableModel.addTableModelListener(this);
		this.tableName = tableName;
		reload();
	}

	private void reload() {
		if (gui.connected) {
			loadSql();
			rebuildModel();
		} else
			state("not connected to reload");
	}

	private void loadSql() {
		state("load sql table from db");
		if (rslt != null) {
			try {
				rslt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			rslt = gui.statement.executeQuery("SELECT * FROM " + tableName);
			rsltMetaData = rslt.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void rebuildModel() {
		state("reload model");
		Vector columnNames = new Vector();
		Vector data = new Vector();
		try {
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// System.out.println(data.toString()+columnNames.toString());
			tableModel.setDataVector(data, columnNames);
		}
	}

	private void test() {

	}

	private void state(String state) {
		gui.setState(state);
	}

	@Override
	public void tableChanged(TableModelEvent event) {
		if (event.ALL_COLUMNS == event.getColumn()) {
			// rebuild the model
		} else {
			int column = event.getColumn() + 1;
			int frow = event.getFirstRow() + 1;
			Object value = tableModel.getValueAt(frow - 1, column - 1);
			try {
				rslt.absolute(frow);
				System.out.println(rslt.getObject(column));
				if (value != rslt.getObject(column)) {
					rslt.updateObject(column, value);
					rslt.updateRow();
					state("column " + column + " & row " + frow
							+ " updated to " + value);
				}
			} catch (SQLException e) {
				try {
					rslt.moveToInsertRow();
					rslt.updateObject(column, value);
					rslt.insertRow();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} finally {
				reload();
			}
		}
	}
}