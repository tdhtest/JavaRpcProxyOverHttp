package testjavarpc.http.service;

public class FuckImpl implements Fuck {

	@Override
	public Humen getBaby(String name, String sex, int age) { 
		return new Humen(name, sex, age);
	}

}
