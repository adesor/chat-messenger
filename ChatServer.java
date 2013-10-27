package chat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	ArrayList clientOutputStreams;
	
	public void tellEveryone(String message) {
		Iterator i = clientOutputStreams.iterator();
		while (i.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) i.next();
				writer.println(message);
				writer.flush();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public class ClientHandler implements Runnable {
		BufferedReader reader;
		Socket sock;
		
		public ClientHandler(Socket clientSocket) {
			try {
				sock = clientSocket;
				InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("read: " + message);
					tellEveryone(message);
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	public void go() {
		clientOutputStreams = new ArrayList();
		try {
			ServerSocket serverSock = new ServerSocket(5000);
			
			while(true) {
				Socket clientSocket = serverSock.accept();
				System.out.println(clientSocket.getClass());
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer);
				
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				System.out.println("Got a connection");
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String [] args) {
		ChatServer server = new ChatServer();
		server.go();
	}
}
