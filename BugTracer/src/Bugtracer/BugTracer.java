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
	private String dbUsr = "Gui";
	private String dbPwd = "guipasswort";
	private String dbURL = "jdbc:jtds:sqlserver://127.0.0.1/BugTracer";

	public BugTracer() {
		if (connect() != null) {
			disconnect();
			System.out.println("connection avaiable");
		}
		new Gui(this);
	}

	public Statement connect() {
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
		} catch (SQLException e) {
			System.out.println("can't create statement on connection");
			e.printStackTrace();

		}
		System.out.print("connected\t");
		return stmt;
	}

	// private ResultSet selectUserdata(Statement stmt) {
	// ResultSet rs = stmt.executeQuery("Select * From UserData;");
	// model.setColumnCount(0);
	// model.setNumRows(0);
	// newHeader("KundeID");
	// newHeader("KuName");
	// newHeader("Ort");
	// newHeader("PLZ");
	//
	// while (rs.next()) {
	// String[] übergabe = { rs.getString("UserdataID"),
	// rs.getString("Nick"), rs.getString("Name"),
	// rs.getString("Adresse"), rs.getString("Telefon") };
	// model.addRow(übergabe);
	// }
	//
	// if (rs != null)
	// rs.close();
	// return rs;
	// }

	public void disconnect() {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("-->\tdisconnected");
	}
}
