package de.livoris.dietcop;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public final class Context {
	
	private static List<Layer> layers = new LinkedList<Layer>();
	
	public static void with(Class<? extends Layer> layerClass, Runnable runnable) {
		Collection<Class<? extends Layer>> list = new ArrayList<Class<? extends Layer>>();
		list.add(layerClass);
		with(list, runnable);
	}
	
	public static void with(Class<? extends Layer> layerClass1,
			                Class<? extends Layer> layerClass2,
			                Runnable runnable) {
		Collection<Class<? extends Layer>> list = new ArrayList<Class<? extends Layer>>();
		list.add(layerClass1);
		list.add(layerClass2);
		with(list, runnable);
	}

	public static void with(Collection<Class<? extends Layer>> layerClasses, Runnable runnable) {
		Collection<Layer> newLayers = new LinkedList<Layer>();
		try {
			for (Class<? extends Layer> cls : layerClasses) {
				newLayers.add(cls.newInstance());
			}
			layers.addAll(newLayers);
			runnable.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for (Layer layer : newLayers) {
				layers.remove(layer);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T>T wrap(T t) {
		return (T)Proxy.newProxyInstance(t.getClass().getClassLoader(), t.getClass().getInterfaces(), new ContextProxy<T>(t));
	}
	
	static class ProceedCall implements Callable<Object> {
		Layer layer;
		Callable<?> next;
		Method m;
		Object obj;
		Object[] args;
		public ProceedCall(Layer layer, Callable<?> next, Method m, Object obj, Object[] args) {
			this.layer = layer;
			this.next = next;
			this.m = m;
			this.obj = obj;
			this.args = args;
		}
		
		@Override
		public Object call() throws Exception {
			ArrayList<Object> newArgs = new ArrayList<Object>(Arrays.asList(
					args == null ? new Object[0] : args));
			newArgs.add(0, obj);
			newArgs.add(0, next);
			System.out.println("  " + m);
			System.out.print("  args = ");
			for (Object object : newArgs) {
				System.out.print(object.getClass());
			}
			System.out.println();
			return m.invoke(layer, newArgs.toArray());
		}
	}

	public static Object executeLayered(Object obj, Method method,
			Object[] args, Callable<?> proceed) throws Exception {
		Callable<?> current = proceed;
		System.out.printf("[%s] %s\n", obj.getClass().getSimpleName(), method.toGenericString());
		List<Class<?>> methodParameterTypes = Arrays.asList(method.getParameterTypes());
		for (Layer l : layers) {
			for (Method m : l.getClass().getDeclaredMethods()) {				
				AppliesTo at = m.getAnnotation(AppliesTo.class);
				for (Class<?> intf : obj.getClass().getInterfaces()) {
					if (at != null && at.value().equals(intf) &&
						method.getName().equals(m.getName())) {
						System.out.printf("[%s] %s\n", at.value().getSimpleName(), m.toGenericString());
						List<Class<?>> mParameterTypes = new ArrayList<Class<?>>(Arrays.asList(m.getParameterTypes()));
						if (Callable.class.equals(mParameterTypes.remove(0)) &&
							at.value().equals(mParameterTypes.remove(0)) &&
							methodParameterTypes.equals(mParameterTypes)) {
							current = new ProceedCall(l, current, m, obj, args);
						}
					}
				}
			}
		}
		System.out.println();
		return current.call();
	}
}
