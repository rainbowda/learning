package com.learnBase.bio;

import com.learnBase.Constant;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestBio {


    /**
     * 客户端
     */
    @Test
    public void client(){


        try (Socket socket = new Socket(Constant.ADDRESS_LOCALHOST, Constant.PORT_10001);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){

            out.println("客户端请求...");

            String response = in.readLine();
            System.out.println("接收到服务器的数据:" + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务端
     */
    @Test
    public void server(){
        try(ServerSocket server = new ServerSocket(Constant.PORT_10001)){

            System.out.println("server start...");

            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors(),
                    10,
                    120L,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(20));

            while (true){
                Socket socket = server.accept();//阻塞
                executor.submit(new SocketHandler(socket));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
