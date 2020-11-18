package com.yc.nio.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author navy
 * @date 2020年11月18日
 * @email haijunzhou@hnit.edu.cn
 */
public class TCPProtocolImpl implements TCPProtocol{
	private int bufferSize;
	private SimpleDateFormat sbf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public TCPProtocolImpl(int bufferSize) {  
		this.bufferSize = bufferSize;  
	}  

	public void handleAccept(SelectionKey key) throws IOException {  
		// 返回创建此键的通道，接受客户端建立连接的请求，并返回 SocketChannel 对象  
		//SocketChannel clientChannel =key.channel(); //.accept();  
		ServerSocketChannel serverChannel=(ServerSocketChannel) key.channel();
		SocketChannel clientChannel = serverChannel.accept();
		
		// 非阻塞式  
		clientChannel.configureBlocking(false);  
		// 注册到selector                                                        分配一个新的字节缓冲区。
		clientChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize)); 
	}

	public void handleRead(SelectionKey key) throws IOException {  
		// 获得与客户端通信的信道  
		SocketChannel clientChannel = (SocketChannel) key.channel();  
		
		// 得到附加对象
		ByteBuffer buffer = (ByteBuffer) key.attachment();//获取当前的附加对象。当前已附加到此键的对象，如果没有附加对象，则返回 null
		
		// 清空缓冲区 将位置设置为 0，将限制设置为容量，并丢弃标记。
		buffer.clear();  
		
		// 读取信息获得读取的字节数  
		long bytesRead = clientChannel.read(buffer);
		
		if (bytesRead == -1) {  //没有读取到内容的情况， 关闭此通道
			clientChannel.close();   
		} else {  
			// 将缓冲区准备为数据传出状态  
			buffer.flip();  
			// 将字节转化为为UTF-16的字符串  
			//String receivedString = Charset.forName("UTF-8").newDecoder().decode(buffer).toString();  
			String receivedString = new String(buffer.array(),0,buffer.remaining(),"UTF-8");
			// 控制台打印出来  
			System.out.println("接收到来自" + clientChannel.socket().getRemoteSocketAddress() + "的信息:"  + receivedString);  
			// 准备发送的文本  
			String sendString = "你好,客户端." + sbf.format(new Date()) + "，已经收到你的信息" + receivedString;  
			buffer = ByteBuffer.wrap(sendString.getBytes("UTF-8"));  
			clientChannel.write(buffer);  
			
			// 将此键的 interest 集合设置为给定值，设置为下一次读取或是写入做准备  
			key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);  
		}  
	}  

	public void handleWrite(SelectionKey key) throws IOException {  
		
	}  
}