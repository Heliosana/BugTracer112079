package Bugtracer;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.sql.SQLException;

public class ReferencePane extends JPanel implements ChangeListener {

	private Gui gui;
	private String mainTableName;
	private TablePane mainTablePane;

	public ReferencePane(Gui gui, String name) {
		this.gui = gui;
		mainTableName = name;
		gui.addtabbedPanelListener(this);
		setLayout(new BorderLayout(0, 0));
		mainTablePane= new TablePane(gui, mainTableName);
		this.add(mainTablePane);
	}

	@Override
	public void stateChanged(ChangeEvent event) {
	JTabbedPane source=	((JTabbedPane) event.getSource());
	if(source.getSelectedComponent().equals(this));
		reload();
	}

	private void reload() {
		try {
			mainTablePane.reload();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void state(String state) {
		gui.setState(state);
	}
}
