package Bugtracer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

public class ErrorDialog extends JDialog implements ActionListener {

	ErrorDialog(Exception exc) {
		// exc.printStackTrace();
		getContentPane().setBackground(Color.RED);
		setTitle("Error");
		this.setBounds(0, 0, 328, 273);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);

		JPanel panel = new JPanel();
		panel.setBackground(Color.RED);
		getContentPane().add(panel, BorderLayout.SOUTH);

		JButton btnOk = new JButton("Okay");
		btnOk.addActionListener(this);
		panel.add(btnOk);

		JLabel lblHead = new JLabel("Es ist ein Fehler Aufgetreten:");
		lblHead.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblHead, BorderLayout.NORTH);

		JTextPane errorTextPane = new JTextPane();
		errorTextPane.setEditable(false);
		errorTextPane.setBackground(Color.BLACK);
		errorTextPane.setForeground(Color.WHITE);
		getContentPane().add(errorTextPane, BorderLayout.CENTER);
		//errorTextPane.setText(exc.toString());
		errorTextPane.setText(exc.getMessage().substring(pointFirstpartendposition(exc.getMessage())));

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.dispose();
	}
	
private int pointFirstpartendposition(String message) {
		
		return message.indexOf(".")+1;
		
	}
}
