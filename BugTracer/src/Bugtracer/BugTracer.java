/**
 * 
 */
package Bugtracer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Heliosana
 * 
 */
public class BugTracer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BugTracer();
	}

	private Connection conn;
	private Statement stmt;

	// db connection values
	private String dbURL = "127.0.0.1";
	private String dbName = "BugTracer";
	private String dbUser = "Gui";
	private String dbPwd = "guipasswort";
	private boolean connected;
	private Gui gui;

	public BugTracer() {
		gui = new Gui(this);
		gui.login();

		// if (connect() != null) {
		// disconnect();
		// gui.setState("connection avaiable");
		// }
	}

	private Connection connect() throws SQLException {
		return connect(dbURL, dbName, dbUser, dbPwd);
	}

	public Connection connect(String dbURL, String dbName, String dbUsr,
			String dbPwd) throws SQLException {
		// System.out.println(dbURL + dbUsr + dbPwd);
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			gui.setState("no sql driver loaded");
		}
		conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + dbURL
				+ "/" + dbName, dbUsr, dbPwd);
		gui.setState("connected");
		gui.setconnected();
		return conn;
	}

	public void disconnect() {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
			gui.setdisconnected();
		} catch (SQLException e) {
			gui.handleSQLException(e);
		}
	}
}
