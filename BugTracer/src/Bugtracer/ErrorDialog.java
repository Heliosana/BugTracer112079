package Bugtracer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

public class ErrorDialog extends JDialog implements ActionListener {

	private JTextPane errorTextPane;
	private Gui gui;

	public ErrorDialog(SQLException e, Gui gui) {
		this.gui = gui;
		getContentPane().setBackground(Color.RED);
		setTitle("Error");
		this.setBounds(0, 0, 500, 200);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initialize();
		handleException(e);
		this.setModal(true);
		this.setVisible(true);
	}

	private void handleException(SQLException e) {
		if (gui.debug) {
			System.out.println(e.getErrorCode());
			e.printStackTrace();
		}
		errorTextPane.setText("ErrorCode: " + e.getErrorCode() + "\n"
				+ e.getMessage());
	}

	private void initialize() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.RED);
		getContentPane().add(panel, BorderLayout.SOUTH);

		JButton btnOk = new JButton("Okay");
		btnOk.addActionListener(this);
		panel.add(btnOk);

		JLabel lblHead = new JLabel("Es ist ein Fehler Aufgetreten:");
		lblHead.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblHead, BorderLayout.NORTH);

		errorTextPane = new JTextPane();
		errorTextPane.setEditable(false);
		errorTextPane.setBackground(Color.WHITE);
		errorTextPane.setForeground(Color.BLACK);
		getContentPane().add(errorTextPane, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.dispose();
	}

}
