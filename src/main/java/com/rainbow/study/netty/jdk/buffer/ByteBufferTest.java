package com.rainbow.study.netty.jdk.buffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

public class ByteBufferTest {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        CharBuffer charBuffer = CharBuffer.allocate(15);
        printBufferInfo(charBuffer);

        charBuffer.put("Hello World");
        printBufferInfo(charBuffer);

        charBuffer.flip();
        printBufferInfo(charBuffer);

        System.out.println(charBuffer.get());
        printBufferInfo(charBuffer);

        charBuffer.rewind(); // reread data
        System.out.println(charBuffer.get());
        printBufferInfo(charBuffer);

        charBuffer.compact(); // make buffer ready for following write operation
        charBuffer.put("!!!");
        printBufferInfo(charBuffer);
        System.out.println(new String(charBuffer.array()));

        // wrap初始化buffer之后，position位置为0，无需调用flip即可开始读取
        ByteBuffer wrapBuffer = ByteBuffer.wrap("How are you".getBytes());
        printBufferInfo(wrapBuffer);
        System.out.println((char) wrapBuffer.get());

        // ByteBuffer 与 FileChannel结合测试
        RandomAccessFile file = new RandomAccessFile("input.txt", "r");
        FileChannel fileChannel = file.getChannel();
        ByteBuffer fileBuffer = ByteBuffer.allocate(5);
        fileChannel.read(fileBuffer);
        fileBuffer.flip();
        System.out.println(new String(fileBuffer.array()));
    }

    private static int getMark(Buffer buffer) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = Buffer.class.getDeclaredMethod("markValue");
        m.setAccessible(true);
        return (int) m.invoke(buffer);
    }

    private static void printBufferInfo(Buffer buffer) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        System.out.println(
                String.format("position: %d; limit: %d; capacity: %d; mark: %d", buffer.position(), buffer.limit(), buffer.capacity(), getMark(buffer)));
    }
}
