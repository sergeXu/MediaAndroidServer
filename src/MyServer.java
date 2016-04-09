
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.net.*;

public class MyServer {

	public static boolean isFramed = true;

	public static void main(String[] args) {
		
		if (isFramed) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					JFrame frame = new ServerFrame();
					frame.setTitle("ServerFrame");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
				}
			});
		}

		//
		else {
			workThreads w= new workThreads();
			try {
				w.serverRun();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("IOException in main\n");
			}
		}
	}
}
