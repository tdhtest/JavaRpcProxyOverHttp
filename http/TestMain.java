package testjavarpc.http;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import testjavarpc.http.service.IHello; 

public class TestMain {

	public static void main(String[] args) throws Exception {
		    Class<?> value =  IHello.class;
 		    InvocationHandler client1 =new HttpInvocationHandler(value,  "localhost",64222);
 		   IHello proxy1 = (IHello)Proxy.newProxyInstance(value.getClassLoader(), new Class[] {value}, client1); 
		   String girl = proxy1.sayHello("小小的,紧张颤抖的"+"瘦弱的"+13);
		   System.out.println(girl);
	}

}
