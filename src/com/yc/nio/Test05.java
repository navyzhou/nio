package com.yc.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 通过Buffer的put()方法向Buffer中写数据
 * 然后从Buffer读取数据到Channel
 * @author navy
 * @date 2020年11月18日
 * @email haijunzhou@hnit.edu.cn
 */
public class Test05 {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		RandomAccessFile file = new RandomAccessFile("data_3.txt", "rw");
		FileChannel channel = file.getChannel();
		
		ByteBuffer buf1 = ByteBuffer.allocate(6);
		buf1.put("源辰".getBytes());
		ByteBuffer buf2   = ByteBuffer.allocate(12);
		buf2.put("信息科技".getBytes());
		
		ByteBuffer[] bufferArray = { buf1, buf2 };
		channel.write(bufferArray);
		
		System.out.println(new String(buf1.array()));
		System.out.println(new String(buf2.array()));
		
	}
}
