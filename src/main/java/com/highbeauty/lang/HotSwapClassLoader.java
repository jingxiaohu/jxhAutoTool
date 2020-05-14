package com.highbeauty.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashSet;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class HotSwapClassLoader extends ClassLoader {

	private String baseDir; // 需要该类加载器直接加载的类文件的基目录
	private HashSet dynaClazzNs; // 需要由该类加载器直接加载的类名

	public HotSwapClassLoader(String baseDir, String[] clazzNs)
			throws Exception {
		super(null); // 指定父类加载器为 null
		this.baseDir = baseDir;
		dynaClazzNs = new HashSet();
		loadClassByMe(clazzNs);
	}

	private void loadClassByMe(String[] clazzNs) throws Exception {
		for (int i = 0; i < clazzNs.length; i++) {
			loadDirectly(clazzNs[i]);
			dynaClazzNs.add(clazzNs[i]);
		}
	}

	private Class loadDirectly(String name) throws Exception {
		Class cls = null;
		StringBuffer sb = new StringBuffer(baseDir);
		String classname = name.replace('.', File.separatorChar) + ".class";
		sb.append(File.separator + classname);
		File classF = new File(sb.toString());
		cls = instantiateClass(name, new FileInputStream(classF), classF
				.length());
		return cls;
	}

	private Class instantiateClass(String name, InputStream fin, long len)
			throws IOException {
		byte[] raw = new byte[(int) len];
		fin.read(raw);
		fin.close();
		return defineClass(name, raw, 0, raw.length);
	}

	@Override
	protected Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class cls = null;
		cls = findLoadedClass(name);
		if (!this.dynaClazzNs.contains(name) && cls == null)
			cls = getSystemClassLoader().loadClass(name);
		if (cls == null)
			throw new ClassNotFoundException(name);
		if (resolve)
			resolveClass(cls);
		return cls;
	}
	
	/*
	public class Foo{ 
	    public void sayHello() { 
	        System.out.println("hello world2! (version one)"); 
	    } 
	} 	 
	*/
	public static void main(String[] args) throws Exception {
		new Thread(new Runnable() {

			public void run() {
				try {
					while (true) {
						// 每次都创建出一个新的类加载器
						HotSwapClassLoader cl = new HotSwapClassLoader("./",
								new String[] { "Foo" });
						Class cls = cl.loadClass("Foo");
						Object foo = cls.newInstance();

						Method m = foo.getClass().getMethod("sayHello",
								new Class[] {});
						m.invoke(foo, new Object[] {});
						Thread.sleep(1000);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
		Thread.currentThread().join();
	}
}