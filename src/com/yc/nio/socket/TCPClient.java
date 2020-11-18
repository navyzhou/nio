package com.yc.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * @author navy
 * @date 2020年11月18日
 * @email haijunzhou@hnit.edu.cn
 */
public class TCPClient {  
    // 信道选择器  
    private Selector selector;  
  
    // 与服务器通信的信道  
    SocketChannel socketChannel;  
  
    // 要连接的服务器Ip地址  
    private String hostIp;  
  
    // 要连接的远程服务器在监听的端口  
    private int hostListenningPort; 
    
    public TCPClient(String HostIp, int HostListenningPort) throws IOException {  
        this.hostIp = HostIp;  
        this.hostListenningPort = HostListenningPort;  
        initialize();  
    }  
    /**  
     * 初始化  
     *   
     * @throws IOException  
     */  
    private void initialize() throws IOException {  
        // 打开监听信道并设置为非阻塞模式  
        socketChannel = SocketChannel.open(new InetSocketAddress(hostIp,hostListenningPort));  
        socketChannel.configureBlocking(false);  
  
        // 打开并注册选择器到信道  
        selector = Selector.open();  
        socketChannel.register(selector, SelectionKey.OP_READ);  
  
        // 启动读取线程  
        new TCPClientReadThread(selector);  
    }  
    /**  
     * 发送字符串到服务器  
     *   
     * @param message  
     * @throws IOException  
     */  
    public void sendMsg(String message) throws IOException { 
    	//将 byte 数组包装到缓冲区中
        ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes("UTF-8"));  
        //System.out.println(new String( writeBuffer.array(),0,writeBuffer.remaining() ) );
        socketChannel.write(writeBuffer); 
    } 
    
    static TCPClient client;  
    static boolean mFlag = true;  
    
    @SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {  
        client = new TCPClient("127.0.0.1", 8888);
        final Scanner scan = new Scanner(System.in);//键盘输入数据  
        new Thread() {  
            @Override  
            public void run() {  
                try {  
                    client.sendMsg("你好!醉里挑灯看剑,梦回吹角连营。");  
                    while (mFlag) {  
                    	System.out.println("请输入您要发送的信息：");
                        String string = scan.next();  
                        client.sendMsg(string);  
                    }  
                } catch (IOException e) {  
                    mFlag = false;  
                    e.printStackTrace();  
                } finally {  
                    mFlag = false;  
                }  
                super.run();  
            }  
        }.start();  
    }  
} 