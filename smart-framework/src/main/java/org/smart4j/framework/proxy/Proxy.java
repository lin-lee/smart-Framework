package org.smart4j.framework.proxy;

/**
 * 代理接口
 * @author lin
 *
 */
public interface Proxy {
	
	Object doProxy(ProxyChain proxyChain) throws Throwable;

}
