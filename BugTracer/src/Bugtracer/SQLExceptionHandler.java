package Bugtracer;

import java.sql.SQLException;

public class SQLExceptionHandler {
	private Gui gui;

	public SQLExceptionHandler(Gui ui){
		this.gui=gui;
		
	}
	
	public void SQLExceptionInterpreter(SQLException exe)
	{
		gui.setState(exe.getMessage());
		exe.printStackTrace();
	}

}
