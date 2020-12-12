package testjavarpc.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reflections.Reflections; 

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import testjavarpc.http.tools.SpringTool; 
/**
 * 使用 http 重写 rpc
 * @author michazl
 *
 * @param <T>
 */
public class HttpRpcServer implements HttpHandler ,HttpRpcConstants{     
	private Reflections r;
	private HttpServer server; 
	public HttpRpcServer(String packagePrefix) {
		   r  = new Reflections(packagePrefix) ; 
	}
	public void  start() {

        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        	try {
				server = HttpServer.create(new InetSocketAddress(PORT), 0);
		        server.createContext(SERVICE_PATH, this);    
				server.setExecutor(newCachedThreadPool);  
		        server.start();    
			} catch (IOException e1) {  
				return;
			}    
	        newCachedThreadPool.execute(new HttpRpcNaming(PORT+SERVICE_PATH)); 
     	} 
    private Object getResult(String classFullName, String methodName, Class<?>[] parameterTypes,Object[] arguments, String instanceName) throws Exception {
    	Object result = null;  
    	Object bean = null;
        if(null != instanceName) {//通过spring bean 提供服务
        	try {
        		Object ctx = SpringTool.getSpringContext(); 
        		Method method = ctx.getClass().getDeclaredMethod("getBean",String.class);
        		bean = method.invoke(ctx, instanceName);
        	}catch(Exception e) {
        		throw new Exception("spring bean failed",e);
        	}
        }else {//通过非管理class 创建对象并提供服务。 
       		     Class<?> serviceClass = null ;
       		     Class<?> clazz = Class.forName(classFullName); 
        		 Set<?> subs = r.getSubTypesOf(clazz);
        		 if(subs.isEmpty()) {//本体的服务器端版本
        			 serviceClass = clazz;
        		 }else {//子类或者实现接口的类
            		 Iterator<?> itr = subs.iterator();
            		 while(itr.hasNext()) {
            			 serviceClass = (Class<?>) itr.next();break;
            		 }
        		 }
        	     if (serviceClass == null) {
     	             throw  new ClassNotFoundException("impl or subclass of  "+ classFullName + "  not found in class path ");
     	         }
     	         bean = serviceClass.newInstance();
             }
			 Method method = bean.getClass().getMethod(methodName, parameterTypes);
	         result = method.invoke(bean, arguments);
            return result;
	} 
	@Override
	public void handle(HttpExchange exchange) throws IOException { 
		String classFullName = exchange.getRequestHeaders().getFirst(CLASS_FULL_NAME);
		String methodName = exchange.getRequestHeaders().getFirst(METHOD_NAME);
		String hasParam = exchange.getRequestHeaders().getFirst(HAS_PARAM);
    	String instanceName  = exchange.getRequestHeaders().getFirst(INSTANCE_NAME);
		String types ,  args  = null;
		  Class<?>[] parameterTypes = null; Object[] arguments  = null; 
		if(HAS_PARAM_TRUE.equals(hasParam)) {
			InputStream inputStream = exchange.getRequestBody() ;
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
			    result.write(buffer, 0, length);
			}
			String all = new String(result.toByteArray());
			String[] param = all.split("-");  
			  types  = param[0];
			  args = param[1];
		       try {
		    	ObjectInputStream parameterInput = new ObjectInputStream(new ByteArrayInputStream(decoder.decode(types.getBytes())));
				parameterTypes = (Class<?>[]) parameterInput.readObject(); 
		    	ObjectInputStream argumentsInput = new ObjectInputStream(new ByteArrayInputStream(decoder.decode((args.getBytes())))); 
					arguments = (Object[]) argumentsInput.readObject();
				} catch (Exception e) { 
		        	doOut(exchange, e,400);return;
				} 
		}
        Object result = null; 
        try { 
			result = getResult(classFullName, methodName, parameterTypes, arguments,instanceName);
        } catch (Exception e) {	 
        	doOut(exchange, e,400);return;
		} 
    	doOut(exchange, result,200);return; 
	} 
	private void doOut(HttpExchange exchange, Object e,int status) throws IOException {
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 ObjectOutputStream objOut = new ObjectOutputStream(baos);  
		  objOut.writeObject(e);
		  byte[] byteArray = baos.toByteArray();byteArray = encoder.encode(byteArray);
			exchange.sendResponseHeaders(status, byteArray.length); 
		exchange.getResponseBody().write(byteArray);
	}
 
}