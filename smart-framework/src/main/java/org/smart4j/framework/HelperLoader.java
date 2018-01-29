package org.smart4j.framework;

import org.smart4j.framework.helper.AopHelper;
import org.smart4j.framework.helper.BeanHelper;
import org.smart4j.framework.helper.ClassHelper;
import org.smart4j.framework.helper.ControllerHelper;
import org.smart4j.framework.helper.IocHelper;
import org.smart4j.framework.util.ClassUtil;

public final class HelperLoader {
	
	public static void init(){
		Class<?>[] classList = {
				ClassHelper.class,
				BeanHelper.class,
				AopHelper.class,//AopHelper要做IocHelper之前加载，因为需要通过AopHelper获取
				IocHelper.class,//代理对象，然后才能通过IocHelper进行依赖注入
				ControllerHelper.class
		};
		for(Class<?> cls : classList){
			ClassUtil.loadClass(cls.getName());
		}
	}
}
