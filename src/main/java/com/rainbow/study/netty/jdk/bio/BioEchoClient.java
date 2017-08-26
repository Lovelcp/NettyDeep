package com.rainbow.study.netty.jdk.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class BioEchoClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 8080;
        Socket socket = new Socket("127.0.0.1", port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true); // set autoFlush true

        sendMessage(in, out, "Hi!");
        sendMessage(in, out, "How");
        sendMessage(in, out, "Are");
        sendMessage(in, out, "You?");

        socket.close();
    }

    public static void sendMessage(BufferedReader in, PrintWriter out, String message) throws IOException, InterruptedException {
        out.println(message);
        out.flush();
        System.out.println("Client send: " + message);
        System.out.println("Client receive: " + in.readLine());
        System.out.println("===================");
    }
}
