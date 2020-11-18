package com.yc.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Channel 字符串输出
 * @author navy
 * @date 2020年11月18日
 * @email haijunzhou@hnit.edu.cn
 */
public class Test02 {
	public static void main(String[] args) throws IOException {
		//创建从中读取和向其中写入（可选）的随机访问文件流。
		RandomAccessFile file = new RandomAccessFile("data_2.txt", "rw");
		//用于读取、写入、映射和操作文件的通道
		FileChannel channel = file.getChannel(); //返回与此文件关联的唯一 FileChannel对象。
		//字节缓冲区  分配一个新的字节缓冲区，容量为48个字节。容量最好设置为2、3的公倍数，因为utf-8一个汉字3个字节，而GBK一个汉字2个字节
		ByteBuffer buf = ByteBuffer.allocate(48);
		int readLen = channel.read(buf); //返回实际读到的数据长度，每次最多都容器大小个字节
		while (readLen != -1) {
			System.out.println("读到的数据大小为： " + readLen);
			//使缓冲区为一系列新的通道写入或相对获取操作做好准备：它将限制设置为当前位置，然后将位置设置为 0。 
			buf.flip(); //反转此缓冲区，即将数据写模式转换成数据读模式
			
			//以字符串的方式一次性输出缓冲区中的内容
			System.out.print(new String( buf.array(),0,readLen ) );

			/*while(buf.hasRemaining()){ //告知在当前位置和限制之间是否有元素
				System.out.print(new String( buf.array(),0,readLen ) );
				buf.position(readLen); //跳过已经读取了的数据
			}
			 */			

			buf.clear(); //清除此缓冲区，等待后面新数据的写入
			readLen = channel.read(buf);
		}
		file.close();	
	}
}
