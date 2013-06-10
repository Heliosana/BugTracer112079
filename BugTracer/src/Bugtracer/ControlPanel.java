package Bugtracer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;

public class ControlPanel extends JPanel implements ActionListener {

	private Gui gui;
	private JTabbedPane tPane;
	private DefaultTableModel tableModel;
	private String tableName;

	public ControlPanel(Gui gui, DefaultTableModel tableModel) {
		this.gui = gui;
		this.tableModel = tableModel;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 72, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
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
		gbc_btnTest.gridx = 0;
		gbc_btnTest.gridy = 1;
		add(btnTest, gbc_btnTest);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "Test") {
			this.tableModel.setColumnCount(5);
			this.tableModel.setRowCount(4);
		} else {
			this.tableModel.setColumnCount(10);
			this.tableModel.setRowCount(50);
		}
	}

	public void setActiveModel(DefaultTableModel tableModel, String tableName) {
		gui.setState("switch to " + tableName);
		this.tableModel = tableModel;
		this.tableName = tableName;
	}

}
