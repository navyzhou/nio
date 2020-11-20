package com.yc.nio.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * NIO客户端
 * @author navy
 * @date 2020年11月20日
 * @email haijunzhou@hnit.edu.cn
 */
public class NioClient {
	public static void main(String[] args) throws IOException {
		NioClient client = new NioClient();
		client.start();
	}
	
	/**
	 * 启动的方法
	 * @throws IOException 
	 */
	@SuppressWarnings("resource")
	public void start() throws IOException {
		// 1、连接服务器端
		SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
		
		// 2、接收服务器端的信息 -> 必须新开一个线程
		Selector selector = Selector.open();
		channel.configureBlocking(false); // 设置为非阻塞时
		channel.register(selector, SelectionKey.OP_READ); // 监听可读事件
		new Thread(new NioClientHandler(selector)).start();
		
		// 3、向服务器端发送请求数据
		Scanner sc = new Scanner(System.in);
		String line = "";
		while(sc.hasNextLine()) {
			line = sc.nextLine();
			if (line != null && !"".equals(line)) {
				// 发送数据到服务器
				channel.write(Charset.forName("UTF-8").encode(line));
			}
		}
	}
}
