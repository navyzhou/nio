package com.yc.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * transferTo()方法
 * @author navy
 * @date 2020年11月18日
 * @email haijunzhou@hnit.edu.cn
 */
public class Test08 {
	public static void main(String[] args) throws IOException {
		RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
		FileChannel fromChannel = fromFile.getChannel();
		
		RandomAccessFile toFile = new RandomAccessFile("toFile.txt", "rw");
		FileChannel toChannel = toFile.getChannel();
		
		long position = 0;
		long count = fromChannel.size();
		
		//将字节从此通道的文件传输到给定的可写入字节通道。
		//从fromChannel写入到toChannel的0号位置，从fromChannel的position位置开始写，写入count个字节到toChannel
		fromChannel.transferTo(position, count, toChannel);
		
		ByteBuffer bufFrom=ByteBuffer.allocate(48);
		int readLenFrom=fromChannel.read(bufFrom);
		
		ByteBuffer bufTo=ByteBuffer.allocate(48);
		int readLenTo=toChannel.read(bufTo);
		
		while (readLenFrom != -1) {
			//使缓冲区为一系列新的通道写入或相对获取操作做好准备：它将限制设置为当前位置，然后将位置设置为 0。 
			bufFrom.flip(); //反转此缓冲区，即将数据写模式转换成数据读模式
			//以字符串的方式一次性输出缓冲区中的内容
			System.out.print(new String( bufFrom.array(),0,readLenFrom ) );
			bufFrom.clear(); //清除此缓冲区，等待后面新数据的写入
			readLenFrom = fromChannel.read(bufFrom);
		}
		
		while (readLenTo != -1) {
			//使缓冲区为一系列新的通道写入或相对获取操作做好准备：它将限制设置为当前位置，然后将位置设置为 0。 
			bufFrom.flip(); //反转此缓冲区，即将数据写模式转换成数据读模式
			//以字符串的方式一次性输出缓冲区中的内容
			System.out.print(new String( bufTo.array(),0,readLenTo ) );
			bufFrom.clear(); //清除此缓冲区，等待后面新数据的写入
			readLenTo = toChannel.read(bufTo);
		}
		
		//fromChannel管道中还有数据，而此时toChannel中没有数据了
		fromFile.close();	
		toFile.close();	
	}
}

