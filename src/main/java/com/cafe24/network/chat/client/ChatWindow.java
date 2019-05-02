package com.cafe24.network.chat.client;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatWindow {

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pw;
	

	public ChatWindow(String name, Socket socket, BufferedReader br, PrintWriter pw) {
		frame = new Frame(name);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		this.socket = socket;
		this.br = br;
		this.pw = pw;
	}

	public void show() {
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent actionEvent ) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				finish();
				return;
			}
		});
		
		frame.setVisible(true);
		frame.pack();
		
		new Thread(new ChatClientWorkingThread()).start();
	}
	
	private void sendMessage() {
		String message = textField.getText();
		pw.println("message:"+message);
		
		textField.setText("");
		textField.requestFocus();

	}
	
	private void updateTextArea(String name, String message) {
		System.out.println(name);
		textArea.append(name +":"+message);
		textArea.append("\n");
	}
	
	private void finish() {
	
		try {		
			pw.println("quit");
			pw.flush();
			
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
			System.exit(0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class ChatClientWorkingThread implements Runnable{
		@Override
		public void run() {
			while(true) {
				try {
					String getMessage = br.readLine();
					
					if ("quit".equals(getMessage))break;
					
					//아이디:내용 
					String[] tokens = getMessage.split(":");
					updateTextArea(tokens[0],tokens[1]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
