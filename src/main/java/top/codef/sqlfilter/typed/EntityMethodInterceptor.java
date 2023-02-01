package top.codef.sqlfilter.typed;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import top.codef.exceptions.JpaAmebaException;

public class EntityMethodInterceptor implements MethodInterceptor {

	private String propName;

	private Class<?> returnType;

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		propName = getterOrSetter(method);
		if (propName == null)
			throw new JpaAmebaException("cannot find propName please use getter or setter(is) method");
		returnType = method.getReturnType();
		return null;
	}

	private String getterOrSetter(Method method) {
		var methodName = method.getName();
		if (!methodName.equals("getClass") && match(methodName, "is", "get", "set")) {
			var propName = methodName.replaceFirst("^(is|get|set)", "");
			propName = propName.replaceFirst("^.", String.valueOf(propName.charAt(0)).toLowerCase());
			return propName;
		}
		return null;
	}

	private boolean match(String name, String... startWith) {
		return Stream.of(startWith).anyMatch(name::startsWith);
	}

	public String getPropName() {
		return propName;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

}
