package Bugtracer;

import java.sql.SQLException;

public class SQLExceptionHandler {
	private Gui gui;

	public SQLExceptionHandler(Gui gui) {
		this.gui = gui;

	}

	public void SQLExceptionInterpreter(SQLException exe) {
		gui.setState(exe.getMessage().substring(
				pointFirstpartendposition(exe.getMessage())));
		exe.printStackTrace();
	}

	private int pointFirstpartendposition(String message) {

		return message.indexOf(".") + 1;

	}

}
