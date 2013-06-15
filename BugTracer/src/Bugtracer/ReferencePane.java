package Bugtracer;

import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ReferencePane extends JPanel implements ChangeListener,
		ListSelectionListener {

	private Gui gui;
	private String mainTableName;
	private TablePane mainTablePane;
	private ResultSet rslt;
	private ResultSetMetaData rsltMetaData;
	private boolean initialLoad = true;
	private TreeMap<String, TablePane> referenceTable;
	private Statement statement;

	public ReferencePane(Gui gui, String name) {
		this.gui = gui;
		this.mainTableName = name;
		gui.addtabbedPanelListener(this);
		setLayout(new GridLayout(0, 1, 0, 0));

		mainTablePane = new TablePane(gui, mainTableName);
		this.add(mainTablePane);
		mainTablePane.addListSelectionListener(this);

	}

	private void reload() {
		if (gui.connected) {
			firstLoad();
			try {
				mainTablePane.reload();
				if (referenceTable != null) {
					for (TablePane reference : referenceTable.values()) {
						reference.reload();
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				gui.handleSQLException(e);
			}
		}
	}

	private void firstLoad() {
		if (initialLoad) {
			if (gui.connection != null) {
				try {
					statement = gui.connection.createStatement();
					if (rslt != null) {
						rslt.close();
					}
					rslt = statement
							.executeQuery("SELECT * FROM GETREFERENCES('"
									+ mainTableName + "')");
					rsltMetaData = rslt.getMetaData();
					initialLoad = false;
					state("load sql reference for " + mainTableName);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					gui.handleSQLException(e);
				}
				try {
					;
					JPanel refencePane = new JPanel(new GridLayout(1, 0));
					referenceTable = new TreeMap<String, TablePane>();
					while (rslt.next()) {

						this.add(refencePane);
						TablePane referencedTable = new TablePane(gui,
								rslt.getString(1), rslt.getString(2));
						refencePane.add(referencedTable);
						referenceTable.put(rslt.getString(3), referencedTable);
					}
					// }
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					gui.handleSQLException(e);
				}
			}
		}

	}

	private void test() throws SQLException {
		System.out.print("\n-->" + mainTableName + ": ");
		for (int i = 1; i <= rsltMetaData.getColumnCount(); i++) {
			// System.out.println(rsltMetaData.getCatalogName(i));
			// System.out.print(rsltMetaData.getColumnClassName(i));
			// System.out.print(rsltMetaData.getTableName(i));
			System.out.println(rsltMetaData.getColumnType(i));
		}
	}

	private void state(String state) {
		gui.setState(state);
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		JTabbedPane source = ((JTabbedPane) event.getSource());
		if (source.getSelectedComponent().equals(this)) {
			reload();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		// TODO ListSelectionEvent
		event.getSource();
	}
}
