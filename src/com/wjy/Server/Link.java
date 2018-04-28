package com.wjy.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Link implements Runnable {
	Socket socket = null;
	// String name = null;
	ArrayList<Link> list = null;
	OutputStream out = null;
	BufferedReader read = null;
	InputStream in = null;
	int targetid;
	String name = null;
	String password = null;

	public Link(Socket socket, ArrayList<Link> list) {
		this.socket = socket;
		try {
			this.out = socket.getOutputStream();
			in = socket.getInputStream();
			this.read = new BufferedReader(new InputStreamReader(in));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.list = list;
	}

	final String d = "com.mysql.jdbc.Driver";
	final String url = "jdbc:mysql://localhost/Dinner";
	final String user = "root";
	final String password1 = "123456";
	Connection conn = null;
	PreparedStatement state = null;
	ResultSet rs = null;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String str = null;
		// 读取信息
		try {
			str = read.readLine();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println("服务器收到了" + str);
		// 如果是手机端连接
		if (str.equals("longin")) {
			System.out.println("手机客户端请求登录");
			String str1 = null;
			try {
				str1 = read.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			char[] c = str1.toCharArray();
			int one = -1;
			// 找到第一个标识符
			for (int i = 0; i < c.length; i++) {
				if (c[i] == '|') {
					one = i;
					break;
				}
			}
			System.out.println(one);
			name = new String(c, 0, one);
			System.out.println("收到手机传来的name:" + name);
			password = new String(c, one + 1, c.length - one - 1);
			System.out.println("收到手机传里的password:" + password);
			try {
				Class.forName(d);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				conn = DriverManager.getConnection(url, user, password1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				// 找到该id对应的password和name
				System.out.println("数据库");
				// System.out.println(id);
				state = conn
						.prepareStatement("select password,name from userinfo where name="
								+ name);
				rs = state
						.executeQuery("select password,name from userinfo where name="
								+ name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {

				// 如果rs的password等于用户输入password
				// System.out.println(rs.getString("password"));
				if (rs.next() && password.equals(rs.getString("password"))) {
					System.out.println(name + "登录success");
					// 向客户端发送成功
					out.write("OK\r".getBytes());
					// 数据库的id和name附给本服务器的id和name
				} else {
					System.out.print("fail");
					out.write("fail\r".getBytes());
					return;
				}

			} catch (SQLException | IOException e) {
				e.printStackTrace();

			}

		}
		if (str.equals("Register")) {
			System.out.println("手机客户端请求注册");
			String str1 = null;
			try {
				str1 = read.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			char[] c = str1.toCharArray();
			int one = -1;
			// 找到第一个标识符
			for (int i = 0; i < c.length; i++) {
				if (c[i] == '|') {
					one = i;
					break;
				}
			}
			System.out.println(one);
			name = new String(c, 0, one);
			System.out.println("收到手机传来的name:" + name);
			password = new String(c, one + 1, c.length - one - 1);
			System.out.println("收到手机传里的password:" + password);
			try {
				Class.forName(d);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				conn = DriverManager.getConnection(url, user, password1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				// 如果该账号已存在，则不允许注册
				PreparedStatement state1 = null;
				state = conn.prepareStatement("select password,name from userinfo where name=" + name);
				rs = state.executeQuery("select password,name from userinfo where name=" + name);
				
				
				if (!rs.next()) {
					state = conn
							.prepareStatement("insert into userinfo(name,password) values('"
									+ name + "','" + password + "')");
					int result = state.executeUpdate();
					if (result == 1) {
						out.write("OK\r".getBytes());
						return;
					} else {
						out.write("fail\r".getBytes());
						return;
					}
				}else{
					out.write("fail\r".getBytes());
					return;
				}

			} catch (Exception e) {
				e.printStackTrace();
				try {
					out.write("fail\r".getBytes());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
		}

	}
}
