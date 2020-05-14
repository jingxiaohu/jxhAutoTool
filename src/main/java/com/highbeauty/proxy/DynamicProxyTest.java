/**  
 * @Title: DynamicProxyTest.java
 * @Package com.highbeauty.proxy
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 敬小虎  
 * @date 2015年12月1日 下午3:16:20
 * @version V1.0  
 */
package com.highbeauty.proxy;

import java.lang.reflect.Proxy;

/**
 * @ClassName: DynamicProxyTest
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 敬小虎
 * @date 2015年12月1日 下午3:16:20
 *
 */
public class DynamicProxyTest {
	
	public static void main(String[] args) {
		// 创建具体类ClassB的处理对象
		Invoker invoker_A = new Invoker(new ClassA());
		// 获得具体类ClassA的代理
		AbstractClass ac1 = (AbstractClass) Proxy.newProxyInstance(
				AbstractClass.class.getClassLoader(),
				new Class[] { AbstractClass.class }, invoker_A);
		// 调用ClassA的show方法。
		ac1.show();

		// 创建具体类ClassB的处理对象
		Invoker invoker_B = new Invoker(new ClassB());
		// 获得具体类ClassB的代理
		AbstractClass ac2 = (AbstractClass) Proxy.newProxyInstance(
				AbstractClass.class.getClassLoader(),
				new Class[] { AbstractClass.class }, invoker_B);
		// 调用ClassB的show方法。
		ac2.show();

	}
}
