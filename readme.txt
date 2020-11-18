学习视频地址：https://www.imooc.com/learn/1118
NIO：
	Non-blocking I/O 或 New I/O
	这个跟叫一个人高富帅或小鲜肉一个意思，一个是从它的特性出发，因为他是非阻塞式I/O，一个是从它的年龄出发，它是原始I/O之后出现的。
	从JDK1.4开始引入，不是在原先的I/O基础之上扩展的，而是重新设计的一套I/O标准，用来应对这种高性能、高并发的应用场景。
	
BIO网络模型，基于阻塞I/O实现，什么是阻塞I/O呢？
	比如：我从网络上下载一个文件，那么我会调用一个read()读取数据的方法，当这个读取没有报错也没有返回时，程序是不会往下执行的。
		如果当前网络环境非常差，整个读取过程花了10秒钟，那么程序就会卡在这里10秒，这个情况就是阻塞I/O的特点。

NIO模型核心组件
	Selector组件：负责管理与客户端建立的连接，监听注册到它上面的一些事件，比如：有新链接接入、这个链接有可读消息或是可写的消息，那么它都可以监听到
		一旦这个事件被它监听到之后，它就会调用对应事件的处理器，来完成对事件的响应。
	步骤：
		1、Selector注册建立连接的事件
		2、客户端发送建立连接的请求给Selector
		3、Selector启动建立连接事件的处理器 Acceptor Handler
		4、Acceptor Handler创建与客户端的连接
		5、Acceptor Handler响应客户端的建立连接的请求
		6、Acceptor Handler注册连接可读事件给Selector
		7、客户端发送请求Selector
		8、Selector启动连接读写处理器Read&Write Handler
		9、读写处理器Read&Write Handler处理与客户端的读写业务
		10、读写处理器Read&Write Handler响应客户端请求
		11、读写处理器Read&Write Handler注册连接可读事件给Selector

NIO核心
	Channel：通道，对输入输出方式的另外一种抽象，可以类比BIO中流的概念。
		特点：
			双向性。BIO中流是单向的，分输入流和输出流，而Channel是双向的，即可读也可写。
			非阻塞的。
			操作唯一性。操作Channel的唯一方式是Buffer，通过Buffer实现对Channel中数据块的读写。
		实现：
			文件类：FileChannel
			UDP类：DatagramChannel
			TCP类：ServerSocketChannel/SocketChannel
		
	Buffer：读写Channel中的数据
		本质上是一块可以内存区域，这块内存被NIO包装成了一个Buffer对象，并提供一组方法，用来方便用户操作。
		核心属性：
			Capacity：容量。
			Position：位置，当前的数据位置。最大值为容量-1
			Limit：上限。在写模式下，表示最多可以往Buffer中写多少数据，此模式下Limit等于Capacity。
				在读模式下，表示最多能从Buffer中读取多少数据，此时Limit会被设置为写模式下的Position值。
			Mark：标记。存储一个特定的Position位置，之后可以通过调用Buffer的reset()方法可以恢复到这个Position位置，
				依然可以从这个位置开始处理数据。
				
	Selector：选择器或多路复用器
		作用：I/O就绪选择
		地位：NIO网络编程的基础
		通过它，一个线程就可以管理多个Channel，从而可以管理多个连接。
		
		SelectionKey常量：
			四种就绪状态常量：将选择器绑定到监听信道，只有非阻塞信道才可以注册选择器，并在注册过程中指出该信道可以进行Accept操作。
				SelectionKey.OP_ACCEPT:用于套接字接收操作的就绪
		 		SelectionKey.OP_CONNECT:用于套接字连接操作的就绪
		 		SelectionKey.OP_READ:用于读取操作的就绪
		 		SelectionKey.OP_WRITE:用于写入操作的就绪
		
NIO编程实现步骤
	1、创建Selector
	2、创建ServerSocketChannel，并绑定监听端口
	3、将Channel设置为非阻塞模式
	4、将Channel注册到Selector上，监听连接事件
	5、循环调用Selector的select方法，检测就绪情况
	6、调用selectedKeys方法获取就绪channel集合
	7、判断就绪事件种类，调用业务处理方法
	8、根据业务需要决定是否再次注册监听事件，重复执行第三步操作
