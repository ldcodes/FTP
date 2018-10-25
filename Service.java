package ftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Service {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ServerSocket socket;
		try {
			socket = new ServerSocket(21);
			while(true){
				Socket s=socket.accept();
				ClientThread clientThread = new ClientThread(s);
				clientThread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
