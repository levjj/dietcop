package de.livoris.dietcop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import de.livoris.dietcop.Layer.AppliesTo;

public final class Context {
	
	private static List<Layer> layers = new LinkedList<Layer>();

	public static interface Evaluator {
		void eval(Runnable block);
	}
	
	public static Evaluator with(final Class<? extends Layer>... layerClasses) {
		return new Evaluator() {
			@Override
			public void eval(Runnable block) {
				List<Layer> oldLayers = new LinkedList<Layer>(layers);
				try {
					for (Class<? extends Layer> cls : layerClasses) {
						layers.add(cls.newInstance());
					}
					block.run();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					layers = oldLayers;
				}
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <T> T wrap(final T object) {
		return (T) Proxy.newProxyInstance(
			object.getClass().getClassLoader(),
			object.getClass().getInterfaces(),
			new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					return executeLayered(object, method, args);
				}
			});
	}

	private static Callable<Object> layer(final Layer layer,
			final Callable<?> next, final Method m, final Object obj,
			final Object[] args) {
		return new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				ArrayList<Object> newArgs = new ArrayList<Object>(Arrays.asList(args == null ? new Object[0] : args));
				newArgs.add(0, obj);
				newArgs.add(0, next);
				return m.invoke(layer, newArgs.toArray());
			}
		};
	}

	private static Object executeLayered(final Object obj, final Method method, final Object[] args) throws Exception {
		Callable<Object> current = new Callable<Object>() {
			public Object call() throws Exception {
				return method.invoke(obj, args);
			}
		};
		List<Class<?>> methodParameterTypes = Arrays.asList(method.getParameterTypes());
		for (Layer l : layers) {
			for (Method m : l.getClass().getDeclaredMethods()) {
				AppliesTo at = m.getAnnotation(AppliesTo.class);
				for (Class<?> intf : obj.getClass().getInterfaces()) {
					if (at != null && at.value().equals(intf)
						&& method.getName().equals(m.getName())) {
						List<Class<?>> mParameterTypes = new ArrayList<Class<?>>(Arrays.asList(m.getParameterTypes()));
						if (Callable.class.equals(mParameterTypes.remove(0))
							&& at.value().equals(mParameterTypes.remove(0))
							&& methodParameterTypes.equals(mParameterTypes)) {
							current = layer(l, current, m, obj, args);
						}
					}
				}
			}
		}
		return current.call();
	}
}
