package com.cafe24.network.chat.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

import com.cafe24.network.chat.server.ChatServerMainThread;

public class ChatClientApp {
	
	public static String SERVER_IP = "192.168.1.5";

	public static void main(String[] args) {
		String name = null;
		Scanner sc = new Scanner(System.in);
		Socket socket = null;
		
		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(SERVER_IP, ChatServerMainThread.PORT));
			log("[client] connected");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
			
			
			while( true ) {
				
				System.out.println("대화명을 입력하세요.");
				System.out.print(">>> ");
				name = sc.nextLine();
				
				if (name.isEmpty() == false ) {
					break;
				}
				
				System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
			}
			
			pw.println("join:"+name);
			pw.flush();
			new ChatWindow(name, socket, br, pw).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (sc != null) sc.close();
		}
		
	}
	private static void log(String log) {
		System.out.println("[client]"+log);
	}

}
