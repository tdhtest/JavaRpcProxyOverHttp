package testjavarpc.http;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method; 
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils; 
/**
 * 使用 http 重写 rpc
 * @author michazl
 *
 * @param  
 */
public class HttpInvocationHandler   implements InvocationHandler {

    private   HttpClient client ; 
	private final Base64.Decoder decoder = Base64.getDecoder();
	private final Base64.Encoder encoder = Base64.getEncoder();
	private String url ;
    private  Class<?>  serviceInterface; 

    public HttpInvocationHandler(Class<?>  serviceInterface, String ip,int port) {
        this.serviceInterface = serviceInterface;  
        client =  new DefaultHttpClient();
        url ="http://"+ip+":"+port+HttpRpcServer.SERVICE_PATH;
    }

 

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 
        ByteArrayOutputStream paramTypesArray = new ByteArrayOutputStream();
		ObjectOutputStream paramTypes = new ObjectOutputStream(paramTypesArray);
		paramTypes.writeObject(method.getParameterTypes());
        ByteArrayOutputStream argumentsArray = new ByteArrayOutputStream();
		ObjectOutputStream argumentsArrayOs = new ObjectOutputStream(argumentsArray);
		argumentsArrayOs.writeObject(args); 
		ObjectInputStream input = null;; 
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(HttpRpcServer.CLASS_FULL_NAME, serviceInterface.getName()));
        nameValuePairs.add(new BasicNameValuePair(HttpRpcServer.METHOD_NAME, method.getName()));
        nameValuePairs.add(new BasicNameValuePair(HttpRpcServer.PARAMETER_TYPES, encoder.encodeToString(paramTypesArray.toByteArray())));
        nameValuePairs.add(new BasicNameValuePair(HttpRpcServer.ARGUMENTS_BASE64, encoder.encodeToString(argumentsArray.toByteArray())));
         HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
         HttpResponse response = client.execute(httpPost);
         if(response.getStatusLine().getStatusCode()!=200) {
        	 throw new Exception(response.getStatusLine().getReasonPhrase());
         }else {
             if(null != method.getReturnType())
             { 
             	String base64Res = EntityUtils.toString(response.getEntity());  
                    byte[] ba = decoder.decode(base64Res); 
                    input = new ObjectInputStream(new ByteArrayInputStream(ba));
                    return input.readObject(); 
             }else {
                 return null; 
             }
         }

    
    }

}