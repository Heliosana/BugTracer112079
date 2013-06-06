import java.sql.*;
import java.util.Properties;


public class MicSql {
	
	 Connection con   = null; 
	 Statement  stmt  = null; 
	 ResultSet  rs    = null; 
	MicSql() throws SQLException{
		 try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

		 con  = DriverManager.getConnection("jdbc:sqlserver://localhost;databasename=PZM1;user=Aismael;password=Nitram1;" ); 
			
		 
			
		 stmt = con.createStatement( ) ; 
		     rs   = stmt.executeQuery("Select * From Kunde;"); 
		  
		     while(rs.next( )){ 
		     System.out.println(rs.getString("Ort") + "," + rs.getString("PLZ")); 
		        } 
		    } catch ( Exception e ) {} 
		    finally { 
		    	if (rs   != null) rs.close ( ) ; 
		    	if (stmt != null) stmt.close ( ) ; 
		    	if (con  != null) con.close ( ); 
		       } 
		   } 
		 
	public static void main(String[] args) throws SQLException {
		new MicSql();
	}
	
}
