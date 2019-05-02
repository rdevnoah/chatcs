package com.cafe24.network.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class ChatServerWorkingThread extends Thread {

	private Socket socket;
	private String nickname;
	private List<Writer> listWriters;

	public ChatServerWorkingThread(Socket socket, List<Writer> listWriters) {
		this.socket = socket;
		this.listWriters = listWriters;
	}

	@Override
	public void run() {
		BufferedReader br = null;
		PrintWriter pw = null;
		InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

		String remoteHostAddress = inetRemoteSocketAddress.getAddress().getHostAddress();
		int remotePort = inetRemoteSocketAddress.getPort();
		ChatServerMainThread.log("connected by client [" + remoteHostAddress + ":" + remotePort + "]");

		try {
			// 스트림 열기
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);

			// 요청 처리
			while (true) {
				String request = br.readLine();
				if (request == null) {
					ChatServerMainThread.log("closed by Client");
					doQuit(pw);
					break;
				}
				// 프로토콜 분석
				String[] tokens = request.split(":");
				if (("join").equals(tokens[0])) {
					doJoin(tokens[1], pw);
				} else if (("message").equals(tokens[0])) {
					doMessage(tokens[1]);
				} else if (("quit").equals(tokens[0])) {
					doQuit(pw);
				} else {
					ChatServerMainThread.log("에러: 알 수 없는 요청 (" + tokens[0] + ")");
				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void doJoin(String nickName, Writer writer) {
		this.nickname = nickName;

		String data = nickName + "님이 입장하셨습니다.";
		broadcast(data);

		// writer pool에 저장
		addWriter(writer);

		// ack
		((PrintWriter) writer).println("join:ok");
		((PrintWriter) writer).flush();

	}

	private void doMessage(String message) {
		broadcast(message);
	}

	private void doQuit(Writer writer) {
		removeWriter(writer);

		String data = nickname + "님이 퇴장하였습니다.";
		broadcast(data);
	}

	private void removeWriter(Writer writer) {
		synchronized (listWriters) {
			listWriters.remove(writer);
		}
	}

	private void addWriter(Writer writer) {
		synchronized (listWriters) {
			listWriters.add(writer);
		}
	}

	private void broadcast(String data) {
		synchronized (listWriters) {
			for (Writer writer : listWriters) {
				PrintWriter pw = (PrintWriter) writer;
				pw.println(nickname+":"+data);
				pw.flush();
			}
		}
		System.out.println(nickname + ":"+ data);
	}

}
