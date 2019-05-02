package com.cafe24.network.chat.server;

import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServerMainThread {
	public static final int PORT = 8088;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket();
			InetAddress inetAddress = InetAddress.getLocalHost();
			String localhost = inetAddress.getHostAddress(); // 맥은 제대로 나옴. vm ip가 나오는 듯.
			System.out.println(localhost); 
			
			//Writer Pool
			List<Writer> listWriters = new ArrayList<Writer>();
			
			// binding
			serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT));
			log("server start...[port:" + PORT + "]");

			while (true) {
				Socket socket = serverSocket.accept(); // blocking

				Thread thread = new ChatServerWorkingThread(socket, listWriters);
				thread.start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null && !serverSocket.isClosed())
					serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void log(String log) {
		System.out.println("[server#" + Thread.currentThread().getId() + "] " + log);

	}
}
