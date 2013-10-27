package chat;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatClient {
	JTextArea incoming;
	JTextField outgoing;
	BufferedReader reader;
	PrintWriter writer;
	FileWriter logger;
	Socket sock;
	String ip;
	String nick;
	
	public void setUpNetworking() {
		try {
			sock = new Socket(ip, 5000);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			logger = new FileWriter("chatLog.txt");
			logger.write("Session started!\n");
			System.out.println("Connected!");
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public class SendButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				writer.println(nick + ": " + outgoing.getText());
				writer.flush();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			outgoing.setText("");
			outgoing.requestFocus();
		}
	}
	
	public class IncomingReader implements Runnable {
		@Override
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("Read: " + message);
					incoming.append(message + "\n");
					logger.write(message + "\n");
				}
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}	
	}
	public void go() {
		JFrame frame = new JFrame("Chat Client");
		JPanel mainPanel = new JPanel();
		incoming = new JTextArea(15, 50);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		qScroller.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});
		outgoing = new JTextField(20);
		outgoing.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					try {
						writer.println(nick + ": " + outgoing.getText());
						writer.flush();
					}catch (Exception ex) {
						ex.printStackTrace();
					}
					outgoing.setText("");
					outgoing.requestFocus();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}
		});
		
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		
		setUpNetworking();
		
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(600, 320);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public String getIP() {
		return ip;
	}
	
	public void setIP(String i) {
		ip = i;
	}
	
	public String getNick() {
		return nick;
	}
	
	public void SetNick(String n) {
		nick = n;
	}
	
	public static void main(String [] args) {
		ChatClient client = new ChatClient();
		client.setIP(args[0]);
		client.SetNick(args[1]);
		client.go();
	}
}
