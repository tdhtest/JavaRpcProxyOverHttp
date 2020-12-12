package testjavarpc.http;

import java.util.Base64;

public interface HttpRpcConstants {
	static final String INSTANCE_NAME = "instanceName";
	static final String HAS_PARAM = "hasParam";
	static final String HAS_PARAM_TRUE = "true";
	static final int broadCastPort = 42226;
	static final String SERVICE_PATH = "/rpc";
	static final int PORT = 64222;
	static final String METHOD_NAME = "methodName";
	static final String CLASS_FULL_NAME = "classFullName"; 
	
	final Base64.Decoder decoder = Base64.getDecoder();
	final Base64.Encoder encoder = Base64.getEncoder();
}
