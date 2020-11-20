package com.yc.nio.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO服务器端
 * @author navy
 * @date 2020年11月18日
 * @email haijunzhou@hnit.edu.cn
 */
public class NioServer {
	// 缓冲区大小  
	private static final int BUFFERSIZE = 1024;  
	// 超时时间，单位毫秒  
	private static final int TIMEOUT = 3000;  
	// 本地监听端口  
	private static final int PORT = 8888;

	public static void main(String[] args) throws IOException {
		NioServer server = new NioServer();
		server.start();
	}

	/**
	 * 启动服务器的方法
	 * @throws IOException 
	 */
	public void start() throws IOException {
		// 1、创建Selector
		Selector selector = Selector.open(); // 创建选择器

		// 2、通过ServerSocketChannel创建channel
		ServerSocketChannel channel = ServerSocketChannel.open(); // 打开通道

		// 3、为channel并绑定监听端口
		channel.bind(new InetSocketAddress(PORT));

		// 4、将channel设置为非阻塞模式
		channel.configureBlocking(false); // 设置为非阻塞模式  

		/*
		 * 5、将channel注册到Selector上，监听连接事件
		 * SelectionKey.OP_ACCEPT:用于套接字接收连接操作的就绪
		 * SelectionKey.OP_CONNECT:用于套接字连接操作的就绪
		 * SelectionKey.OP_READ:用于读取操作的就绪
		 * SelectionKey.OP_WRITE:用于写入操作的就绪
		 */

		channel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("服务器启动成功，占用端口：" + PORT);

		// 6、循环调用Selector的select方法，检测就绪情况
		while(true) {
			// 等待某信道就绪(或超时)  selector.select() 返回就绪事件的个数
			if (selector.select(TIMEOUT) == 0) {// 如果没有就绪的信道时
				System.out.println("独自等待...");  
				continue;  
			}
			// 7、调用selectedKeys方法获取就绪channel集合
			// 如果有已经准备好的信道，则获取所有已经准备好的信道
			Set<SelectionKey> setKeys = selector.selectedKeys();
			// Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();  

			// 迭代取出每一个信道处理
			Iterator<SelectionKey> iterator = setKeys.iterator(); 
			SelectionKey key = null;
			while ( iterator.hasNext() ) { // 如果有还有准备好的信道，则进行如下处理
				key = iterator.next();

				// 8、判断就绪事件种类，调用业务处理方法
				try {  
					if (key.isAcceptable()) { // 如果是接入事件，即有连接上来了
						acceptHandler(key); // 有客户端连接请求时  
					}  
					if (key.isReadable()) {// 如果是可读事件，即：有数据发送过来需要读取  
						readHandler(key); // 从客户端读取数据  
					}  
					if (key.isValid() && key.isWritable()) {// 如果有效且是可写事件，即：有数据可以发送给客户端  
						writeHandler(key);  // 客户端可写时   
					}  
				} catch (IOException e) {  
					// 出现IO异常（如客户端断开连接）时移除这个信道
					iterator.remove();  
					continue;  
				}  
				// 移除处理过的信道
				iterator.remove();  
			}  
		}
	}

	/**
	 * 接入事件处理方法
	 * @throws IOException 
	 */
	private void acceptHandler(SelectionKey key) throws IOException {
		// 1、 创建一个SocketChannel，用来维持与客户端的连接
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel(); // 获取与这个客户端通信的信道
		SocketChannel socketChannel = serverChannel.accept(); // 获取客户端连接

		// 2、将socketChannel设置为非阻塞方式
		socketChannel.configureBlocking(false);

		// 3、将这个channel注册到selector上，监听可读事件，这样当客户端有信息发过来时，就可以被服务器监听到，然后通知我们处理
		//  分配一个新的字节缓冲区
		socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(BUFFERSIZE));

		// 4、给客户端一个响应   以UTF-8格式给客服端一个响应
		socketChannel.write(Charset.forName("UTF-8").encode("登录成功！您与其他成员不是朋友关系，请注意个人信息安全..."));
	}

	/**
	 * 可读事件处理方法
	 * @throws IOException 
	 */
	private void readHandler(SelectionKey key) throws IOException {
		// 1、先获得与客户端通信的信道  
		SocketChannel channel = (SocketChannel) key.channel();  

		// 2、得到附加对象，如果为空则自己创建一个
		ByteBuffer buffer = (ByteBuffer) key.attachment(); //获取当前的附加对象。当前已附加到此键的对象，如果没有附加对象，则返回 null

		if (buffer == null) {
			buffer = ByteBuffer.allocate(BUFFERSIZE);
		}

		// 清空缓冲区 将位置设置为 0，将限制设置为容量，并丢弃标记。
		buffer.clear();  

		// 3、循环读取客户端发过来的信息
		String line = "";
		while ( channel.read(buffer) > 0 ) {
			// 将缓冲区准备为数据传出状态 ，即将写模式转换成读模式
			buffer.flip();

			// 将字节转化为为UTF-8的字符串  
			// line += new String(buffer.array(), 0, buffer.remaining(), "UTF-8");
			line += Charset.forName("UTF-8").decode(buffer).toString();  
		}
		
		// 4、将channel再次注册到selector上，继续监听它的可读事件，等待下一个客户端发信息过来
		channel.register(key.selector(), SelectionKey.OP_READ);
		
		// 5、将客户端信息，广播给所有在线用户
		if (line.length() > 0) {
			// 控制台打印出来  
			System.out.println("接收到来自 " + channel.socket().getRemoteSocketAddress() + " 的信息:"  + line);  
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss EEE");
			// 准备响应信息  
			String sendStr = "你好,客户端。" + sdf.format(new Date()) + "，已经收到你的信息：" + line;  
			buffer = ByteBuffer.wrap(sendStr.getBytes("UTF-8"));  
			channel.write(buffer);  
			
			broadCast(key, line);
			// 将此键的 interest 集合设置为给定值，设置为下一次读取或是写入做准备  
			// key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);  
		}  
	}

	/**
	 * 可写事件处理方法
	 */
	private void writeHandler(SelectionKey key) {

	}
	
	/**
	 * 广播给所有在线用户的方法
	 */
	private void broadCast(SelectionKey key, String line) {
		// 1、获取所有已接入的客户端channel
		Set<SelectionKey> setKeys = key.selector().keys();
		
		// 2、循环广播
		setKeys.forEach(SelectionKey -> {
			Channel channel = SelectionKey.channel();
			
			// 判断是不是SocketChannel，并且是不是原来的那个channel
			if (channel instanceof SocketChannel && channel !=key.channel()) {
				try {
					// 发送消息到客户端
					((SocketChannel)channel).write(Charset.forName("UTF-8").encode(line));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
