package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

public class ClientWindow {
	
	// Server port
	private static final int PORT = 4565;
	
	//Window Diemnsions
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	private JFrame frame;
	private JTextField messageField;
	private JTextField nameField;
	private static JTextArea textArea = new JTextArea();
	
	private Client client;
	private String clientName;
	

	// Main method
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					ClientWindow window = new ClientWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Constructor - Initialize window
	public ClientWindow() {
		initialize();
		
		clientName = JOptionPane.showInputDialog("Enter Name: ");
		nameField.setText(clientName + ":");
		client = new Client(clientName, "localhost" , PORT);
	}

	// Initializes window and adds event listeners
	private void initialize() {
		frame = new JFrame();
		
		// Sets focus on input field when open window
		frame.addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent arg0) {
				messageField.requestFocus();
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// Sends user disconnected message when closed window
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.send("\\dis:" + clientName);
			}
		});
		
		// Init JFrame
		frame.setTitle("Chat Engine");
		frame.setResizable(false);
		frame.setBounds(100, 100, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Init text area to show messages
		textArea.setEditable(false);
		textArea.setBackground(Color.DARK_GRAY);
		textArea.setForeground(Color.WHITE);
		textArea.setFont(new Font("Sans Serif",Font.BOLD,20));
		
		// Scrolls to bottom when message overflow
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		//Scrollable window
		JScrollPane scrollPane = new JScrollPane(textArea);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		// Backgrond of input field
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		
		// Sends message on "Enter" press
		messageField = new JTextField();
		messageField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!messageField.getText().equals("")) {
					client.send(messageField.getText());
					messageField.setText("");
				}
			}
		});
		
		// Shows the client name on window
		nameField = new JTextField();
		nameField.setHorizontalAlignment(SwingConstants.RIGHT);
		nameField.setEditable(false);
		panel.add(nameField);
		nameField.setColumns(10);
		
		// Init message input field
		messageField.setPreferredSize(new Dimension(50, 30));
		panel.add(messageField);
		messageField.setColumns(50);
		
		// Send button -> sends on click event
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(e -> {
			if(!messageField.getText().equals("")) {
				client.send(messageField.getText());
				messageField.setText("");
			}
		});
		panel.add(btnNewButton);
		
		// Opens window in the center of display
		frame.setLocationRelativeTo(null);
	}
	
	/*
	 * Message types-
	 * 0 - message
	 * 1 - user connected
	 * 2 - user disconnected
	 */
	
	// Adds messages to the current text field
	public static void printToConsole(String message, int type) {
		if(type == 0) {
//			textArea.setFont(new Font("Sans Serif",Font.PLAIN,20));
			textArea.setText(textArea.getText() + message + "\n");
		} else if(type == 1) {
//			textArea.setFont(new Font("Sans Serif",Font.BOLD,20));
			textArea.setText(textArea.getText() + message + "\n");
		} else if(type == 2) {
//			textArea.setFont(new Font("Sans Serif",Font.BOLD,20));
			textArea.setText(textArea.getText() + message + "\n");
		}
		
	}

}