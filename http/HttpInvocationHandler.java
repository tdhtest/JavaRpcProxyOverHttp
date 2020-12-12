package testjavarpc.http;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream; 
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList; 
import java.util.List;
 

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy; 
import testjavarpc.http.tools.NetUtil; 
/**
 * 使用 http 重写 rpc
 * @author michazl
 *
 * @param  
 */
public class HttpInvocationHandler   implements InvocationHandler , MethodInterceptor , HttpRpcConstants{
  
	private String url ;
    private  Class<?>  serviceInterface;
	private String instanceName; 
 
    public HttpInvocationHandler(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;   
        url = NetUtil.findRpcServer();
    } 


    public HttpInvocationHandler() {  
        url = NetUtil.findRpcServer();
    } 

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         return invoke(method, args,this.serviceInterface);
    }
    public Object invoke(Method method, Object[] args, Class<?> clazz)
			throws IOException,  ClassNotFoundException, Exception {

    	URL url1 = new URL(url); 
    	HttpURLConnection c =   (HttpURLConnection) url1.openConnection();  
		c.addRequestProperty(CLASS_FULL_NAME, clazz.getName());
		c.addRequestProperty(METHOD_NAME, method.getName());
        if(null != instanceName) {
        	 c.addRequestProperty(INSTANCE_NAME, instanceName);
            instanceName = null;
        }
         if(null!=args) {
        	 c.addRequestProperty(HAS_PARAM, HAS_PARAM_TRUE);
        	 c.setDoOutput(true);
        	 c.getOutputStream().write(toEntity(method.getParameterTypes(),args) .getBytes());; 
         } 
          
		if(c.getResponseCode()!=200) {  
	        Exception e = (Exception) toObject(c, c.getErrorStream());
             throw e;
         }else {
             if(null != method.getReturnType())
             { 
     	        return toObject(c, c.getInputStream());
             }else {
                 return null; 
             }
         }
	}
	private String toEntity(Class<?>[] parameterTypes, Object[] args)   {
		String ret = null;
		try {
	 		ByteArrayOutputStream typesBaos =new  ByteArrayOutputStream();
			ObjectOutputStream typesStream = new ObjectOutputStream(typesBaos);
			typesStream.writeObject(parameterTypes);
	
	 		ByteArrayOutputStream argsBaos =new  ByteArrayOutputStream();
			ObjectOutputStream argsStream = new ObjectOutputStream(argsBaos);
			argsStream.writeObject(args);
			
			ret =encoder.encodeToString(typesBaos.toByteArray())+"-"+encoder.encodeToString(argsBaos.toByteArray()); 
		} catch (IOException e) { 
			e.printStackTrace();
		}  
		return ret;
} 
	private Object toObject(HttpURLConnection c,InputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream input;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); 
		byte[] buf = new byte[1024];
		int len  = 0;
		while((len= in.read(buf)) > 0){
			byteArrayOutputStream.write(buf, 0, len); 
		};
		c.disconnect();
		byte[] ba = decoder.decode(byteArrayOutputStream.toByteArray());
		 input = new ObjectInputStream(new ByteArrayInputStream(ba));
		 return  input.readObject(); 
	} 
    public static Class<?>[] getMethodParamsType( Object[] methodParams)
    {
        List<Class<?>> classs = new ArrayList<Class<?>>(methodParams.length);  
        for (Object os : methodParams)
        {
        	classs.add(os.getClass());
        }
        return classs.toArray(new Class<?>[0]);
    }
	public Object getInstance(Class<?> clazz) {  
		this.serviceInterface = clazz;
		if(!clazz.isInterface()) {
	    	  Enhancer enhancer = new Enhancer(); 
	          enhancer.setSuperclass(clazz); 
	          enhancer.setCallback(this); 
	          return enhancer.create(); 
	    }else {
	    	return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, this); 
	    }
      
	}
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable { 
//	        Object object = proxy.invokeSuper(obj, args);
		   Object object = invoke(method,args,obj.getClass().getSuperclass());
	        return object; 
	}
	public Object getInstance(String instanceName,Class<?>clazz) { 
		this.instanceName = instanceName; 
		return getInstance(clazz);
	}

}