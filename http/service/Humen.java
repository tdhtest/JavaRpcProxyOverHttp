package testjavarpc.http.service;

import java.io.Serializable;

public class Humen  implements  Serializable {
 /**
	 * 
	 */
	private static final long serialVersionUID = -2698997196165192340L;
private String name;
 private String sex;
 public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getSex() {
	return sex;
}
public void setSex(String sex) {
	this.sex = sex;
}
public Humen(String name, String sex, int age) {
	super();
	this.name = name;
	this.sex = sex;
	this.age = age;
}
@Override
public String toString() {
	return "Humen [name=" + name + ", sex=" + sex + ", age=" + age + "]";
}
public int getAge() {
	return age;
}
public void setAge(int age) {
	this.age = age;
}
private int age;
}
