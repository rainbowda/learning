package com.learnBase.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketHandler implements Runnable {

    private Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {


        try (BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
             PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true)) {

            while (true) {
                String body = in.readLine();
                if (body == null) break;
                System.out.println("接收到数据:" + body);
                out.println("我收到数据了。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
