package testjavarpc.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.ContentType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler; 

import testjavarpc.http.tools.Util;
/**
 * 使用 http 重写 rpc
 * @author michazl
 *
 * @param <T>
 */
public class HttpRpcServer extends HttpServlet {
	 static final String SERVICE_PATH = "/rpc";
	static final int PORT = 64222;
	static final String ARGUMENTS_BASE64 = "argumentsBase64";
	 static final String PARAMETER_TYPES = "parameterBase64";
	 static final String METHOD_NAME = "methodName";
	 static final String CLASS_FULL_NAME = "classFullName";
	 static final String INTERFACE_FULL_NAME = "interfaceFullName";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4388686824109402813L;
	private final Base64.Decoder decoder = Base64.getDecoder();

	private  final Base64.Encoder encoder = Base64.getEncoder();

    public HttpRpcServer() {
    }
    static final HashMap<String, Class<?>> serviceRegistry = new HashMap<>();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String classFullName = req.getParameter(CLASS_FULL_NAME);
        String methodName = req.getParameter(METHOD_NAME);
        Object result = null;
        Method method  = null;
        try {
    	String parameterBase64 = req.getParameter(PARAMETER_TYPES); 
    	ObjectInputStream parameterInput = new ObjectInputStream(new ByteArrayInputStream(decoder.decode(parameterBase64)));
        Class<?>[] parameterTypes = (Class<?>[]) parameterInput.readObject();
        String argumentsBase64 = req.getParameter(ARGUMENTS_BASE64); 
    	ObjectInputStream argumentsInput = new ObjectInputStream(new ByteArrayInputStream(decoder.decode(argumentsBase64)));
        Object[] arguments = (Object[]) argumentsInput.readObject(); 
         Class<?> serviceClass = serviceRegistry.get(classFullName);
         if (serviceClass == null) {
        	 ArrayList<Class<?>> list = Util.getInterfaceImpls(Class.forName(classFullName));
        	 if(!list.isEmpty()) {
        		 serviceClass = list.get(0);
        	 }
         }
         if (serviceClass == null) {
             throw  new ClassNotFoundException("impl of "+ classFullName + "  not found in class path ");
         }
		  method = serviceClass.getMethod(methodName, parameterTypes);

         result = method.invoke(serviceClass.newInstance(), arguments);
        }catch( ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
        	e.printStackTrace();
        	resp.sendError(500, e.getMessage());
        	return;
        }
		resp.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
		resp.setContentType(ContentType.DEFAULT_TEXT.getMimeType());
	         ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream resultOutput = new ObjectOutputStream(baos);  
			resultOutput.writeObject(result);
			String resultBase64 = encoder.encodeToString(baos.toByteArray());
			resp.getWriter().append(resultBase64);
		
    }

    public static void main(String[] args) throws Exception {
    	Server server=new Server(PORT);
    	ServletContextHandler handler = new ServletContextHandler(server,"/");
    	handler.addServlet(HttpRpcServer.class,HttpRpcServer.SERVICE_PATH); 
		server.setHandler(handler);
    	server.start();
    	server.join();
    }
}