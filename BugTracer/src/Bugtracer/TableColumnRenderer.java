package Bugtracer;

import java.awt.Color;
import java.awt.Component;
import java.util.TreeMap;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableColumnRenderer extends DefaultTableCellRenderer {
		private TreeMap<Integer,Color> columnColors=new TreeMap<Integer,Color>();
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
			this.setBackground(Color.RED);
            return this;
        }
		public void setColumn(int column, Color back) {
			
		}
}
