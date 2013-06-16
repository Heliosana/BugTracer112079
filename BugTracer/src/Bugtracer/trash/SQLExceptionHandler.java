package Bugtracer.trash;

import java.sql.SQLException;

import Bugtracer.ErrorDialog;
import Bugtracer.Gui;

public class SQLExceptionHandler {
	private Gui gui;

	public SQLExceptionHandler(Gui gui) {
		this.gui = gui;
	}

	public void SQLExceptionInterpreter(SQLException e) {
		if (gui.debug) {
			System.out.println(e.getErrorCode());
			e.printStackTrace();
		}
		switch (e.getErrorCode()) {
		case 2627:
			gui.setState("double PRIMARY KEY");
			break;
		case 515:
			gui.setState("Null as PRIMARY KEY");
			break;
		default:
			new ErrorDialog(e, gui);
			break;
		}
	}
}
