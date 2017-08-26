package com.rainbow.study.netty.jdk.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioWithThreadPoolEchoServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket server = new ServerSocket(port);
        System.out.println("BioEchoServer start!");

        //
        // 这里我们设置了一个线程池，里面最多只有2个工作线程
        //
        EchoThreadPool threadPool = new EchoThreadPool();

        while (true) {
            Socket socket = server.accept(); // 一直阻塞直到有新的socket客户端连接到来
            threadPool.execute(new BioEchoServer.EchoHandler(socket)); // 提交到线程池中
        }
    }

    public static class EchoThreadPool {
        private ExecutorService executorService;

        public EchoThreadPool() {
            this.executorService = Executors.newFixedThreadPool(2);
        }

        public void execute(Runnable task) {
            executorService.execute(task);
        }
    }

}
