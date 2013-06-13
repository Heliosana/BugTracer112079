package Bugtracer;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class ReferencePane extends JPanel implements ChangeListener {

	private Gui gui;
	private String mainTableName;
	private TablePane mainTablePane;
	private ResultSet rslt;
	private ResultSetMetaData rsltMetaData;
	private boolean initialLoad = true;

	public ReferencePane(Gui gui, String name) {
		this.gui = gui;
		mainTableName = name;
		gui.addtabbedPanelListener(this);
		setLayout(new BorderLayout(0, 0));
		mainTablePane = new TablePane(gui, mainTableName);
		this.add(mainTablePane);
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		JTabbedPane source = ((JTabbedPane) event.getSource());
		if (source.getSelectedComponent().equals(this)) {
			reload();			
		}
	}

	private void reload() {
		if (gui.connected) {
			firstLoad();
			try {
				mainTablePane.reload();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void firstLoad() {
		if (initialLoad) {
			try {
				if (rslt != null) {
					rslt.close();
				}
				rslt = gui.statement.executeQuery("SELECT * FROM "
						+ mainTableName);
				rsltMetaData = rslt.getMetaData();
				initialLoad = false;
				state("load sql table from db");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void test() throws SQLException {
		System.out.print("\n-->"+mainTableName+": ");
		for (int i = 1; i <= rsltMetaData.getColumnCount(); i++) {
//			System.out.println(rsltMetaData.getCatalogName(i));
//			System.out.print(rsltMetaData.getColumnClassName(i));
//			System.out.print(rsltMetaData.getTableName(i));
			System.out.println(rsltMetaData.getColumnType(i));
		}
	}

	private void state(String state) {
		gui.setState(state);
	}
}
