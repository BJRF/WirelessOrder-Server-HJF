package com.wjy.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class netlink extends Thread{

		//创建队列
		public ArrayList<Link> list = new ArrayList<Link>();

		public  void link(int port) throws IOException {
			System.out.println("服务器打开");
			ServerSocket server = new ServerSocket(port);
			while (true) {
				Socket socket = server.accept();		
				Link link = new Link(socket, list);
				list.add(link);
				new Thread(link).start();
			}
		}
		

		public static void main(String[] args) {
			try {
				netlink link=new netlink();
				link.start();
				link.link(8888);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
