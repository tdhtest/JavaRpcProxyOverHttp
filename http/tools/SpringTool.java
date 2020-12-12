package testjavarpc.http.tools;
 
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import org.reflections.Reflections;  

public class SpringTool {

	@SuppressWarnings("unchecked")
	public static Object getSpringContext() throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {   
		Reflections r  =new Reflections();
		Class<?> springBootAppClazz = Class.forName("org.springframework.boot.autoconfigure.SpringBootApplication");
		Set<Class<?>> classes = r.getTypesAnnotatedWith((Class<? extends Annotation>) springBootAppClazz);
		Iterator<Class<?>> itr = classes.iterator();
		if(itr.hasNext()) {
			Class<?> clazz = itr.next();
			Field[] fields = clazz.getDeclaredFields();
			Class<?> configurableApplicationContext = Class.forName("org.springframework.context.ConfigurableApplicationContext");
			for(Field field:fields) {
				if(field.getDeclaringClass().equals(configurableApplicationContext)) {
					field.setAccessible(true);   
					Object  ctx =  field.get(null); 
					return ctx;
				}
			}
		}
		return null; 

	}

}
