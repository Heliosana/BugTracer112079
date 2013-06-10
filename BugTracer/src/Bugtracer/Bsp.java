package Bugtracer;

import java.sql.*;

public class Bsp {

	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	private String dbUsr = "Gui";
	private String dbPwd = "guipasswort";

	public Bsp() {

		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(
					"jdbc:jtds:sqlserver://127.0.0.1/BugTracer", dbUsr, dbPwd);
			stmt = con.createStatement();
			rs = stmt.executeQuery("Select * From Projekt;");
			System.out.println("try to print:");
			while (rs.next()) {
				System.out.println(rs.getString("Name") + ",\t"
						+ rs.getString("ProjektID"));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public static void main(String[] args) throws SQLException {
		new Bsp();
	}
}