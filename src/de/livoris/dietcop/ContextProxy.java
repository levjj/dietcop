package de.livoris.dietcop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class ContextProxy<T> implements InvocationHandler {
	T object;
	
	public ContextProxy(T object) {
		this.object = object;
	}
	
	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args)
			throws Throwable {
		return Context.executeLayered(object, method, args, new Callable<Object>(){
			public Object call() throws Exception {
				return method.invoke(object, args);
			}
		});
	}
}
