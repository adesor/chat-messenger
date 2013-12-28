import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatClient {
	JFrame f;
	JTextArea incoming;
	JTextField outgoing, field;
	JLabel label;
	BufferedReader reader;
	PrintWriter writer;
	FileWriter logger;
	Socket sock;
	String ip;
	String nick;
	boolean flag = true, flag2 = true;

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
	
	public void setNick(String n) {
		nick = n;
	}

	public void setDetails() {
		f = new JFrame("Enter your details");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		label = new JLabel("Enter your name:");
		field = new JTextField();
		field.addKeyListener(new FieldListener());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(label);
		panel.add(field);

		f.getContentPane().add(panel);
		f.setSize(200, 100);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setVisible(true);
		field.requestFocus();
	}

	public class FieldListener implements KeyListener {
		public void keyTyped(KeyEvent ev) {
			if(ev.getKeyChar() == '\n') {
				if(flag) {
					setNick(field.getText());
					flag = false;
					label.setText("Enter IP:");
					field.setText("    .    .    .    ");
					field.selectAll();
					field.requestFocus();
				}

				else {
					setIP(field.getText());
					f.dispose();

					flag2 = false;
				}
			}
		}

		public void keyPressed(KeyEvent ev) {}

		public void keyReleased(KeyEvent ev) {}
	}
	
	public static void main(String [] args) {
		ChatClient client = new ChatClient();

		client.setDetails();

		try {
			while(client.flag2)
				Thread.sleep(1);
		} catch(Exception ex) {}

//		client.setIP(args[0]);
//		client.SetNick(args[1]);
		client.go();
	}
}
