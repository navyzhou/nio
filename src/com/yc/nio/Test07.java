package com.yc.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * transferFrom()方法
 * @author navy
 * @date 2020年11月18日
 * @email haijunzhou@hnit.edu.cn
 */
public class Test07 {
	public static void main(String[] args) throws IOException {
		RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
		FileChannel fromChannel = fromFile.getChannel();
		
		RandomAccessFile toFile = new RandomAccessFile("toFile.txt", "rw");
		FileChannel toChannel = toFile.getChannel();
		
		long position = 6;
		long count = fromChannel.size();
		
		//将字节从给定的可读取字节通道传输到此通道的文件中。
		//从指定位置开始覆盖指定的字节数
		toChannel.transferFrom(fromChannel, position, count);
		
		ByteBuffer buf=ByteBuffer.allocate(48);
		
		int readLen=toChannel.read(buf);
		
		while (readLen != -1) {
			System.out.println("读到的数据大小为： " + readLen);
			buf.flip(); 

			while(buf.hasRemaining()){ 
				System.out.print((char)buf.get()); 
			}
			buf.clear(); 
			readLen = toChannel.read(buf);
		}
		fromFile.close();	
		toFile.close();	
	}
}
