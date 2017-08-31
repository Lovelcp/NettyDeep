package com.rainbow.study.netty.jdk.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioEchoServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        System.out.println("Server start!");

        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment) {
                System.out.println("Accept connection from " + client);
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                // read client data
                client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        if (result <= 0) {
                            // 如果没有数据，则什么都不做，继续保持读取状态
                            ByteBuffer newBuffer = ByteBuffer.allocate(1024);
                            client.read(newBuffer, newBuffer, this);
                            return;
                        }

                        attachment.flip();
                        byte[] body = new byte[attachment.remaining()];
                        attachment.get(body);
                        try {
                            System.out.println("Server receive: " + new String(body, "UTF-8"));

                            // echo message
                            ByteBuffer writeBuffer = ByteBuffer.allocate(body.length);
                            writeBuffer.put(body);
                            writeBuffer.flip();
                            client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                                @Override
                                public void completed(Integer result, ByteBuffer attachment) {
                                    // 如果没有发送完成，继续发送
                                    if (buffer.hasRemaining()) {
                                        client.write(attachment, attachment, this);
                                    }
                                }

                                @Override
                                public void failed(Throwable exc, ByteBuffer attachment) {
                                    exc.printStackTrace();
                                    try {
                                        client.close();
                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        finally {
                            // continue reading data from client
                            ByteBuffer newBuffer = ByteBuffer.allocate(1024);
                            client.read(newBuffer, newBuffer, this);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        exc.printStackTrace();
                        try {
                            client.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // continue accepting new client connection
                serverSocketChannel.accept(null, this);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
                try {
                    serverSocketChannel.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 阻塞main线程，防止server退出
        System.in.read();
    }
}
