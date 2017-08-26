package com.rainbow.study.netty.jdk.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BioEchoServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket server = new ServerSocket(port);
        System.out.println("BioEchoServer start!");
        while (true) {
            Socket socket = server.accept(); // 一直阻塞直到有新的socket客户端连接到来
            new Thread(new EchoHandler(socket)).start(); // 新建一个线程处理客户端连接
        }
    }

    public static class EchoHandler implements Runnable {
        private Socket socket;

        public EchoHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {
                String data;
                while ((data = in.readLine()) != null) {
                    System.out.println("===================");
                    System.out.println("Server receive: " + data);

                    Thread.sleep(1000); // 1s之后返回
                    out.println(data);
                    out.flush();
                    System.out.println("Server echo: " + data);
                }
                System.out.println("Server close socket");
                socket.close();
            }
            catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
