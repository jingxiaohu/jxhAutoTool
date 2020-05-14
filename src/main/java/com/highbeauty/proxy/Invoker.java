/**  
* @Title: Invoker.java
* @Package com.highbeauty.proxy
* @Description: TODO(用一句话描述该文件做什么)
* @author 敬小虎  
* @date 2015年12月1日 下午3:14:21
* @version V1.0  
*/ 
package com.highbeauty.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @ClassName: Invoker
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 敬小虎
 * @date 2015年12月1日 下午3:14:21
 *
 */
public class Invoker implements InvocationHandler {
	AbstractClass ac;  
	  
    public Invoker(AbstractClass ac) {  
        this.ac = ac;  
    }  
  

	/* (非 Javadoc)
	 * <p>Title: invoke</p>
	 * <p>Description: </p>
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return
	 * @throws Throwable
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] arg)
			throws Throwable {
		// TODO Auto-generated method stub
		//调用之前可以做一些处理  
        method.invoke(ac, arg);  
        //调用之后也可以做一些处理  
        return null;  
	}

}
