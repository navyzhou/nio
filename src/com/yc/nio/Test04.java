package com.yc.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 从Channel写到Buffer
 * @author navy
 * @date 2020年11月18日
 * @email haijunzhou@hnit.edu.cn
 */
public class Test04 {
	public static void main(String[] args) throws IOException {
		RandomAccessFile file = new RandomAccessFile("data_3.txt", "rw");
		FileChannel channel = file.getChannel();
		
		ByteBuffer buf1 = ByteBuffer.allocate(6);
		ByteBuffer buf2   = ByteBuffer.allocate(12);
		
		ByteBuffer[] bufferArray = { buf1, buf2 };
		channel.read(bufferArray);
		
		System.out.println(new String(buf1.array()));
		System.out.println(new String(buf2.array()));
		
		channel.close();
		file.close();
		
	}
}
