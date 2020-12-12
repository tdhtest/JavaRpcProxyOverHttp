package testjavarpc.http.tools;

import java.net.DatagramPacket;
import java.net.DatagramSocket; 

import testjavarpc.http.HttpRpcServer; 

public class NetUtil { 

	public static  String findRpcServer() {
		int port = HttpRpcServer.broadCastPort;//开启监听的端口
        DatagramSocket ds = null;
        DatagramPacket dp = null;
        byte[] buf = new byte[1024];//存储发来的消息
        StringBuffer sbuf = new StringBuffer();
        try 
        {
            //绑定端口的
            ds = new DatagramSocket(port);
            dp = new DatagramPacket(buf, buf.length); 
            ds.receive(dp);
            ds.close();
            int i;
            for(i=0;i<1024;i++)
            {
                if(buf[i] == 0)
                {
                    break;
                }
                sbuf.append((char) buf[i]);
            }           
            String portPath = sbuf.toString(); 
            String ip = dp.getAddress().getHostAddress();
    		String url = "http://"+ip+":"+portPath;
    		return url;
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		return null; 
	}
}
