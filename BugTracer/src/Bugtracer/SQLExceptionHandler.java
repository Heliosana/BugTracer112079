package Bugtracer;

import java.sql.SQLException;

public class SQLExceptionHandler {
	private Gui gui;

	public SQLExceptionHandler(Gui gui) {
		this.gui = gui;

	}
<<<<<<< HEAD

	public void SQLExceptionInterpreter(SQLException exe) {
		gui.setState(exe.getMessage().substring(
				pointFirstpartendposition(exe.getMessage())));
		exe.printStackTrace();
	}

	private int pointFirstpartendposition(String message) {

		return message.indexOf(".") + 1;

=======
	
	public void SQLExceptionInterpreter(SQLException exe)
	{
		switch (exe.getErrorCode()) {
		case 2627:
			gui.setState("double PRIMARY KEY");
			break;

		case 515:
			gui.setState("Null as PRIMARY KEY");
			break;
			
		default:
			new ErrorFrame(exe);
			break;
		}
		//gui.setState(exe.getMessage().substring(pointFirstpartendposition(exe.getMessage())));
		
		//exe.printStackTrace();
>>>>>>> better exception handler
	}
	
//private int pointFirstpartendposition(String message) {
//		
//		return message.indexOf(".")+1;
//		
//	}

}
