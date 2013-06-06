import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.Color;
import java.util.Vector;

public class SqlGui {

	private JFrame frame;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String[] titles = new String[] { "A", "B", "C", "D" };
	final DefaultTableModel model = new DefaultTableModel(titles, 0);

	// Das JTable initialisieren
	JTable tbl = new JTable(model);
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SqlGui window = new SqlGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SqlGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tbl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		frame.getContentPane().add(tbl, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(tbl);

		// Add the scroll pane to this panel.
		frame.add(scrollPane);

		JPanel panel = new JPanel();
		panel.setBorder(UIManager.getBorder("Button.border"));
		frame.getContentPane().add(panel, BorderLayout.NORTH);

		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnNewButton = new JButton("Kunde");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					KundeQuestion();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		panel.add(btnNewButton);

		JButton btnNewButton_2 = new JButton("Mitarbeiter");
		panel.add(btnNewButton_2);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					MitarbeiterQuestion();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		JButton btnNewButton_1 = new JButton("Abteilung");
		panel.add(btnNewButton_1);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					AbteilungQuestion();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	private void KundeQuestion() throws SQLException {
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			con = DriverManager
					.getConnection("jdbc:sqlserver://localhost;databasename=PZM1;user=Aismael;password=Nitram1;");
			stmt = con.createStatement();
			rs = stmt.executeQuery("Select * From Kunde;");

			model.setColumnCount(0);
			model.setNumRows(0);
			newHeader("KundeID");
			newHeader("KuName");
			newHeader("Ort");
			newHeader("PLZ");

			while (rs.next()) {
				String[] übergabe = { rs.getString("KundeID"),
						rs.getString("KuName"), rs.getString("Ort"),
						rs.getString("PLZ") };
				model.addRow(übergabe);
			}
		} catch (Exception e) {
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}
	}

	private void MitarbeiterQuestion() throws SQLException {
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			con = DriverManager
					.getConnection("jdbc:sqlserver://localhost;databasename=PZM1;user=Aismael;password=Nitram1;");
			stmt = con.createStatement();
			rs = stmt.executeQuery("Select * From Mitarbeiter;");

			model.setColumnCount(0);
			model.setNumRows(0);
			newHeader("MitarbeiterID");
			newHeader("FaName");
			newHeader("VoName");
			newHeader("Gehalt");
			newHeader("Abteilung");

			while (rs.next()) {
				String[] übergabe = { rs.getString("MitarbeiterID"),
						rs.getString("FaName"), rs.getString("VoName"),
						rs.getString("Gehalt"), rs.getString("Abteilung") };
				model.addRow(übergabe);
			}
		} catch (Exception e) {
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}

	}

	private void AbteilungQuestion() throws SQLException {
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			con = DriverManager
					.getConnection("jdbc:sqlserver://localhost;databasename=PZM1;user=Aismael;password=Nitram1;");
			stmt = con.createStatement();
			rs = stmt.executeQuery("Select * From Abteilung;");

			model.setColumnCount(0);
			model.setNumRows(0);
			newHeader("AbteilungsID");
			newHeader("AbteilungsName");

			while (rs.next()) {
				String[] übergabe = { rs.getString("AbteilungsID"),
						rs.getString("AbteilungsName") };
				model.addRow(übergabe);
			}
		} catch (Exception e) {
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		}

	}

	public static Vector createDataVector(String prefix, int size) {
		Vector vector = new Vector(size);
		for (int i = 0; i < size; i++)
			vector.add(prefix + " : " + size + " : " + i);

		return vector;
	}

	public void newHeader(String title) {
		int size = model.getRowCount();
		Vector newDatas = createDataVector("column", size);
		model.addColumn(title, newDatas);
	}

}
