package com.rainbow.study.netty.jdk.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioEchoClient {
    public static void main(String[] args) throws IOException {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        
        client.connect(new InetSocketAddress("127.0.0.1", 8080), null, new CompletionHandler<Void, Object>() {
            @Override
            public void completed(Void result, Object attachment) {
                byte[] data = String.valueOf(System.currentTimeMillis()).getBytes();
                ByteBuffer writeBuffer = ByteBuffer.allocate(data.length);
                writeBuffer.put(data);
                writeBuffer.flip();
                client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        if (attachment.hasRemaining()) {
                            client.write(attachment, attachment, this);
                        }
                        else {
                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                            client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                                @Override
                                public void completed(Integer result, ByteBuffer attachment) {
                                    attachment.flip();
                                    byte[] bytes = new byte[attachment.remaining()];
                                    attachment.get(bytes);
                                    try {
                                        String body = new String(bytes, "UTF-8");
                                        System.out.println("Client receive: " + body);
                                    }
                                    catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
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

            @Override
            public void failed(Throwable exc, Object attachment) {
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
}
