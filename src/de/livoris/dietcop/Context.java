package de.livoris.dietcop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;


public final class Context {

	private static ThreadLocal<List<Layer>> activeLayers = new ThreadLocal<List<Layer>>() {
	    @Override
		protected List<Layer> initialValue() { return new LinkedList<Layer>(); }
	};

	public static interface Evaluator {
		void eval(Runnable block);
	}

	public static Evaluator with(final Layer... layers) {
		return new Evaluator() {
			@Override
			public void eval(Runnable block) {
				List<Layer> oldLayers = new LinkedList<Layer>(activeLayers.get());
				try {
					activeLayers.get().addAll(Arrays.asList(layers));
					block.run();
				} finally {
					activeLayers.set(oldLayers);
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
				public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
					Callable<Object> top = new Callable<Object>() {
						@Override
						public Object call() throws Exception {
							return method.invoke(object, args);
						}
					};
					for (Layer layer : activeLayers.get()) {
						top = layer.chain(object, method, args, top);
					}
					return top.call();
				}
			});
	}
}
