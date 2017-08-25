package com.rainbow.study.netty.jdk.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class BioEchoClient {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        Socket socket = new Socket("127.0.0.1", port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        out.write("Hi!\n");
        out.write("How\n");
        out.write("Are\n");
        out.write("You?\n");
        out.flush();
        String data;
        while ((data = in.readLine()) != null) {
            System.out.println("Client receive " + data);
        }
        socket.close();
    }
}
