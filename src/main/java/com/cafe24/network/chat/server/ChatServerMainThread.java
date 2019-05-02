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
			
			
			//이부분 조심한다. 리눅스에서는 현재 호스트네임을 lx01로 영구적으로 바꾸었다. 
			//하지만 여기는 localhost라는 이름을 찾는다.
			// # hostname localhost 로 일시적으로 변경하고 사용하자...
			InetAddress inetAddress = InetAddress.getLocalHost();
			String localhost = inetAddress.getHostAddress(); // 맥은 제대로 나옴. 윈도우는 vm ip가 나오는 듯.
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
