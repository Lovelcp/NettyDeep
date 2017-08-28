package com.rainbow.study.netty.jdk.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class NioEchoServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.socket()
               .bind(new InetSocketAddress(8080), 1024);
        channel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server start!");

        boolean stop = false;
        while (!stop) {
            selector.select(1000);
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            for (SelectionKey key : selectedKeys) {
                try {
                    handleKey(key, selector);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    if (key != null) {
                        key.cancel();
                        if (key.channel() != null) {
                            key.channel()
                               .close();
                        }
                    }
                    stop = true;
                }
            }
        }

        // 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会自动关闭，所以不需要重复释放资源
        selector.close();
        System.out.println("Server stop!");
    }

    private static void handleKey(SelectionKey key, Selector selector) throws IOException {
        if (!key.isValid()) {
            return;
        }

        // 处理新接入的请求消息
        if (key.isAcceptable()) {
            // 处理新连接
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverChannel.accept(); // 类比BIO的ServerSocket.accept()方法
//            if (socketChannel != null) {
                socketChannel.configureBlocking(false);

                // 将新连接注册到selector中监听
                socketChannel.register(selector, SelectionKey.OP_READ);
//            }
        }

        if (key.isReadable()) {
            // 读取数据
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            int readBytes = socketChannel.read(readBuffer);
            if (readBytes > 0) {
                readBuffer.flip();
                byte[] bytes = new byte[readBuffer.remaining()];
                readBuffer.get(bytes);
                String body = new String(bytes, "UTF-8");
                System.out.println("Server receive: " + body);
                echoMessage(socketChannel, body);
            }
            else if (readBytes < 0) {
                // 关闭链路
                key.cancel();
                socketChannel.close();
            }
        }
    }

    private static void echoMessage(SocketChannel channel, String response) throws IOException {
        if (response == null || response.isEmpty()) {
            return;
        }

        byte[] bytes = response.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        channel.write(writeBuffer);
    }
}
