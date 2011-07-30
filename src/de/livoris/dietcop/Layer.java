package de.livoris.dietcop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

public abstract class Layer {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface AppliesTo {
		Class<?> value();
	}
	
	private Callable<Object> layer(final Callable<?> next, final Object obj, final Method layerMethod, final Object[] args) {
		return new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				ArrayList<Object> newArgs = new ArrayList<Object>(Arrays.asList(args == null ? new Object[0] : args));
				newArgs.add(0, obj);
				newArgs.add(0, next);
				return layerMethod.invoke(Layer.this, newArgs.toArray());
			}
		};
	}

	Callable<Object> chain(final Object obj, final Method method, final Object[] args, Callable<Object> next) {
		for (Method layerMethod : getClass().getDeclaredMethods()) {
			AppliesTo at = layerMethod.getAnnotation(AppliesTo.class);
			for (Class<?> intf : obj.getClass().getInterfaces()) {
				if (at != null && at.value().equals(intf) && method.getName().equals(layerMethod.getName())) {
					ArrayList<Class<?>> mParameterTypes = new ArrayList<Class<?>>(Arrays.asList(layerMethod.getParameterTypes()));
					if (Callable.class.equals(mParameterTypes.remove(0))
						&& at.value().equals(mParameterTypes.remove(0))
						&& mParameterTypes.equals(Arrays.asList(method.getParameterTypes()))) {
						return layer(next, obj, layerMethod, args);
					}
				}
			}
		}
		return next;
	}
}
