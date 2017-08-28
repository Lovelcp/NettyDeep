package com.rainbow.study.netty.jdk.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.Set;

public class NioEchoClient {
    public static void main(String[] args) throws IOException {
        new Thread(new EchoClient("127.0.0.1", 8080)).start();
        new Thread(new EchoClient("127.0.0.1", 8080)).start();
        new Thread(new EchoClient("127.0.0.1", 8080)).start();
        new Thread(new EchoClient("127.0.0.1", 8080)).start();
        new Thread(new EchoClient("127.0.0.1", 8080)).start();
        new Thread(new EchoClient("127.0.0.1", 8080)).start();
        new Thread(new EchoClient("127.0.0.1", 8080)).start();
        new Thread(new EchoClient("127.0.0.1", 8080)).start();
        new Thread(new EchoClient("127.0.0.1", 8080)).start();
        new Thread(new EchoClient("127.0.0.1", 8080)).start();
    }

    public static class EchoClient implements Runnable {
        private String host;
        private int port;
        private Selector selector;
        private SocketChannel socketChannel;
        private boolean stop;

        public EchoClient(String host, int port) throws IOException {
            this.host = host;
            this.port = port;

            this.selector = Selector.open();
            this.socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        }

        @Override
        public void run() {
            System.out.println("Client start!");

            try {
                doConnect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            while (!stop) {
                try {
                    selector.select(1000);
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    for (SelectionKey key : selectedKeys) {
                        try {
                            handleKey(key);
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
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    stop = true;
                }
            }

            if (selector != null) {
                try {
                    selector.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Client stop!");
        }

        private void handleKey(SelectionKey key) throws IOException {
            if (!key.isValid()) {
                return;
            }

            SocketChannel socketChannel = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (socketChannel.finishConnect()) {
                    System.out.println("Client connected!");
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    sendMessage();
                }
                else {
                    System.exit(1); // 连接失败，进程退出
                }
            }

            if (key.isReadable()) {
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(readBuffer);
                if (readBytes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("Client receive: " + body);
                    stop = true;
                }
                else if (readBytes < 0) {
                    // 关闭链路
                    key.cancel();
                    socketChannel.close();
                }
            }
        }

        private void doConnect() throws IOException {
            // 如果直接连接成功，则注册到多路复用器上，发送请求消息，读应答
            if (socketChannel.connect(new InetSocketAddress(host, port))) {
                System.out.println("Client connected!");
                socketChannel.register(selector, SelectionKey.OP_READ);
                sendMessage();
            }
            else {
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }
        }

        private void sendMessage() throws IOException {
            String message = "Send num [" + new Random().nextInt(100) + "]";
            System.out.println("Message: " + message);
            byte[] bytes = message.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
            if (!writeBuffer.hasRemaining()) {
                System.out.println("Send message succeed");
            }
        }
    }
}
