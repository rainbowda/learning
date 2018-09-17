package com.learnBase.aio;

import com.learnBase.Constant;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class Client {

	private AsynchronousSocketChannel asc ;
	
	public Client() throws Exception {
		asc = AsynchronousSocketChannel.open();
	}
	
	public void connect(){
		asc.connect(new InetSocketAddress(Constant.ADDRESS_LOCALHOST, Constant.PORT_10008));
	}
	
	public void write(byte[] bytes){
		try {
			asc.write(ByteBuffer.wrap(bytes)).get();
			read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void read() {
		ByteBuffer buf = ByteBuffer.allocate(1024);
		try {
			asc.read(buf).get();
			buf.flip();
			byte[] respByte = new byte[buf.remaining()];
			buf.get(respByte);
			System.out.println(new String(respByte,"utf-8").trim());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.connect();

		while (true){
			System.out.print("请输出你要发送的内容:");
			byte[] bytes = new byte[1024];
			System.in.read(bytes);
			client.write(bytes);
		}

	}
	
}
