package com.rainbow.study.netty.jdk.buffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelTest {
    public static void main(String[] args) throws IOException {
        // 使用FileChannel和Buffer实现文件的拷贝
        RandomAccessFile inFile = new RandomAccessFile("input.txt", "r");
        FileChannel inChannel = inFile.getChannel();
        RandomAccessFile outFile = new RandomAccessFile("output.txt", "rw");
        FileChannel outChannel = outFile.getChannel();

        ByteBuffer fileBuffer = ByteBuffer.allocate(8);
        while (inChannel.read(fileBuffer) != -1) {
            fileBuffer.flip();
            outChannel.write(fileBuffer);
            fileBuffer.compact(); // In case of partial write
        }

        // 确保buffer里的数据被全部读取
        fileBuffer.flip();
        while (fileBuffer.hasRemaining()) {
            outChannel.write(fileBuffer);
        }
    }
}
