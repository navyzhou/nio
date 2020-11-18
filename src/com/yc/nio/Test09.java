package com.yc.nio;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * selector()选择器
 * @author navy
 * @date 2020年11月18日
 * @email haijunzhou@hnit.edu.cn
 */
public class Test09 {
	@SuppressWarnings({ "unused", "null" })
	public static void main(String[] args) throws IOException {
		//创建选择器
		Selector selector = Selector.open(); 

		//创建通道
		//RandomAccessFile file = new RandomAccessFile("data_1.txt", "rw");
		SelectableChannel channel=null;


		/*
		 * 调整此通道的阻塞模式:如果为 true，则此通道将被置于阻塞模式；如果为 false，则此通道将被置于非阻塞模式 
		 * 与Selector一起使用时，Channel必须处于非阻塞模式下。这意味着不能将FileChannel与Selector一起使用，
		 * 因为FileChannel不能切换到非阻塞模式。而套接字通道都可以。 
		 */
		channel.configureBlocking(false);  

		/*
		 * 向Selector注册通道 
		 * 注意register()方法的第二个参数。这是一个"interest集合"，意思是在通过Selector监听Channel时对什么事件感兴趣。
		 * 可以监听四种不同类型的事件： 
		 * Connect
		 * Accept
		 * Read
		 * Write
		 * 通道触发了一个事件意思是该事件已经就绪。所以，某个channel成功连接到另一个服务器称为“连接就绪”。
		 * 一个server socket channel准备好接收新进入的连接称为“接收就绪”。一个有数据可读的通道可以说是“读就绪”。等待写数据的通道可以说是“写就绪”。 
		 * 这四种事件用SelectionKey的四个常量来表示： 
		 * SelectionKey.OP_CONNECT
		 * SelectionKey.OP_ACCEPT
		 * SelectionKey.OP_READ
		 * SelectionKey.OP_WRITE
		 */
		SelectionKey key = channel.register(selector,SelectionKey.OP_READ);

		//如果你对不止一种事件感兴趣，那么可以用“位或”操作符将常量连接起来，如下： 
		//int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;

		/*
		 * 当向Selector注册Channel时，register()方法会返回一个SelectionKey对象。这个对象包含了一些你感兴趣的属性： 
		 * interest集合:interest集合是你所选择的感兴趣的事件集合。可以通过SelectionKey读写interest集合，像这样：
		 * 				int interestSet = selectionKey.interestOps(); 
		 * 				boolean isInterestedInAccept  = (interestSet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT；
		 * 				boolean isInterestedInConnect = interestSet & SelectionKey.OP_CONNECT;			
		 * 				boolean isInterestedInRead    = interestSet & SelectionKey.OP_READ;
		 * 				boolean isInterestedInWrite   = interestSet & SelectionKey.OP_WRITE;
		 * ready集合:ready集合是通道已经准备就绪的操作的集合。在一次选择(Selection)之后，你会首先访问这个ready set。
		 * 				可以这样访问ready集合： int readySet = selectionKey.readyOps(); 
		 * 				可以用像检测interest集合那样的方法，来检测channel中什么事件或操作已经就绪。但是，也可以使用以下四个方法，它们都会返回一个布尔类型： 
		 * 					selectionKey.isAcceptable();
		 * 					selectionKey.isConnectable();
		 * 					selectionKey.isReadable();
		 * 					selectionKey.isWritable();
		 * Channel:从SelectionKey访问Channel和Selector很简单。如下： 
		 * 			Channel  channel  = selectionKey.channel();
		 * 			Selector selector = selectionKey.selector();
		 * Selector
		 * 附加的对象（可选）
		 * 下面我会描述这些属性。 
		 */

		/*
		 * 一旦向Selector注册了一或多个通道，就可以调用几个重载的select()方法。这些方法返回你所感兴趣的事件（如连接、接受、读或写）已经准备就绪的那些通道。
		 * 换句话说，如果你对“读就绪”的通道感兴趣，select()方法会返回读事件已经就绪的那些通道。 
		 * 下面是select()方法： 
		 * int select()
		 * int select(long timeout)
		 * int selectNow()
		 * select()阻塞到至少有一个通道在你注册的事件上就绪了。 
		 * select(long timeout)和select()一样，除了最长会阻塞timeout毫秒(参数)。 
		 * selectNow()不会阻塞，不管什么通道就绪都立刻返回（此方法执行非阻塞的选择操作。如果自从前一次选择操作后，没有通道变成可选择的，则此方法直接返回零。）。
		 * select()方法返回的int值表示有多少通道已经就绪。亦即，自上次调用select()方法后有多少通道变成就绪状态。
		 * 如果调用select()方法，因为有一个通道变成就绪状态，返回了1，若再次调用select()方法，如果另一个通道就绪了，它会再次返回1。
		 * 如果对第一个就绪的channel没有做任何操作，现在就有两个就绪的通道，但在每次select()方法调用之间，只有一个通道就绪了。
		 */
	}
}

