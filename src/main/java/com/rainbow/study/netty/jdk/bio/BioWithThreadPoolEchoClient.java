package com.rainbow.study.netty.jdk.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static com.rainbow.study.netty.jdk.bio.BioEchoClient.sendMessage;

public class BioWithThreadPoolEchoClient {
    public static void main(String[] args) {
        CountDownLatch startSignal = new CountDownLatch(1);

        for (int i = 0; i < 10; i++) {
            new Thread(new SendMessageThread(i, startSignal)).start();
        }

        startSignal.countDown(); // 保证所有的client同时启动
    }

    public static class SendMessageThread implements Runnable {
        private int index;
        private CountDownLatch startSignal;

        private SendMessageThread(int index, CountDownLatch startSignal) {
            this.index = index;
            this.startSignal = startSignal;
        }

        @Override
        public void run() {
            try {
                startSignal.await();
                int port = 8080;
                Socket socket = new Socket("127.0.0.1", port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true); // set autoFlush true

                sendMessage(in, out, "Hi!");
                sendMessage(in, out, "How");
                sendMessage(in, out, "Are");
                sendMessage(in, out, "You?");

                socket.close();
                System.out.println("Thread " + index + " finish!");
            }
            catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
