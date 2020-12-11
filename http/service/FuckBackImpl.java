package testjavarpc.http.service;

public class FuckBackImpl implements FuckBack {

	@Override
	public void sendMsg(String msg) {
		System.out.println("server recived:"+msg);

	}

}
