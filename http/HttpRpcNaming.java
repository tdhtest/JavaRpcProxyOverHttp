package testjavarpc.http;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HttpRpcNaming implements Runnable ,HttpRpcConstants{
	private String message;
	public HttpRpcNaming(String message) {
		this.message = message;
	}
	@Override
	public void run() {
     	while(true) {
     		try
            {	
	         DatagramSocket ds = new DatagramSocket();
	         DatagramPacket dp = new DatagramPacket(message.getBytes(),message.length(), InetAddress.getByName("255.255.255.255"), broadCastPort);
	         ds.send(dp);
	         ds.close();
	 		Thread.sleep(500);;
	        }   catch (Exception e) 
    {
        e.printStackTrace();
    }
		
     	}}

}
