/**
 * 
 */
package Bugtracer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
	
		// if (connect() != null) {
		// disconnect();
		// gui.setState("connection avaiable");
		// }
	}

	

	private Statement connect() {
		return connect(dbURL, dbName, dbUser, dbPwd);
	}

	public Statement connect(String dbURL, String dbName, String dbUsr,
			String dbPwd) throws IllegalArgumentException {
		// System.out.println(dbURL + dbUsr + dbPwd);
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
		} catch (ClassNotFoundException ce) {
			// TODO Auto-generated catch block

			ce.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + dbURL
					+ "/" + dbName, dbUsr, dbPwd);
		} catch (SQLException e) {
			gui.setState("failed to connect");
			throw new IllegalArgumentException(
					"\ncouldn't establish connection\n --> check you logon values:\ndbUrl: "
							+ dbURL + "\ndbName: " + dbName + "\ndbUsername: "
							+ dbUsr + "\ndbPassword: " + dbPwd, e);
		}
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			gui.setconnected();
		} catch (SQLException e) {
			System.out.println("can't create statement on connection");
			e.printStackTrace();
		}
		return stmt;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
