package Bugtracer;

import java.awt.Color;
import java.awt.Component;
import java.sql.ResultSetMetaData;
import java.util.TreeMap;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableColumnRenderer extends DefaultTableCellRenderer {
	private TreeMap<Integer, Color> columnColors = new TreeMap<Integer, Color>();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		DefaultTableCellRenderer superRenderer = (DefaultTableCellRenderer) super
				.getTableCellRendererComponent(table, value, isSelected,
						hasFocus, row, column);
		Color color = columnColors.get(column);
		setForeground(Color.BLACK);
		if (!isSelected) {
			superRenderer.setBackground(color);
		} else {
			superRenderer.setForeground(color);
		}

		return superRenderer;
	}

	public void setColumn(int column, boolean autoIncrement, int nullable) {
		Color color = Color.white;
		if (autoIncrement) {
			color = Color.lightGray.darker();
		} else if (nullable == ResultSetMetaData.columnNoNulls) {
			color = Color.lightGray;
		}

		columnColors.put(column - 1, color);

	}
}
