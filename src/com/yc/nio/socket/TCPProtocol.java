package com.yc.nio.socket;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * @author navy
 * @date 2020年11月18日
 * @email haijunzhou@hnit.edu.cn
 */
public interface TCPProtocol{ 
	   /**  
	    * 接收一个SocketChannel的处理  
	    * @param key  
	    * @throws IOException  
	    * SelectionKey:每次向选择器注册通道时就会创建一个选择键。通过调用某个键的 cancel 方法、关闭其通道，
	    * 或者通过关闭其选择器来取消 该键之前，它一直保持有效。取消某个键不会立即从其选择器中移除它；
	    * 相反，会将该键添加到选择器的已取消键集，以便在下一次进行选择操作时移除它。
	    * 可通过调用某个键的 isValid 方法来测试其有效性。 
	    */  
	   void handleAccept(SelectionKey key) throws IOException;  
	     
	   /**  
	    * 从一个SocketChannel读取信息的处理  
	    * @param key  
	    * @throws IOException  
	    */  
	   void handleRead(SelectionKey key) throws IOException;  
	     
	   /**  
	    * 向一个SocketChannel写入信息的处理  
	    * @param key  
	    * @throws IOException  
	    */  
	   void handleWrite(SelectionKey key) throws IOException;  
	 }  