package testjavarpc.http;
   

import java.lang.reflect.Proxy;

import testjavarpc.service.Fuck;
import testjavarpc.service.Humen;
 

public class TestMain {

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, Throwable   {  
		  
    	HttpRpcServer r = new HttpRpcServer("testjavarpc");
    	r.start();
    
	    HttpInvocationHandler client1 =new HttpInvocationHandler();
	    Fuck ri = (Fuck) client1.getInstance(Fuck.class);
	    Humen str = ri.getBaby("asdasfdasdf","asdfas，，啊啊",13);
	    System.out.println(str);
	    
	      str = ri.getBaby("safdsdfsfasfdas","asd，，啊啊",13);
	    System.out.println(str);
	     Fuck f=   (Fuck) Proxy.newProxyInstance (Fuck.class.getClassLoader(),new Class<?>[]{Fuck.class},client1);
	    Humen msg = f.getBaby("君君", "女", 18);
	    System.out.println(msg); 
	    
	    Fuck fuck = (Fuck)client1.getInstance("fuck1",Fuck.class);
	    msg = fuck.getBaby("asdfa僧佛法", "nv", 23);
	    System.out.println(msg);
	}

}
