/**
 * 
 */
package Bugtracer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

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
	private String dbURL = "jdbc:jtds:sqlserver://127.0.0.1/BugTracer";
	private String dbUsr = "Gui";
	private String dbPwd = "guipasswort";
	private boolean connected;
	private Gui gui;

	public BugTracer() {
		gui= new Gui(this);
		if (connect(dbUsr,dbPwd) != null) {
			disconnect();
			System.out.println("connection avaiable");
		}
	}

	public Statement connect(String dbUsr,String dbPwd) {
		return connect(dbUsr,dbPwd, dbURL);
	}

	private Statement connect(String dbUsr,String dbPwd, String dbURL) {
		System.out.println(dbURL + dbUsr + dbPwd);
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
		} catch (ClassNotFoundException ce) {
			// TODO Auto-generated catch block
			
			ce.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(dbURL, dbUsr, dbPwd);
		} catch (SQLException e) {
			System.out.println("no conection established");
			e.printStackTrace();
		}
		try {
			stmt = conn.createStatement();
			System.out.print("connected\t");
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
		System.out.println("-->\tdisconnected");
	}
}
