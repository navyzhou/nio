package com.yc.nio.chatroom;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端处理信息的方法
 * @author navy
 * @date 2020年11月20日
 * @email haijunzhou@hnit.edu.cn
 */
public class NioClientHandler implements Runnable{
	private static final int BUFFERSIZE = 1024; // 缓冲区大小   
	private static final int TIMEOUT = 3000; // 超时时间，单位毫秒  
	private Selector selector;

	public NioClientHandler(Selector selector) {
		this.selector = selector;
	}

	@Override
	public void run() {
		try {
			while(true) {
				// 等待某信道就绪(或超时)  selector.select() 返回就绪事件的个数
				if (selector.select(TIMEOUT) == 0) {// 如果没有就绪的信道时
					continue;  
				}

				// 如果有已经准备好的信道，则获取所有已经准备好的信道
				Set<SelectionKey> setKeys = selector.selectedKeys();
				// Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();  

				// 迭代取出每一个信道处理
				Iterator<SelectionKey> iterator = setKeys.iterator(); 
				SelectionKey key = null;
				while ( iterator.hasNext() ) { // 如果有还有准备好的信道，则进行如下处理
					key = iterator.next();

					try {  
						if (key.isReadable()) {// 如果是可读事件，即：有数据发送过来需要读取  
							readHandler(key);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		// 3、循环读取服务器响应的信息
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
		
		if (line.length() > 0) {
			// 控制台打印出来  
			System.out.println("接收到来自 " + channel.socket().getRemoteSocketAddress() + " 的信息:"  + line);  
		}  
	}
}
